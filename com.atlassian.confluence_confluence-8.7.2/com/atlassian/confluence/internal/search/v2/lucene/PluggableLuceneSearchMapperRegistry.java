/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.impl.plugin.descriptor.search.AbstractLuceneMapperModuleDescriptor;
import com.atlassian.confluence.impl.plugin.descriptor.search.LuceneQueryMapperModuleDescriptor;
import com.atlassian.confluence.impl.plugin.descriptor.search.LuceneSortMapperModuleDescriptor;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapperRegistry;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSortMapper;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluggableLuceneSearchMapperRegistry
implements LuceneSearchMapperRegistry {
    private static final Logger log = LoggerFactory.getLogger(PluggableLuceneSearchMapperRegistry.class);
    protected PluginAccessor pluginAccessor;

    public PluggableLuceneSearchMapperRegistry(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public LuceneQueryMapper getQueryMapper(String key) {
        return (LuceneQueryMapper)this.getMappingPluginModule(key, LuceneQueryMapperModuleDescriptor.class, "searches of type: ");
    }

    @Override
    public LuceneSortMapper getSortMapper(String key) {
        return (LuceneSortMapper)this.getMappingPluginModule(key, LuceneSortMapperModuleDescriptor.class, "sort orders of type: ");
    }

    private <T> T getMappingPluginModule(String handleKey, Class<? extends AbstractLuceneMapperModuleDescriptor<T>> moduleType, String errorTypeDescription) {
        Collection mappers = this.pluginAccessor.getEnabledModuleDescriptorsByClass(moduleType).stream().filter(d -> d.handles(handleKey)).collect(Collectors.toList());
        if (mappers.size() == 0) {
            return null;
        }
        if (mappers.size() > 1) {
            this.warnMultiplePluginsInstalled(mappers, errorTypeDescription + handleKey);
        }
        return ((AbstractLuceneMapperModuleDescriptor)((Object)mappers.iterator().next())).getModule();
    }

    private void warnMultiplePluginsInstalled(Collection<? extends AbstractLuceneMapperModuleDescriptor> mappers, String description) {
        List moduleKeys = mappers.stream().map(AbstractModuleDescriptor::getCompleteKey).collect(Collectors.toList());
        log.warn("Multiple plugins provide lucene mappings for {}. This may result in unpredictable search results. You should disable all but one of the following plugin modules: {}", (Object)description, moduleKeys);
    }
}

