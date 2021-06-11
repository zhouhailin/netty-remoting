package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.RemotingBootstrapFactory;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.RemotingService;
import link.thingscloud.netty.remoting.api.RequestProcessor;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.impl.netty.NettyRemotingAbstract;
import link.thingscloud.netty.remoting.impl.netty.NettyRemotingServer;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingRequestProcessor;
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

@Configuration
@EnableConfigurationProperties({RemotingServerProperties.class})
@ConditionalOnClass(RemotingServer.class)
public class RemotingServerAutoConfiguration extends AbstractRemotingAutoConfiguration{

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
