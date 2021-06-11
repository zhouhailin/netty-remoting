package link.thingscloud.netty.remoting.spring.boot.starter.properties;

import link.thingscloud.netty.remoting.config.RemotingClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : zhouhailin
 * @version 0.5.0
 */
@ConfigurationProperties(prefix = "netty.remoting.client")
public class RemotingClientProperties extends RemotingClientConfig {
}
