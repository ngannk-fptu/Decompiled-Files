/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.util.Assert;

public interface RepositoryConfigurationUtils {
    public static void exposeRegistration(RepositoryConfigurationExtension extension, BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
        Assert.notNull((Object)extension, (String)"RepositoryConfigurationExtension must not be null!");
        Assert.notNull((Object)registry, (String)"BeanDefinitionRegistry must not be null!");
        Assert.notNull((Object)configurationSource, (String)"RepositoryConfigurationSource must not be null!");
        Class<?> extensionType = extension.getClass();
        String beanName = extensionType.getName().concat("#").concat("0");
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        RootBeanDefinition definition = new RootBeanDefinition(extensionType);
        definition.setSource(configurationSource.getSource());
        definition.setRole(2);
        definition.setLazyInit(true);
        registry.registerBeanDefinition(beanName, (BeanDefinition)definition);
    }
}

