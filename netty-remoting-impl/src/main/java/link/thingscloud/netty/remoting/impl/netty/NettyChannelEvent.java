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

import io.netty.channel.Channel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
public class NettyChannelEvent {
    private final Channel channel;
    private final NettyChannelEventType type;
    private final Throwable cause;

    public NettyChannelEvent(NettyChannelEventType type, Channel channel) {
        this(type, channel, null);
    }

    public NettyChannelEvent(NettyChannelEventType type, Channel channel, Throwable cause) {
        this.type = type;
        this.channel = channel;
        this.cause = cause;
    }

    public NettyChannelEventType getType() {
        return type;
    }

    public Channel getChannel() {
        return channel;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
