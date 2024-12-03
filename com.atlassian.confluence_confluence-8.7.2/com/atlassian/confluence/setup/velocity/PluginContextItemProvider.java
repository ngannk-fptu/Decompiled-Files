/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.ozymandias.SafePluginPointAccess
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.plugin.descriptor.VelocityContextItemModuleDescriptor;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PluginContextItemProvider
implements VelocityContextItemProvider {
    private PluginAccessor pluginAccessor;

    public PluginContextItemProvider(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public @NonNull Map<String, Object> getContextMap() {
        HashMap velocityContextModuleMap = new HashMap();
        SafePluginPointAccess.to((PluginAccessor)this.pluginAccessor).forType(VelocityContextItemModuleDescriptor.class, (moduleDescriptor, module) -> velocityContextModuleMap.put(moduleDescriptor.getContextKey(), module));
        return ImmutableMap.copyOf(velocityContextModuleMap);
    }

    @Deprecated
    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Deprecated
    public void setPluginController(PluginController pluginController) {
    }
}

