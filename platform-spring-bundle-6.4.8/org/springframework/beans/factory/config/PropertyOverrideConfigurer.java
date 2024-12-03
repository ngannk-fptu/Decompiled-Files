/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;

public class PropertyOverrideConfigurer
extends PropertyResourceConfigurer {
    public static final String DEFAULT_BEAN_NAME_SEPARATOR = ".";
    private String beanNameSeparator = ".";
    private boolean ignoreInvalidKeys = false;
    private final Set<String> beanNames = Collections.newSetFromMap(new ConcurrentHashMap(16));

    public void setBeanNameSeparator(String beanNameSeparator) {
        this.beanNameSeparator = beanNameSeparator;
    }

    public void setIgnoreInvalidKeys(boolean ignoreInvalidKeys) {
        this.ignoreInvalidKeys = ignoreInvalidKeys;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
        Enumeration<?> names = props.propertyNames();
        while (names.hasMoreElements()) {
            String key = (String)names.nextElement();
            try {
                this.processKey(beanFactory, key, props.getProperty(key));
            }
            catch (BeansException ex) {
                String msg = "Could not process key '" + key + "' in PropertyOverrideConfigurer";
                if (!this.ignoreInvalidKeys) {
                    throw new BeanInitializationException(msg, ex);
                }
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)msg, (Throwable)ex);
            }
        }
    }

    protected void processKey(ConfigurableListableBeanFactory factory, String key, String value) throws BeansException {
        int separatorIndex = key.indexOf(this.beanNameSeparator);
        if (separatorIndex == -1) {
            throw new BeanInitializationException("Invalid key '" + key + "': expected 'beanName" + this.beanNameSeparator + "property'");
        }
        String beanName = key.substring(0, separatorIndex);
        String beanProperty = key.substring(separatorIndex + 1);
        this.beanNames.add(beanName);
        this.applyPropertyValue(factory, beanName, beanProperty, value);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Property '" + key + "' set to value [" + value + "]"));
        }
    }

    protected void applyPropertyValue(ConfigurableListableBeanFactory factory, String beanName, String property, String value) {
        BeanDefinition bd;
        BeanDefinition bdToUse = bd = factory.getBeanDefinition(beanName);
        while (bd != null) {
            bdToUse = bd;
            bd = bd.getOriginatingBeanDefinition();
        }
        PropertyValue pv = new PropertyValue(property, (Object)value);
        pv.setOptional(this.ignoreInvalidKeys);
        bdToUse.getPropertyValues().addPropertyValue(pv);
    }

    public boolean hasPropertyOverridesFor(String beanName) {
        return this.beanNames.contains(beanName);
    }
}

