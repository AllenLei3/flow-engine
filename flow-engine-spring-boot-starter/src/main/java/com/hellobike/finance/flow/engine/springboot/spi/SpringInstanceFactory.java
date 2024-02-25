package com.hellobike.finance.flow.engine.springboot.spi;

import com.hellobike.finance.flow.engine.spi.instance.ReflectInstanceFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author 徐磊080
 */
public class SpringInstanceFactory extends ReflectInstanceFactory {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        SpringInstanceFactory.context = context;
    }

    @Override
    public Object getInstance(Class<?> type) {
        if (context == null) {
            throw new IllegalStateException("SpringInstanceFactory ApplicationContext is NULL!");
        }
        Object bean = null;
        try {
            bean = context.getBean(type);
        } catch (Exception e) {
            // ignore
        }
        return bean != null ? bean : super.getInstance(type);
    }

    @Override
    public Object getInstance(String className) {
        if (context == null) {
            throw new IllegalStateException("SpringInstanceFactory ApplicationContext is NULL!");
        }
        // 这里的className实际为beanName
        Object bean = null;
        try {
            bean = context.getBean(className);
        } catch (Exception e) {
            // ignore
        }
        return bean != null ? bean : super.getInstance(className);
    }
}
