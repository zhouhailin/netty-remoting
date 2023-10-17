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

package link.thingscloud.netty.remoting;

import link.thingscloud.netty.remoting.api.RemotingClient;
import link.thingscloud.netty.remoting.config.RemotingClientConfig;
import link.thingscloud.netty.remoting.config.RemotingServerConfig;
import link.thingscloud.netty.remoting.impl.netty.NettyRemotingClient;
import link.thingscloud.netty.remoting.impl.netty.NettyRemotingServer;
import link.thingscloud.netty.remoting.internal.BeanUtils;
import link.thingscloud.netty.remoting.internal.PropertyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

/**
 * Remoting Bootstrap entrance.
 *
 * @author zhouhailin
 * @since 0.5.0
 */
public final class RemotingBootstrapFactory {
    public static RemotingClient createRemotingClient(@NotNull final RemotingClientConfig config) {
        return new NettyRemotingClient(config);
    }

    public static RemotingClient createRemotingClient(@NotNull final String fileName) {
        Properties prop = PropertyUtils.loadProps(fileName);
        RemotingClientConfig config = BeanUtils.populate(prop, RemotingClientConfig.class);
        return new NettyRemotingClient(config);
    }

    public static RemotingClient createRemotingClient(@NotNull final Properties properties) {
        RemotingClientConfig config = BeanUtils.populate(properties, RemotingClientConfig.class);
        return new NettyRemotingClient(config);
    }

    public static NettyRemotingServer createRemotingServer(@NotNull final String fileName) {
        Properties prop = PropertyUtils.loadProps(fileName);
        RemotingServerConfig config = BeanUtils.populate(prop, RemotingServerConfig.class);
        return new NettyRemotingServer(config);
    }

    public static NettyRemotingServer createRemotingServer(@NotNull final Properties properties) {
        RemotingServerConfig config = BeanUtils.populate(properties, RemotingServerConfig.class);
        return new NettyRemotingServer(config);
    }

    public static NettyRemotingServer createRemotingServer(@NotNull final RemotingServerConfig config) {
        return new NettyRemotingServer(config);
    }
}
