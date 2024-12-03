/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 */
package com.atlassian.confluence.impl.search.v2.searchfilter;

import com.atlassian.confluence.content.ContentTypeModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EnabledContentTypeSupplier
implements Supplier<Set<String>> {
    private final PluginAccessor pluginAccessor;

    public EnabledContentTypeSupplier(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public Set<String> get() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTypeModuleDescriptor.class).stream().map(AbstractModuleDescriptor::getCompleteKey).collect(Collectors.toSet());
    }
}

