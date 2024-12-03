/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.plugin.descriptor.ContentNameSearchSectionSpecModuleDescriptor;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ContentNameSearchSectionSpecsProvider
implements Supplier<Map<Category, ContentNameSearchSectionSpec>> {
    private final Supplier<Map<Category, ContentNameSearchSectionSpec>> coreSectionSpecsProvider;
    private final PluginAccessor pluginAccessor;

    public ContentNameSearchSectionSpecsProvider(Supplier<Map<Category, ContentNameSearchSectionSpec>> coreSectionSpecsProvider, PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.coreSectionSpecsProvider = Objects.requireNonNull(coreSectionSpecsProvider);
    }

    @Override
    public Map<Category, ContentNameSearchSectionSpec> get() {
        HashMap<Category, ContentNameSearchSectionSpec> result = new HashMap<Category, ContentNameSearchSectionSpec>(this.coreSectionSpecsProvider.get());
        this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentNameSearchSectionSpecModuleDescriptor.class).stream().map(ModuleDescriptor::getModule).forEach(x -> result.put(x.getCategory(), (ContentNameSearchSectionSpec)x));
        return result;
    }
}

