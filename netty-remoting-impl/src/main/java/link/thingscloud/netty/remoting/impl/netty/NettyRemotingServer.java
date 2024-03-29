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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import link.thingscloud.netty.remoting.api.AsyncHandler;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.channel.RemotingChannel;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.exception.RemotingCodecException;
import link.thingscloud.netty.remoting.config.RemotingServerConfig;
import link.thingscloud.netty.remoting.external.ThreadUtils;
import link.thingscloud.netty.remoting.impl.channel.NettyChannelImpl;
import link.thingscloud.netty.remoting.impl.command.CodecHelper;
import link.thingscloud.netty.remoting.impl.netty.handler.BinaryWebSocketFrameDecoder;
import link.thingscloud.netty.remoting.impl.netty.handler.BinaryWebSocketFrameEncoder;
import link.thingscloud.netty.remoting.impl.netty.handler.Decoder;
import link.thingscloud.netty.remoting.impl.netty.handler.Encoder;
import link.thingscloud.netty.remoting.internal.JvmUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class NettyRemotingServer extends NettyRemotingAbstract implements RemotingServer {
    private final RemotingServerConfig serverConfig;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup ioGroup;
    private final EventExecutorGroup workerGroup;
    private final Class<? extends ServerSocketChannel> socketChannelClass;

    private int port;

    public NettyRemotingServer(final RemotingServerConfig serverConfig) {
        super(serverConfig);

        this.serverBootstrap = new ServerBootstrap();
        this.serverConfig = serverConfig;

        if (JvmUtils.isLinux() && this.serverConfig.isServerNativeEpollEnable()) {
            this.ioGroup = new EpollEventLoopGroup(serverConfig.getServerIoThreads(), ThreadUtils.newGenericThreadFactory("NettyEpollIoThreads", serverConfig.getServerIoThreads()));
            this.bossGroup = new EpollEventLoopGroup(serverConfig.getServerAcceptorThreads(), ThreadUtils.newGenericThreadFactory("NettyBossThreads", serverConfig.getServerAcceptorThreads()));
            this.socketChannelClass = EpollServerSocketChannel.class;
        } else {
            this.bossGroup = new NioEventLoopGroup(serverConfig.getServerAcceptorThreads(), ThreadUtils.newGenericThreadFactory("NettyBossThreads", serverConfig.getServerAcceptorThreads()));
            this.ioGroup = new NioEventLoopGroup(serverConfig.getServerIoThreads(), ThreadUtils.newGenericThreadFactory("NettyNioIoThreads", serverConfig.getServerIoThreads()));
            this.socketChannelClass = NioServerSocketChannel.class;
        }

        this.workerGroup = new DefaultEventExecutorGroup(serverConfig.getServerWorkerThreads(), ThreadUtils.newGenericThreadFactory("NettyWorkerThreads", serverConfig.getServerWorkerThreads()));
    }

    @Override
    public void start() {
        super.start();

        this.serverBootstrap.group(this.bossGroup, this.ioGroup).
                channel(socketChannelClass).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(workerGroup, new HandshakeHandler());
                    }
                });

        applyOptions(serverBootstrap);

        ChannelFuture channelFuture = this.serverBootstrap.bind(this.serverConfig.getServerListenPort()).syncUninterruptibly();
        this.port = ((InetSocketAddress) channelFuture.channel().localAddress()).getPort();
    }

    @Override
    public void stop() {
        try {
            this.bossGroup.shutdownGracefully(serverConfig.getRemotingShutdownQuietPeriodMillis(), serverConfig.getRemotingShutdownTimeoutMillis(), TimeUnit.MILLISECONDS).sync();
            this.ioGroup.shutdownGracefully(serverConfig.getRemotingShutdownQuietPeriodMillis(), serverConfig.getRemotingShutdownTimeoutMillis(), TimeUnit.MILLISECONDS).sync();
            this.workerGroup.shutdownGracefully(serverConfig.getRemotingShutdownQuietPeriodMillis(), serverConfig.getRemotingShutdownTimeoutMillis(), TimeUnit.MILLISECONDS).sync();
        } catch (Exception e) {
            LOG.warn("RemotingServer stopped error !", e);
        }

        super.stop();
    }

    @Override
    public int localListenPort() {
        return this.port;
    }

    @Override
    public RemotingCommand invoke(final RemotingChannel remotingChannel, final RemotingCommand request, final long timeoutMillis) {
        return invokeWithInterceptor(((NettyChannelImpl) remotingChannel).getChannel(), request, timeoutMillis);
    }

    @Override
    public void invokeAsync(final RemotingChannel remotingChannel, final RemotingCommand request, final AsyncHandler asyncHandler, final long timeoutMillis) {
        invokeAsyncWithInterceptor(((NettyChannelImpl) remotingChannel).getChannel(), request, asyncHandler, timeoutMillis);
    }

    @Override
    public void invokeOneWay(final RemotingChannel remotingChannel, final RemotingCommand request) {
        invokeOnewayWithInterceptor(((NettyChannelImpl) remotingChannel).getChannel(), request);
    }

    private class ServerConnectionHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Channel {} registered, remote address {}.", ctx.channel(), ctx.channel().remoteAddress());
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Channel {} unregistered, remote address {}.", ctx.channel(), ctx.channel().remoteAddress());
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Channel {} became active, remote address {}.", ctx.channel(), ctx.channel().remoteAddress());
            super.channelActive(ctx);
            putNettyEvent(new NettyChannelEvent(NettyChannelEventType.CONNECT, ctx.channel()));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Channel {} became inactive, remote address {}.", ctx.channel(), ctx.channel().remoteAddress());
            super.channelInactive(ctx);
            putNettyEvent(new NettyChannelEvent(NettyChannelEventType.CLOSE, ctx.channel()));
        }

        @Override
        public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) {
            if (evt instanceof IdleStateEvent) {
                final IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    ctx.channel().close().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            LOG.warn("Close channel {} because of event {},result is {}", ctx.channel(), event, future.isSuccess());
                        }
                    });

                    putNettyEvent(new NettyChannelEvent(NettyChannelEventType.IDLE, ctx.channel()));
                }
            }
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
            LOG.info("Close channel {} because of error ", ctx.channel(), cause);
            putNettyEvent(new NettyChannelEvent(NettyChannelEventType.EXCEPTION, ctx.channel(), cause));
            ctx.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOG.warn("Close channel {} because of error {},result is {}", ctx.channel(), cause, future.isSuccess());
                }
            });
        }
    }


    public class HandshakeHandler extends ByteToMessageDecoder {

        public HandshakeHandler() {
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
            try {
                // Peek the current read index byte to determine if the content is starting with TLS handshake
                byte magic = byteBuf.getByte(byteBuf.readerIndex());
                if (magic == CodecHelper.PROTOCOL_MAGIC) {
                    ctx.pipeline().addLast(workerGroup,
                            new Encoder(),
                            new Decoder(),
                            new IdleStateHandler(serverConfig.getConnectionChannelReaderIdleSeconds(), serverConfig.getConnectionChannelWriterIdleSeconds(), serverConfig.getConnectionChannelIdleSeconds()),
                            new ServerConnectionHandler(),
                            new RemotingCommandDispatcher());
                } else if (magic == CodecHelper.PROTOCOL_MAGIC_G) {
                    // GET / HTTP/1.1
                    ctx.pipeline().addLast(workerGroup,
                            new HttpServerCodec(),
                            new HttpObjectAggregator(65536),
                            new WebSocketServerCompressionHandler(),
                            new WebSocketServerProtocolHandler("/netty/remoting", null, true),
                            new BinaryWebSocketFrameDecoder(),
                            new BinaryWebSocketFrameEncoder(),
                            new IdleStateHandler(serverConfig.getConnectionChannelReaderIdleSeconds(), serverConfig.getConnectionChannelWriterIdleSeconds(), serverConfig.getConnectionChannelIdleSeconds()),
                            new ServerConnectionHandler(),
                            new RemotingCommandDispatcher());
                } else {
                    throw new RemotingCodecException(String.format("MagicCode %d is wrong, expect %d or %d", magic, CodecHelper.PROTOCOL_MAGIC, CodecHelper.PROTOCOL_MAGIC_G));
                }
            } catch (Exception e) {

                LOG.error("process multi protocol negotiator failed.", e);
                throw e;
            }
            try {
                // Remove this handler
                ctx.pipeline().remove(this);
            } catch (NoSuchElementException e) {
                LOG.error("Error while removing HandshakeHandler", e);
            }
            ctx.fireChannelRead(byteBuf.retain());
        }
    }

    private void applyOptions(ServerBootstrap bootstrap) {
        //option() is for the NioServerSocketChannel that accepts incoming connections.
        //childOption() is for the Channels accepted by the parent ServerChannel, which is NioServerSocketChannel in this case
        if (null != serverConfig) {
            if (serverConfig.getTcpSoBacklogSize() > 0) {
                bootstrap.option(ChannelOption.SO_BACKLOG, serverConfig.getTcpSoBacklogSize());
            }

            if (serverConfig.getTcpSoLinger() > 0) {
                bootstrap.option(ChannelOption.SO_LINGER, serverConfig.getTcpSoLinger());
            }

            if (serverConfig.getTcpSoSndBufSize() > 0) {
                bootstrap.childOption(ChannelOption.SO_SNDBUF, serverConfig.getTcpSoSndBufSize());
            }
            if (serverConfig.getTcpSoRcvBufSize() > 0) {
                bootstrap.childOption(ChannelOption.SO_RCVBUF, serverConfig.getTcpSoRcvBufSize());
            }

            bootstrap.option(ChannelOption.SO_REUSEADDR, serverConfig.isTcpSoReuseAddress()).
                    childOption(ChannelOption.SO_KEEPALIVE, serverConfig.isTcpSoKeepAlive()).
                    childOption(ChannelOption.TCP_NODELAY, serverConfig.isTcpSoNoDelay()).
                    option(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverConfig.getTcpSoTimeoutMillis());

            if (serverConfig.isServerPooledBytebufAllocatorEnable()) {
                bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            }
        }
    }

}
