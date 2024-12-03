/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PropertyContributor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.google.common.collect.ImmutableMap;

public class PluginPropertyContributor
implements PropertyContributor {
    @Override
    public void contribute(ImmutableMap.Builder<String, Object> builder, String name, Object value) {
        if (value instanceof Plugin) {
            builder.put((Object)(name + ".key"), (Object)((Plugin)value).getKey());
        }
        if (value instanceof ModuleDescriptor) {
            builder.put((Object)(name + ".key"), (Object)((ModuleDescriptor)value).getCompleteKey());
        }
    }
}

