package link.thingscloud.netty.remoting.spring.boot.starter.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author : zhouhailin
 * @version 0.5.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RemotingInterceptor {

    @AliasFor(annotation = Component.class)
    String value() default "";

    RemotingType type() default RemotingType.BOTH;

}
