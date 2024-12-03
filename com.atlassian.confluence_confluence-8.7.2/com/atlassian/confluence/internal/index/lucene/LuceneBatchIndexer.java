/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.ObjectRetrievalFailureException
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.internal.index.lucene;

import bucket.core.persistence.hibernate.HibernateHandle;
import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.internal.index.BatchIndexer;
import com.atlassian.confluence.internal.index.ReindexProgress;
import com.atlassian.confluence.internal.index.lucene.BulkFieldPrefetcher;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.util.Cleanup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@LuceneIndependent
@Internal
public class LuceneBatchIndexer
implements BatchIndexer {
    private static final Logger log = LoggerFactory.getLogger(LuceneBatchIndexer.class);
    private final AnyTypeDao anyTypeDao;
    private final PlatformTransactionManager transactionManager;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final SearchIndexWriter contentWriter;
    private final SearchIndexWriter changeWriter;
    private final BulkFieldPrefetcher bulkFieldPrefetcher;

    public LuceneBatchIndexer(AnyTypeDao anyTypeDao, PlatformTransactionManager transactionManager, IndexTaskFactoryInternal indexTaskFactory, SearchIndexWriter contentWriter, SearchIndexWriter changeWriter, BulkFieldPrefetcher bulkFieldPrefetcher) {
        this.anyTypeDao = Objects.requireNonNull(anyTypeDao);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.indexTaskFactory = Objects.requireNonNull(indexTaskFactory);
        this.contentWriter = Objects.requireNonNull(contentWriter);
        this.changeWriter = Objects.requireNonNull(changeWriter);
        this.bulkFieldPrefetcher = Objects.requireNonNull(bulkFieldPrefetcher);
    }

    @Override
    public void index(List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles, ReindexProgress progress) {
        try (Cleanup ignore = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            String entityClassName = LuceneBatchIndexer.getEntityClassName(handles);
            new TransactionTemplate(this.transactionManager, LuceneBatchIndexer.createTxDef()).execute(transactionStatus -> {
                List<Searchable> indexableSearchables = this.getSearchables(handles, entityClassName);
                if (indexableSearchables.isEmpty()) {
                    log.debug(String.format("No searchables of type %s in a re-indexing batch were considered indexable.", entityClassName));
                    return null;
                }
                this.doIndex(indexableSearchables, entityClassName);
                return null;
            });
        }
    }

    private static TransactionDefinition createTxDef() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition(3);
        def.setReadOnly(true);
        return def;
    }

    private static String getEntityClassName(Collection<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles) {
        return handles.iterator().next().getClassName();
    }

    private List<Searchable> getSearchables(List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles, String className) {
        List<Searchable> result;
        List<Long> ids = handles.stream().filter(x -> x.getClassName().equals(className)).map(HibernateHandle::getId).collect(Collectors.toList());
        if (handles.size() != ids.size()) {
            throw new IllegalArgumentException("The list of handles must be of the same class");
        }
        try {
            result = this.anyTypeDao.findByIdsAndClassName(ids, className);
        }
        catch (ObjectRetrievalFailureException e) {
            log.warn("Unable to retrieve collection of searchable. Falling back to individual retrieval. " + e.getMessage(), (Throwable)e);
            result = this.retrieveObjectsIndividuallyAndLogFailures(handles);
        }
        List<Searchable> indexableSearchables = result.stream().filter(Searchable::isIndexable).collect(Collectors.toList());
        return indexableSearchables;
    }

    private List<Searchable> retrieveObjectsIndividuallyAndLogFailures(List<com.atlassian.confluence.core.persistence.hibernate.HibernateHandle> handles) {
        ArrayList<Searchable> result = new ArrayList<Searchable>(handles.size());
        for (com.atlassian.confluence.core.persistence.hibernate.HibernateHandle handle : handles) {
            try {
                result.add((Searchable)this.anyTypeDao.findByHandle((Handle)handle));
            }
            catch (ObjectRetrievalFailureException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to retrieve single object: " + e.getMessage(), (Throwable)e);
                    continue;
                }
                log.warn("Unable to retrieve single object: " + e.getMessage());
            }
        }
        return result;
    }

    private void doIndex(List<Searchable> searchables, String entityClassName) {
        this.doIndex(searchables, this.bulkFieldPrefetcher.createPrefetchedDocumentBuilder(searchables, entityClassName));
    }

    private void doIndex(List<Searchable> searchables, Function<Searchable, AtlassianDocument> documentBuilder) {
        log.debug("Indexing {} items", (Object)searchables.size());
        for (Searchable searchable : searchables) {
            try {
                if (searchable != null) {
                    log.debug("Index {} [{}]", (Object)searchable, (Object)searchable.getClass().getName());
                }
                this.tryIndex(searchable, documentBuilder, this.contentWriter);
                if (!ChangeDocumentIndexPolicy.shouldIndex(searchable)) continue;
                if (searchable instanceof Versioned && ((Versioned)searchable).isLatestVersion()) {
                    this.indexTaskFactory.createRebuildChangeDocumentsIndexTask(searchable).perform(this.changeWriter);
                    continue;
                }
                this.indexTaskFactory.createAddChangeDocumentTask(searchable).perform(this.changeWriter);
            }
            catch (IOException | RuntimeException e) {
                if (searchable != null) {
                    log.error("Exception indexing document with id: " + searchable.getId(), (Throwable)e);
                    continue;
                }
                log.error("Error indexing document", (Throwable)e);
            }
        }
    }

    private void tryIndex(Searchable searchable, Function<Searchable, AtlassianDocument> documentBuilder, SearchIndexWriter writer) {
        try {
            writer.add(documentBuilder.apply(searchable));
        }
        catch (IOException | RuntimeException e) {
            log.warn("Error getting document from searchable", (Throwable)e);
        }
    }
}

