/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.UserLocaleAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UserLocaleAwareBeanPostProcessor
implements BeanPostProcessor,
ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof UserLocaleAware) {
            I18NBeanFactory i18NBeanFactory = (I18NBeanFactory)this.applicationContext.getBean("userI18NBeanFactory");
            ((UserLocaleAware)bean).setI18NBeanFactory(i18NBeanFactory);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}

