package link.thingscloud.netty.benchmarks.remoting;

import link.thingscloud.netty.remoting.api.RemotingClient;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.impl.command.RemotingCommandFactoryImpl;
import link.thingscloud.netty.remoting.spring.boot.starter.EnableRemotingClientAutoConfiguration;
import link.thingscloud.netty.remoting.spring.boot.starter.EnableRemotingServerAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author : zhouhailin
 * @version 0.5.0
 */
@EnableRemotingClientAutoConfiguration
@EnableRemotingServerAutoConfiguration
@SpringBootApplication
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Autowired
    RemotingClient remotingClient;
    @Autowired
    RemotingServer remotingServer;
    @Autowired
    RemotingCommandFactory factory;

    @PostConstruct
    public void start() {
        remotingClient.invokeOneWay("127.0.0.1:9888", factory.createRequest());
    }
}
