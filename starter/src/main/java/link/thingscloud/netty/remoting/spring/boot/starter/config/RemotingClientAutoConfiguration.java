package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.RemotingBootstrapFactory;
import link.thingscloud.netty.remoting.api.RemotingClient;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.spring.boot.starter.properties.RemotingClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RemotingClientProperties.class})
@ConditionalOnClass(RemotingClient.class)
public class RemotingClientAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemotingClient remotingClient(RemotingClientProperties properties) {
        return RemotingBootstrapFactory.createRemotingClient(properties);
    }

    @Bean
    @ConditionalOnMissingBean(RemotingCommandFactory.class)
    public RemotingCommandFactory remotingCommandFactory() {
        return new RemotingCommandFactoryImpl();
    }
}
