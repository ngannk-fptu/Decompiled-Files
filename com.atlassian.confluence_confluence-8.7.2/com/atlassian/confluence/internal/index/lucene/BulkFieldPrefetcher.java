/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  javax.annotation.concurrent.NotThreadSafe
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.search.extractor2.BulkExtractorProvider;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
final class BulkFieldPrefetcher {
    private static final Logger log = LoggerFactory.getLogger(BulkFieldPrefetcher.class);
    private final AtlassianDocumentBuilder<Searchable> documentBuilder;
    private final BulkExtractorProvider bulkExtractorProvider;

    public BulkFieldPrefetcher(BulkExtractorProvider bulkExtractorProvider, AtlassianDocumentBuilder<Searchable> documentBuilder) {
        this.bulkExtractorProvider = bulkExtractorProvider;
        this.documentBuilder = documentBuilder;
    }

    public Function<Searchable, AtlassianDocument> createPrefetchedDocumentBuilder(Collection<Searchable> searchables, String entityClassName) {
        return this.prefetch(searchables, entityClassName).wrap(this.documentBuilder);
    }

    private PrefetchedFields prefetch(Collection<Searchable> searchables, String entityClassName) {
        try {
            Class<Searchable> entityClass = Class.forName(entityClassName);
            return this.prefetch(searchables, entityClass);
        }
        catch (ClassNotFoundException ex) {
            log.error("Failed to resolve entity class, cannot bulk-prefetch fields", (Throwable)ex);
            return new PrefetchedFields();
        }
    }

    private PrefetchedFields prefetch(Collection<Searchable> searchables, Class<Searchable> entityClass) {
        log.debug("Bulk prefetching fields for {} {}", (Object)searchables.size(), (Object)entityClass.getName());
        PrefetchedFields prefetchedFields = new PrefetchedFields();
        this.findBulkExtractorsForType(entityClass).forEach(extractor -> {
            log.debug("Using {} to prefetch fields for {} {}", new Object[]{extractor, searchables.size(), entityClass.getName()});
            extractor.extractAll(searchables, entityClass, (x$0, x$1) -> prefetchedFields.collect((Searchable)x$0, (FieldDescriptor)x$1));
        });
        return prefetchedFields;
    }

    private <T> Stream<BulkExtractor<T>> findBulkExtractorsForType(Class<T> entityType) {
        return this.bulkExtractorProvider.findBulkExtractors(SearchIndex.CONTENT).stream().filter(extractor -> extractor.canHandle(entityType)).map(extractor -> extractor);
    }

    @NotThreadSafe
    private static class PrefetchedFields {
        final Multimap<HibernateHandle, FieldDescriptor> prefetchedFields = ArrayListMultimap.create();

        private PrefetchedFields() {
        }

        private void collect(Searchable searchable, FieldDescriptor field) {
            this.prefetchedFields.put((Object)new HibernateHandle(searchable), (Object)field);
        }

        private void decorate(Searchable searchable, AtlassianDocument document) {
            Collection additionalFields;
            if (document != null && !(additionalFields = this.prefetchedFields.get((Object)new HibernateHandle(searchable))).isEmpty()) {
                log.debug("Decorating document with {} prefetched fields for {}", (Object)additionalFields.size(), (Object)searchable);
                additionalFields.forEach(document::addField);
            }
        }

        private Function<Searchable, AtlassianDocument> wrap(AtlassianDocumentBuilder<Searchable> documentBuilder) {
            return searchable -> {
                log.debug("Building initial document for {}", searchable);
                AtlassianDocument document = documentBuilder.build((Searchable)searchable);
                this.decorate((Searchable)searchable, document);
                return document;
            };
        }
    }
}

