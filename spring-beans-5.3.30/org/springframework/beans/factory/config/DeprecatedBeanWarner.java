/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class DeprecatedBeanWarner
implements BeanFactoryPostProcessor {
    protected transient Log logger = LogFactory.getLog(this.getClass());

    public void setLoggerName(String loggerName) {
        this.logger = LogFactory.getLog((String)loggerName);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.isLogEnabled()) {
            String[] beanNames;
            String[] stringArray = beanNames = beanFactory.getBeanDefinitionNames();
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                Class userClass;
                Class<?> beanType;
                String beanName;
                String nameToLookup = beanName = stringArray[i];
                if (beanFactory.isFactoryBean(beanName)) {
                    nameToLookup = "&" + beanName;
                }
                if ((beanType = beanFactory.getType(nameToLookup)) == null || !(userClass = ClassUtils.getUserClass(beanType)).isAnnotationPresent(Deprecated.class)) continue;
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                this.logDeprecatedBean(beanName, beanType, beanDefinition);
            }
        }
    }

    protected void logDeprecatedBean(String beanName, Class<?> beanType, BeanDefinition beanDefinition) {
        StringBuilder builder = new StringBuilder();
        builder.append(beanType);
        builder.append(" ['");
        builder.append(beanName);
        builder.append('\'');
        String resourceDescription = beanDefinition.getResourceDescription();
        if (StringUtils.hasLength((String)resourceDescription)) {
            builder.append(" in ");
            builder.append(resourceDescription);
        }
        builder.append("] has been deprecated");
        this.writeToLog(builder.toString());
    }

    protected void writeToLog(String message) {
        this.logger.warn((Object)message);
    }

    protected boolean isLogEnabled() {
        return this.logger.isWarnEnabled();
    }
}

