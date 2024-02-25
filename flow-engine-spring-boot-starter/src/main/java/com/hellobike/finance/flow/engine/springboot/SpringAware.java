package com.hellobike.finance.flow.engine.springboot;

import com.hellobike.finance.flow.engine.springboot.spi.SpringInstanceFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 基于代码形式的spring上下文工具类
 *
 * @author 徐磊080
 */
public class SpringAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
        SpringInstanceFactory.setContext(applicationContext);
    }

    public <T> T getBean(String name) {
        T t = (T) applicationContext.getBean(name);
        return t;
    }

    public <T> T getBean(Class<T> clazz) {
        T t = applicationContext.getBean(clazz);
        return t;
    }

    private <T> T getBean(String beanName, Class<T> clazz) {
        T t = applicationContext.getBean(beanName, clazz);
        return t;
    }

}
