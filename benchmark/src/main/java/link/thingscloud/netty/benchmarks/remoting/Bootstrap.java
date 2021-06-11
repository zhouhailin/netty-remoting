package link.thingscloud.netty.benchmarks.remoting;

import link.thingscloud.netty.remoting.api.RemotingClient;
import link.thingscloud.netty.remoting.api.RemotingServer;
import link.thingscloud.netty.remoting.api.RequestProcessor;
import link.thingscloud.netty.remoting.api.channel.RemotingChannel;
import link.thingscloud.netty.remoting.api.command.RemotingCommand;
import link.thingscloud.netty.remoting.api.command.RemotingCommandFactory;
import link.thingscloud.netty.remoting.spring.boot.starter.EnableRemotingClientAutoConfiguration;
import link.thingscloud.netty.remoting.spring.boot.starter.EnableRemotingServerAutoConfiguration;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingRequestProcessor;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingType;
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
        RemotingCommand request = factory.createRequest();
        request.cmdCode((short) 13);
        remotingClient.invokeOneWay("127.0.0.1:9888", request);
    }

    @RemotingRequestProcessor(code = 12, type = RemotingType.CLIENT)
    class RequestProcessorImpl1 implements RequestProcessor {
        @Override
        public RemotingCommand processRequest(RemotingChannel channel, RemotingCommand request) {
            System.out.println(request);
            return null;
        }
    }
    @RemotingRequestProcessor(code = 13, type = RemotingType.SERVER)
    class RequestProcessorImpl2 implements RequestProcessor {
        @Override
        public RemotingCommand processRequest(RemotingChannel channel, RemotingCommand request) {
            System.out.println(request);
            return null;
        }
    }
    @RemotingRequestProcessor(code = 14)
    class RequestProcessorImpl3 implements RequestProcessor {
        @Override
        public RemotingCommand processRequest(RemotingChannel channel, RemotingCommand request) {
            System.out.println(request);
            return null;
        }
    }
}
