/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package link.thingscloud.netty.remoting.impl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import link.thingscloud.netty.remoting.config.RemotingClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static link.thingscloud.netty.remoting.internal.RemotingUtil.extractRemoteAddress;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class ClientChannelManager {
    protected static final Logger LOG = LoggerFactory.getLogger(ClientChannelManager.class);

    private static final long LOCK_TIMEOUT_MILLIS = 3000;
    final ConcurrentHashMap<String, RemotingChannelFuture> channelTables = new ConcurrentHashMap<>();
    private final Lock lockChannelTables = new ReentrantLock();
    private final Bootstrap clientBootstrap;
    private final RemotingClientConfig clientConfig;

    ClientChannelManager(final Bootstrap bootstrap,
                         final RemotingClientConfig config) {
        clientBootstrap = bootstrap;
        clientConfig = config;
    }

    void clear() {
        for (RemotingChannelFuture cw : this.channelTables.values()) {
            this.closeChannel(null, cw.getChannel());
        }

        this.channelTables.clear();
    }

    Channel createIfAbsent(final String addr) {
        RemotingChannelFuture cw = this.channelTables.get(addr);
        if (cw != null && cw.isActive()) {
            return cw.getChannel();
        }
        return this.createChannel(addr);
    }

    private Channel createChannel(final String addr) {
        RemotingChannelFuture cw = null;
        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean createNewConnection;
                    cw = this.channelTables.get(addr);
                    if (cw != null) {
                        if (cw.isActive()) {
                            return cw.getChannel();
                        } else if (!cw.getChannelFuture().isDone()) {
                            createNewConnection = false;
                        } else {
                            this.channelTables.remove(addr);
                            createNewConnection = true;
                        }
                    } else {
                        createNewConnection = true;
                    }

                    if (createNewConnection) {
                        String[] s = addr.split(":");
                        SocketAddress socketAddress = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
                        ChannelFuture channelFuture = this.clientBootstrap.connect(socketAddress);
                        LOG.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                        cw = new RemotingChannelFuture(channelFuture);
                        this.channelTables.put(addr, cw);
                    }
                } catch (Exception e) {
                    LOG.error("createChannel: create channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                LOG.warn("createChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException ignore) {
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(this.clientConfig.getConnectTimeoutMillis())) {
                if (cw.isActive()) {
                    LOG.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    LOG.warn("createChannel: connect remote host[" + addr + "] failed, and destroy the channel" + channelFuture.toString(), channelFuture.cause());
                    this.closeChannel(addr, cw.getChannel());
                }
            } else {
                LOG.warn("createChannel: connect remote host[{}] timeout {}ms, {}, and destroy the channel", addr, this.clientConfig.getConnectTimeoutMillis(),
                        channelFuture.toString());
                this.closeChannel(addr, cw.getChannel());
            }
        }
        return null;
    }

    void closeChannel(final String addr, final Channel channel) {
        final String remoteAddr = null == addr ? extractRemoteAddress(channel) : addr;
        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    RemotingChannelFuture prevCW = this.channelTables.get(remoteAddr);
                    //Workaround for null
                    if (null == prevCW) {
                        return;
                    }

                    LOG.info("Begin to close the remote address {} channel {}", remoteAddr, prevCW);

                    if (prevCW.getChannel() != channel) {
                        LOG.info("Channel {} has been closed,this is a new channel {}", prevCW.getChannel(), channel);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(remoteAddr);
                        LOG.info("Channel {} has been removed !", remoteAddr);
                    }

                    channel.close().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            LOG.warn("Close channel {} {}", channel, future.isSuccess());
                        }
                    });
                } catch (Exception e) {
                    LOG.error("Close channel error !", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                LOG.warn("Can not lock channel table in {} ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            LOG.error("Close channel error !", e);
        }
    }

    void closeChannel(final Channel channel) {
        try {
            if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    RemotingChannelFuture prevCW = null;
                    String remoteAddr = null;

                    for (Map.Entry<String, RemotingChannelFuture> entry : channelTables.entrySet()) {
                        RemotingChannelFuture prev = entry.getValue();
                        if (prev.getChannel() != null) {
                            if (prev.getChannel() == channel) {
                                prevCW = prev;
                                remoteAddr = entry.getKey();
                                break;
                            }
                        }
                    }

                    if (null == prevCW) {
                        LOG.info("eventCloseChannel: the channel[{}] has been removed from the channel table before", remoteAddr);
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(remoteAddr);
                        LOG.info("closeChannel: the channel[{}] was removed from channel table", remoteAddr);
                        //RemotingHelper.closeChannel(channel);
                    }
                } catch (Exception e) {
                    LOG.error("closeChannel: close the channel exception", e);
                } finally {
                    this.lockChannelTables.unlock();
                }
            } else {
                LOG.warn("closeChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            LOG.error("closeChannel exception", e);
        }
    }

    private class RemotingChannelFuture {
        private final ChannelFuture channelFuture;

        RemotingChannelFuture(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        boolean isActive() {
            return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
        }

        boolean isWriteable() {
            return this.channelFuture.channel().isWritable();
        }

        private Channel getChannel() {
            return this.channelFuture.channel();
        }

        ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }
}
