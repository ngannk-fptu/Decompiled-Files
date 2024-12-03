/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.RootBeanDefinition
 */
package com.atlassian.user.configuration;

import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.CacheConfiguration;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultRepositoryAccessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.configuration.RepositoryProcessor;
import com.atlassian.user.properties.PropertySetFactory;
import com.atlassian.user.util.ClassLoaderUtils;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class DefaultRepositoryProcessor
implements RepositoryProcessor {
    protected final Logger log = Logger.getLogger(this.getClass());

    public RepositoryAccessor process(RepositoryConfiguration config) throws ConfigurationException {
        DefaultListableBeanFactory beanFactory = this.createComponentContext(config);
        beanFactory.registerSingleton("identifier", (Object)config.getIdentifier());
        List<String> componentNames = Arrays.asList("propertySetFactory", "passwordEncryptor", "userManager", "authenticator", "groupManager", "entityQueryParser");
        for (String component : componentNames) {
            if (!config.hasClassForComponent(component)) continue;
            beanFactory.registerBeanDefinition(component, (BeanDefinition)this.createBeanDefinition(config.getComponentClassName(component), true));
        }
        beanFactory.registerBeanDefinition("class", (BeanDefinition)this.createBeanDefinition(config.getComponentClassName("class"), false));
        DefaultRepositoryAccessor accessor = (DefaultRepositoryAccessor)beanFactory.getBean("class");
        if (config.isCachingEnabled()) {
            CacheConfiguration cacheConfiguration = config.getCacheConfiguration();
            beanFactory.registerBeanDefinition("cachingUserManager", (BeanDefinition)this.createBeanDefinition(cacheConfiguration.getUserManagerClassName(), true));
            beanFactory.registerBeanDefinition("cachingGroupManager", (BeanDefinition)this.createBeanDefinition(cacheConfiguration.getGroupManagerClassName(), true));
            beanFactory.registerBeanDefinition("cachingPropertySetFactory", (BeanDefinition)this.createBeanDefinition(cacheConfiguration.getPropertySetFactoryClassName(), true));
            accessor.setUserManager((UserManager)beanFactory.getBean("cachingUserManager"));
            accessor.setGroupManager((GroupManager)beanFactory.getBean("cachingGroupManager"));
            if (beanFactory.containsBeanDefinition("propertySetFactory")) {
                accessor.setPropertySetFactory((PropertySetFactory)beanFactory.getBean("cachingPropertySetFactory"));
            }
        }
        return accessor;
    }

    protected Object createBean(String componentName, RepositoryConfiguration config) {
        return this.createBean(componentName, config, true);
    }

    protected Object createBean(String componentName, RepositoryConfiguration config, boolean useConstructorInjection) {
        DefaultListableBeanFactory beanFactory = this.createComponentContext(config);
        String className = config.getComponentClassName(componentName);
        if (className == null) {
            throw new RuntimeException("expected component [ " + componentName + " ] does not specify a className");
        }
        beanFactory.registerBeanDefinition(componentName, (BeanDefinition)this.createBeanDefinition(className, useConstructorInjection));
        return beanFactory.getBean(componentName);
    }

    private RootBeanDefinition createBeanDefinition(String className, boolean useConstructorInjection) {
        int autowireMode = useConstructorInjection ? 3 : 2;
        return new RootBeanDefinition(this.getClassForName(className), autowireMode);
    }

    protected Class getClassForName(String className) {
        try {
            return ClassLoaderUtils.loadClass(className, this.getClass());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class: [" + className + "]", e);
        }
    }

    protected DefaultListableBeanFactory createComponentContext(RepositoryConfiguration config) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        for (String name : config.getComponentNames()) {
            Object component = config.getComponent(name);
            beanFactory.registerSingleton(name, component);
        }
        return beanFactory;
    }
}

