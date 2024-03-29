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

package link.thingscloud.netty.remoting.impl.channel;

import io.netty.channel.Channel;
import link.thingscloud.netty.remoting.api.channel.ChunkRegion;
import link.thingscloud.netty.remoting.api.channel.RemotingChannel;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;

import java.net.SocketAddress;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class NettyChannelImpl implements RemotingChannel {
    private final io.netty.channel.Channel channel;

    public NettyChannelImpl(Channel channel) {
        this.channel = channel;
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public boolean isWritable() {
        return channel.isWritable();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void reply(final RemotingCommand command) {
        channel.writeAndFlush(command);
    }

    @Override
    public void reply(final ChunkRegion fileRegion) {
        channel.writeAndFlush(fileRegion);
    }

    public io.netty.channel.Channel getChannel() {
        return channel;
    }

    @Override
    public int hashCode() {
        return channel != null ? channel.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final NettyChannelImpl that = (NettyChannelImpl) o;

        return channel != null ? channel.equals(that.channel) : that.channel == null;

    }

    @Override
    public String toString() {
        return "NettyChannelImpl [channel=" + channel + "]";
    }
}
