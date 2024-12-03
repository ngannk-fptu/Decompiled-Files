/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  com.opensymphony.xwork2.factory.DefaultActionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.confluence.plugin.struts.PluginAwareActionConfig;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.DefaultActionFactory;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginAwareActionFactory
extends DefaultActionFactory {
    private static final Logger log = LoggerFactory.getLogger(PluginAwareActionFactory.class);

    public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
        if (config instanceof PluginAwareActionConfig) {
            return PluginAwareActionFactory.buildAction((PluginAwareActionConfig)config, ((Object)((Object)this)).getClass());
        }
        return super.buildAction(actionName, namespace, config, extraContext);
    }

    private static Action buildAction(PluginAwareActionConfig actionConfig, Class<?> callingClass) throws ReflectiveOperationException {
        Plugin plugin = actionConfig.getPlugin();
        log.debug("Loading PluginAware action class {} via plugin [{}]", (Object)actionConfig.getClassName(), (Object)plugin.getKey());
        Class actionClass = plugin.loadClass(actionConfig.getClassName(), callingClass);
        if (plugin instanceof ContainerManagedPlugin) {
            log.debug("Creating action bean {} via container-managed plugin [{}]", (Object)actionConfig.getClassName(), (Object)plugin.getKey());
            return (Action)LegacySpringContainerAccessor.createBean(plugin, actionClass);
        }
        log.debug("Creating action bean {} from {} [{}] via direct instantiation", new Object[]{actionConfig.getClassName(), plugin.getClass().getName(), plugin.getKey()});
        return (Action)actionClass.getConstructor(new Class[0]).newInstance(new Object[0]);
    }
}

