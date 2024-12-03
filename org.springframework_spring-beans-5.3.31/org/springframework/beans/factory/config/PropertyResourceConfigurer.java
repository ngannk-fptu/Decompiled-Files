/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.io.support.PropertiesLoaderSupport
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.util.ObjectUtils;

public abstract class PropertyResourceConfigurer
extends PropertiesLoaderSupport
implements BeanFactoryPostProcessor,
PriorityOrdered {
    private int order = Integer.MAX_VALUE;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            Properties mergedProps = this.mergeProperties();
            this.convertProperties(mergedProps);
            this.processProperties(beanFactory, mergedProps);
        }
        catch (IOException ex) {
            throw new BeanInitializationException("Could not load properties", ex);
        }
    }

    protected void convertProperties(Properties props) {
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String convertedValue;
            String propertyName = (String)propertyNames.nextElement();
            String propertyValue = props.getProperty(propertyName);
            if (ObjectUtils.nullSafeEquals((Object)propertyValue, (Object)(convertedValue = this.convertProperty(propertyName, propertyValue)))) continue;
            props.setProperty(propertyName, convertedValue);
        }
    }

    protected String convertProperty(String propertyName, String propertyValue) {
        return this.convertPropertyValue(propertyValue);
    }

    protected String convertPropertyValue(String originalValue) {
        return originalValue;
    }

    protected abstract void processProperties(ConfigurableListableBeanFactory var1, Properties var2) throws BeansException;
}

