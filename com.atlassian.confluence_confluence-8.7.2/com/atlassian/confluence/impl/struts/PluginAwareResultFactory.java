/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.opensymphony.xwork2.Result
 *  com.opensymphony.xwork2.config.entities.ResultConfig
 *  com.opensymphony.xwork2.factory.DefaultResultFactory
 *  com.opensymphony.xwork2.inject.Inject
 *  com.opensymphony.xwork2.util.reflection.ReflectionException
 *  com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler
 *  com.opensymphony.xwork2.util.reflection.ReflectionProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.confluence.plugin.struts.PluginAwareResultConfig;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.factory.DefaultResultFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginAwareResultFactory
extends DefaultResultFactory {
    private static final Logger log = LoggerFactory.getLogger(PluginAwareResultFactory.class);
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
        super.setReflectionProvider(reflectionProvider);
    }

    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        if (resultConfig instanceof PluginAwareResultConfig) {
            return this.buildResult((PluginAwareResultConfig)resultConfig, extraContext, ((Object)((Object)this)).getClass());
        }
        return super.buildResult(resultConfig, extraContext);
    }

    private Result buildResult(PluginAwareResultConfig resultConfig, Map<String, Object> extraContext, Class<?> callingClass) throws ReflectiveOperationException {
        Result result;
        Plugin plugin = resultConfig.getPlugin();
        log.debug("Loading PluginAware result class {} via plugin [{}]", (Object)resultConfig.getClassName(), (Object)plugin.getKey());
        Class resultClass = plugin.loadClass(resultConfig.getClassName(), callingClass);
        if (plugin instanceof ContainerManagedPlugin) {
            log.debug("Creating result bean {} via container-managed plugin [{}]", (Object)resultConfig.getClassName(), (Object)plugin.getKey());
            result = (Result)LegacySpringContainerAccessor.createBean(plugin, resultClass);
        } else {
            log.debug("Creating result bean {} from {} [{}] via direct instantiation", new Object[]{resultConfig.getClassName(), plugin.getClass().getName(), plugin.getKey()});
            result = (Result)resultClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        if (resultConfig.getParams() != null) {
            resultConfig.getParams().forEach((key, value) -> {
                block2: {
                    try {
                        this.reflectionProvider.setProperty(key, value, (Object)result, extraContext, true);
                    }
                    catch (ReflectionException ex) {
                        if (!(result instanceof ReflectionExceptionHandler)) break block2;
                        ((ReflectionExceptionHandler)result).handle(ex);
                    }
                }
            });
        }
        return result;
    }
}

