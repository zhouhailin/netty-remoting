package link.thingscloud.netty.remoting.spring.boot.starter.config;

import link.thingscloud.netty.remoting.api.ConnectionService;
import link.thingscloud.netty.remoting.api.RemotingService;
import link.thingscloud.netty.remoting.api.RequestProcessor;
import link.thingscloud.netty.remoting.api.channel.ChannelEventListener;
import link.thingscloud.netty.remoting.api.interceptor.Interceptor;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingChannelEventListener;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingInterceptor;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingRequestProcessor;
import link.thingscloud.netty.remoting.spring.boot.starter.annotation.RemotingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * @author : zhouhailin
 * @version 0.5.0
 */
public abstract class AbstractRemotingAutoConfiguration {

    @Autowired(required = false)
    private List<Interceptor> interceptors = Collections.emptyList();
    @Autowired(required = false)
    private List<RequestProcessor> requestProcessors = Collections.emptyList();
    @Autowired(required = false)
    private List<ChannelEventListener> channelEventListeners = Collections.emptyList();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void registerRequestProcessor(RemotingService remotingService, RemotingType type) {
        requestProcessors.forEach(requestProcessor -> {
            RemotingRequestProcessor annotation = requestProcessor.getClass().getAnnotation(RemotingRequestProcessor.class);
            if (annotation != null && annotation.type() != type) {
                log.info("{} registerRequestProcessor code : {}, class : {}", remotingService.getClass().getSimpleName(), annotation.code(), requestProcessor.getClass().getName());
                remotingService.registerRequestProcessor(annotation.code(), requestProcessor);
            }
        });
    }

    public void registerInterceptor(RemotingService remotingService, RemotingType type) {
        interceptors.forEach(interceptor -> {
            RemotingInterceptor annotation = interceptor.getClass().getAnnotation(RemotingInterceptor.class);
            if (annotation != null && annotation.type() != type) {
                log.info("{} registerInterceptor {}", remotingService.getClass().getSimpleName(), interceptor.getClass().getName());
                remotingService.registerInterceptor(interceptor);
            }
        });
    }

    public void registerChannelEventListener(ConnectionService connectionService, RemotingType type) {
        channelEventListeners.forEach(listener -> {
            RemotingChannelEventListener annotation = listener.getClass().getAnnotation(RemotingChannelEventListener.class);
            if (annotation != null && annotation.type() != type) {
                log.info("{} registerChannelEventListener {}", listener.getClass().getSimpleName(), listener.getClass().getName());
                connectionService.registerChannelEventListener(listener);
            }
        });
    }
}
