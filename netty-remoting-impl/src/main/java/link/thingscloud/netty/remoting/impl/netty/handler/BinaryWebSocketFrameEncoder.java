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

package link.thingscloud.netty.remoting.impl.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import link.thingscloud.netty.remoting.api.buffer.RemotingBuffer;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.exception.RemotingCodecException;
import link.thingscloud.netty.remoting.impl.buffer.NettyRemotingBuffer;
import link.thingscloud.netty.remoting.impl.command.CodecHelper;
import link.thingscloud.netty.remoting.internal.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class BinaryWebSocketFrameEncoder extends MessageToMessageEncoder<RemotingCommand> {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryWebSocketFrameEncoder.class);

    public BinaryWebSocketFrameEncoder() {
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, RemotingCommand remotingCommand, List<Object> list) throws Exception {
        try {
            ByteBuf out = Unpooled.buffer(0);

            RemotingBuffer wrapper = new NettyRemotingBuffer(out);
            encode(remotingCommand, wrapper);

            list.add(new BinaryWebSocketFrame(out));
        } catch (final RemotingCodecException e) {
            String remoteAddress = RemotingUtil.extractRemoteAddress(ctx.channel());
            LOG.error(String.format("Error occurred when encoding command for channel %s", remoteAddress), e);

            ctx.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    LOG.warn("Close channel {} because of error {}, result is {}", ctx.channel(), e, future.isSuccess());
                }
            });

        }
    }

    private void encode(final RemotingCommand remotingCommand, final RemotingBuffer out) {
        CodecHelper.encodeCommand(remotingCommand, out);
    }
}
