package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.RemotingBootstrapFactory;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.RemotingService;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.spring.boot.starter.properties.RemotingServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RemotingServerProperties.class})
@ConditionalOnClass(RemotingServer.class)
public class RemotingServerAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemotingServer remotingServer(RemotingServerProperties properties) {
        return RemotingBootstrapFactory.createRemotingServer(properties);
    }

    @Bean
    @ConditionalOnMissingBean(RemotingCommandFactory.class)
    public RemotingCommandFactory remotingCommandFactory() {
        return new RemotingCommandFactoryImpl();
    }
}
