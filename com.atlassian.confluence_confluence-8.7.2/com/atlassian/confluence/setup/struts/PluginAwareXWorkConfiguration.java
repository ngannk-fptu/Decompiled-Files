/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.opensymphony.xwork2.config.RuntimeConfiguration
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  com.opensymphony.xwork2.config.impl.DefaultConfiguration
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.plugin.struts.PluginAwareActionConfig;
import com.atlassian.plugin.Plugin;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class PluginAwareXWorkConfiguration
extends DefaultConfiguration {
    private Map<ActionKey, Plugin> pluginActionConfigs = Collections.emptyMap();

    public PluginAwareXWorkConfiguration(String defaultBeanName) {
        super(defaultBeanName);
    }

    public void rebuildRuntimeConfiguration() {
        ImmutableMap.Builder actionConfigBuilder = ImmutableMap.builder();
        this.getPackageConfigs().values().stream().filter(packageConfig -> !packageConfig.isAbstract()).forEach(packageConfig -> packageConfig.getActionConfigs().values().stream().filter(actionConfig -> actionConfig instanceof PluginAwareActionConfig).forEach(actionConfig -> {
            if (actionConfig.getName() != null && ((PluginAwareActionConfig)((Object)((Object)actionConfig))).getPlugin() != null) {
                actionConfigBuilder.put((Object)new ActionKey(packageConfig.getNamespace(), actionConfig.getName()), (Object)((PluginAwareActionConfig)((Object)((Object)actionConfig))).getPlugin());
            }
        }));
        this.pluginActionConfigs = actionConfigBuilder.build();
        super.rebuildRuntimeConfiguration();
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        final RuntimeConfiguration actual = super.getRuntimeConfiguration();
        return new RuntimeConfiguration(){

            public ActionConfig getActionConfig(String namespace, String name) {
                ActionConfig actionConfig = actual.getActionConfig(namespace, name);
                return this.convertToPluginAwareActionConfig(namespace, actionConfig);
            }

            public Map<String, Map<String, ActionConfig>> getActionConfigs() {
                return Maps.transformEntries((Map)actual.getActionConfigs(), (namespace, innerMap) -> innerMap == null ? null : Maps.transformValues((Map)innerMap, actionConfig -> this.convertToPluginAwareActionConfig((String)namespace, (ActionConfig)actionConfig)));
            }

            private ActionConfig convertToPluginAwareActionConfig(String namespace, ActionConfig actionConfig) {
                Plugin associatedPlugin;
                if (actionConfig != null && (associatedPlugin = PluginAwareXWorkConfiguration.this.pluginActionConfigs.get(new ActionKey(namespace.equals("/") ? "" : namespace, actionConfig.getName()))) != null) {
                    return new PluginAwareActionConfig(actionConfig, associatedPlugin);
                }
                return actionConfig;
            }
        };
    }

    private static class ActionKey {
        final String namespace;
        final String name;

        private ActionKey(String namespace, String name) {
            this.namespace = namespace;
            this.name = name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ActionKey actionKey = (ActionKey)o;
            return Objects.equals(this.namespace, actionKey.namespace) && Objects.equals(this.name, actionKey.name);
        }

        public int hashCode() {
            return Objects.hash(this.namespace, this.name);
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("namespace", (Object)this.namespace).add("name", (Object)this.name).toString();
        }
    }
}

