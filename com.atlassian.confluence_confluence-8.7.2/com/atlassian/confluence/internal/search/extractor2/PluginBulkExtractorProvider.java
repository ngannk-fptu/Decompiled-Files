/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.internal.search.extractor2;

import com.atlassian.confluence.internal.search.extractor2.BulkExtractorProvider;
import com.atlassian.confluence.plugin.descriptor.BulkExtractorModuleDescriptor;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class PluginBulkExtractorProvider
implements BulkExtractorProvider {
    private final PluginAccessor pluginAccessor;

    public PluginBulkExtractorProvider(PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    @Override
    public Collection<BulkExtractor<?>> findBulkExtractors(SearchIndex searchIndex) {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(BulkExtractorModuleDescriptor.class).stream().filter(descriptor -> descriptor.getSearchIndex() == searchIndex).sorted().map(ModuleDescriptor::getModule).collect(Collectors.toList());
    }
}

