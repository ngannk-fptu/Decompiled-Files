/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.opensymphony.xwork2.config.ConfigurationException
 *  com.opensymphony.xwork2.config.entities.InterceptorConfig
 *  com.opensymphony.xwork2.factory.DefaultInterceptorFactory
 *  com.opensymphony.xwork2.inject.Inject
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  com.opensymphony.xwork2.interceptor.WithLazyParams
 *  com.opensymphony.xwork2.util.reflection.ReflectionProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.confluence.plugin.struts.PluginAwareInterceptorConfig;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.factory.DefaultInterceptorFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.WithLazyParams;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginAwareInterceptorFactory
extends DefaultInterceptorFactory {
    private static final Logger log = LoggerFactory.getLogger(PluginAwareInterceptorFactory.class);
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
        super.setReflectionProvider(reflectionProvider);
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        if (interceptorConfig instanceof PluginAwareInterceptorConfig) {
            try {
                return this.buildInterceptor((PluginAwareInterceptorConfig)interceptorConfig, interceptorRefParams, ((Object)((Object)this)).getClass());
            }
            catch (ReflectiveOperationException e) {
                throw new ConfigurationException(String.format("Unable to instantiate interceptor %s for plugin %s.", interceptorConfig.getClassName(), ((PluginAwareInterceptorConfig)interceptorConfig).getPlugin().getKey()), (Throwable)e);
            }
        }
        return super.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    private Interceptor buildInterceptor(PluginAwareInterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams, Class<?> callingClass) throws ReflectiveOperationException {
        Interceptor interceptor;
        Plugin plugin = interceptorConfig.getPlugin();
        log.debug("Loading PluginAware interceptor class {} via plugin [{}]", (Object)interceptorConfig.getClassName(), (Object)plugin.getKey());
        Class interceptorClass = plugin.loadClass(interceptorConfig.getClassName(), callingClass);
        if (plugin instanceof ContainerManagedPlugin) {
            log.debug("Creating interceptor bean {} via container-managed plugin [{}]", (Object)interceptorConfig.getClassName(), (Object)plugin.getKey());
            interceptor = (Interceptor)LegacySpringContainerAccessor.createBean(plugin, interceptorClass);
        } else {
            log.debug("Creating interceptor bean {} from {} [{}] via direct instantiation", new Object[]{interceptorConfig.getClassName(), plugin.getClass().getName(), plugin.getKey()});
            interceptor = (Interceptor)interceptorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        HashMap<String, String> params = interceptorConfig.getParams() == null ? new HashMap<String, String>() : new HashMap(interceptorConfig.getParams());
        params.putAll(interceptorRefParams);
        if (!(interceptor instanceof WithLazyParams)) {
            this.reflectionProvider.setProperties(params, (Object)interceptor);
        }
        interceptor.init();
        return interceptor;
    }
}

