package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.RemotingBootstrapFactory;
import link.thingscloud.netty.remoting.api.RemotingClient;
import link.thingscloud.netty.remoting.api.RequestProcessor;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingRequestProcessor;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingType;
import link.thingscloud.netty.remoting.spring.boot.starter.properties.RemotingClientProperties;
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
@EnableConfigurationProperties({RemotingClientProperties.class})
@ConditionalOnClass(RemotingClient.class)
public class RemotingClientAutoConfiguration extends AbstractRemotingAutoConfiguration{

    @Autowired(required = false)
    private List<RequestProcessor> requestProcessors = Collections.emptyList();

    protected static final Logger LOG = LoggerFactory.getLogger(RemotingClientAutoConfiguration.class);

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemotingClient remotingClient(RemotingClientProperties properties) {
        RemotingClient remotingClient = RemotingBootstrapFactory.createRemotingClient(properties);
        registerInterceptor(remotingClient, RemotingType.SERVER);
        registerRequestProcessor(remotingClient, RemotingType.SERVER);
        registerChannelEventListener(remotingClient, RemotingType.SERVER);
        return remotingClient;
    }

    @Bean
    @ConditionalOnMissingBean(RemotingCommandFactory.class)
    public RemotingCommandFactory remotingCommandFactory() {
        return new RemotingCommandFactoryImpl();
    }
}
