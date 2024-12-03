/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.spi.HostContextAccessor
 *  com.atlassian.sal.spi.HostContextAccessor$HostTransactionCallback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.support.AbstractApplicationContext
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.sal.spring.component;

import com.atlassian.sal.spi.HostContextAccessor;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringHostContextAccessor
implements HostContextAccessor,
ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final PlatformTransactionManager transactionManager;
    private static final Logger log = LoggerFactory.getLogger(SpringHostContextAccessor.class);

    public SpringHostContextAccessor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public <T> Map<String, T> getComponentsOfType(Class<T> iface) {
        try {
            return this.applicationContext.getBeansOfType(iface);
        }
        catch (RuntimeException ex) {
            AbstractApplicationContext abstractApplicationContext;
            ConfigurableListableBeanFactory beanFactory;
            log.debug("Can't get beans", (Throwable)ex);
            HashMap<String, Object> results = new HashMap<String, Object>();
            if (this.applicationContext instanceof AbstractApplicationContext && (beanFactory = (abstractApplicationContext = (AbstractApplicationContext)this.applicationContext).getBeanFactory()) instanceof DefaultListableBeanFactory) {
                String[] beanDefinitionNames;
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)beanFactory;
                for (String beanName : beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames()) {
                    try {
                        Object bean = defaultListableBeanFactory.getBean(beanName);
                        if (bean == null || !iface.isAssignableFrom(bean.getClass())) continue;
                        results.put(beanName, bean);
                    }
                    catch (BeansException beansException) {
                        // empty catch block
                    }
                }
            }
            return results;
        }
    }

    public Object doInTransaction(HostContextAccessor.HostTransactionCallback callback) {
        TransactionTemplate txTemplate = new TransactionTemplate(this.transactionManager, (TransactionDefinition)this.getTransactionDefinition());
        return txTemplate.execute(transactionStatus -> {
            try {
                return callback.doInTransaction();
            }
            catch (RuntimeException e) {
                transactionStatus.setRollbackOnly();
                throw e;
            }
        });
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected DefaultTransactionDefinition getTransactionDefinition() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("PluginReadWriteTx");
        def.setPropagationBehavior(0);
        def.setReadOnly(false);
        return def;
    }
}

