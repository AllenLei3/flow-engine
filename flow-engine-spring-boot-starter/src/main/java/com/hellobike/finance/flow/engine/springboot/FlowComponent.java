package com.hellobike.finance.flow.engine.springboot;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识流程节点, 方便进行统一处理
 *
 * @author xulei
 */
@Component
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowComponent {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
