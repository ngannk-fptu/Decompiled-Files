/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.lucene.search.IndexSearcher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.impl.plugin.descriptor.search.LuceneSearcherInitialisationModuleDescriptor;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import com.atlassian.confluence.plugin.ModuleDescriptorCache;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collection;
import java.util.function.Supplier;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluggableSearcherInitialisation
implements SearcherInitialisation {
    private static final Logger log = LoggerFactory.getLogger(PluggableSearcherInitialisation.class);
    private final Supplier<Collection<LuceneSearcherInitialisationModuleDescriptor>> moduleDescriptorSupplier;

    public PluggableSearcherInitialisation(PluginAccessor pluginAccessor) {
        this.moduleDescriptorSupplier = () -> pluginAccessor.getEnabledModuleDescriptorsByClass(LuceneSearcherInitialisationModuleDescriptor.class);
    }

    @Deprecated
    public PluggableSearcherInitialisation(ModuleDescriptorCache<LuceneSearcherInitialisationModuleDescriptor> moduleDescriptorCache) {
        this.moduleDescriptorSupplier = moduleDescriptorCache::getDescriptors;
    }

    public void initialise(IndexSearcher searcher) {
        if (log.isDebugEnabled()) {
            log.debug("Warming up searcher..");
        }
        Collection<LuceneSearcherInitialisationModuleDescriptor> descriptors = this.moduleDescriptorSupplier.get();
        for (LuceneSearcherInitialisationModuleDescriptor descriptor : descriptors) {
            try {
                SearcherInitialisation initializer = descriptor.getModule();
                initializer.initialise(searcher);
            }
            catch (Exception e) {
                log.error("Error encountered while warming up searcher for: " + descriptor.getCompleteKey(), (Throwable)e);
            }
        }
    }
}

