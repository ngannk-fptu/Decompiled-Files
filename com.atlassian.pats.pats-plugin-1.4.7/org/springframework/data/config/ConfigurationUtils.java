/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.xml.XmlReaderContext
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 */
package org.springframework.data.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

public interface ConfigurationUtils {
    public static ResourceLoader getRequiredResourceLoader(XmlReaderContext context) {
        Assert.notNull((Object)context, (String)"XmlReaderContext must not be null!");
        ResourceLoader resourceLoader = context.getResourceLoader();
        if (resourceLoader == null) {
            throw new IllegalArgumentException("Could not obtain ResourceLoader from XmlReaderContext!");
        }
        return resourceLoader;
    }

    public static ClassLoader getRequiredClassLoader(XmlReaderContext context) {
        return ConfigurationUtils.getRequiredClassLoader(ConfigurationUtils.getRequiredResourceLoader(context));
    }

    public static ClassLoader getRequiredClassLoader(ResourceLoader resourceLoader) {
        Assert.notNull((Object)resourceLoader, (String)"ResourceLoader must not be null!");
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            throw new IllegalArgumentException("Could not obtain ClassLoader from ResourceLoader!");
        }
        return classLoader;
    }

    public static String getRequiredBeanClassName(BeanDefinition beanDefinition) {
        Assert.notNull((Object)beanDefinition, (String)"BeanDefinition must not be null!");
        String result = beanDefinition.getBeanClassName();
        if (result == null) {
            throw new IllegalArgumentException(String.format("Could not obtain required bean class name from BeanDefinition!", beanDefinition));
        }
        return result;
    }
}

