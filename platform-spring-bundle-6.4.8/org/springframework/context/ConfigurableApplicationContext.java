/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import java.io.Closeable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;

public interface ConfigurableApplicationContext
extends ApplicationContext,
Lifecycle,
Closeable {
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
    public static final String CONVERSION_SERVICE_BEAN_NAME = "conversionService";
    public static final String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";
    public static final String ENVIRONMENT_BEAN_NAME = "environment";
    public static final String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";
    public static final String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";
    public static final String APPLICATION_STARTUP_BEAN_NAME = "applicationStartup";
    public static final String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";

    public void setId(String var1);

    public void setParent(@Nullable ApplicationContext var1);

    public void setEnvironment(ConfigurableEnvironment var1);

    @Override
    public ConfigurableEnvironment getEnvironment();

    public void setApplicationStartup(ApplicationStartup var1);

    public ApplicationStartup getApplicationStartup();

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor var1);

    public void addApplicationListener(ApplicationListener<?> var1);

    public void setClassLoader(ClassLoader var1);

    public void addProtocolResolver(ProtocolResolver var1);

    public void refresh() throws BeansException, IllegalStateException;

    public void registerShutdownHook();

    @Override
    public void close();

    public boolean isActive();

    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}

