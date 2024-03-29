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

package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.RemotingBootstrapFactory;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.RequestProcessor;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.impl.netty.NettyRemotingServer;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingType;
import link.thingscloud.netty.remoting.spring.boot.starter.properties.RemotingServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author zhouhailin
 * @since 0.5.0
 */
@Configuration
@EnableConfigurationProperties({RemotingServerProperties.class})
@ConditionalOnClass(RemotingServer.class)
public class RemotingServerAutoConfiguration extends AbstractRemotingAutoConfiguration {

    @Autowired(required = false)
    private List<RequestProcessor> requestProcessors = Collections.emptyList();

    protected static final Logger LOG = LoggerFactory.getLogger(RemotingServerAutoConfiguration.class);

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemotingServer remotingServer(RemotingServerProperties properties) {
        NettyRemotingServer remotingServer = RemotingBootstrapFactory.createRemotingServer(properties);
        registerInterceptor(remotingServer, RemotingType.SERVER);
        registerRequestProcessor(remotingServer, RemotingType.CLIENT);
        registerChannelEventListener(remotingServer, RemotingType.CLIENT);
        return remotingServer;
    }

    @Bean
    @ConditionalOnMissingBean(RemotingCommandFactory.class)
    public RemotingCommandFactory remotingCommandFactory() {
        return new RemotingCommandFactoryImpl();
    }
}
