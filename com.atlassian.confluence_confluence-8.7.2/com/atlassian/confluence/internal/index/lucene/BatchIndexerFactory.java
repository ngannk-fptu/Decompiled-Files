/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.index.ReIndexSpec;
import com.atlassian.confluence.internal.index.BatchIndexer;
import com.atlassian.confluence.internal.index.ConcurrentBatchIndexer;
import com.atlassian.confluence.internal.index.ConcurrentBatchIndexerExecutorServiceFactory;
import com.atlassian.confluence.internal.index.lucene.BulkFieldPrefetcher;
import com.atlassian.confluence.internal.index.lucene.LuceneBatchIndexer;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import org.springframework.transaction.PlatformTransactionManager;

class BatchIndexerFactory {
    private final AnyTypeDao anyTypeDao;
    private final PlatformTransactionManager transactionManager;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final BulkFieldPrefetcher bulkFieldPrefetcher;
    private final ConcurrentBatchIndexerExecutorServiceFactory executorServiceFactory;

    BatchIndexerFactory(AnyTypeDao anyTypeDao, PlatformTransactionManager transactionManager, IndexTaskFactoryInternal indexTaskFactory, BulkFieldPrefetcher bulkFieldPrefetcher, ConcurrentBatchIndexerExecutorServiceFactory executorServiceFactory) {
        this.anyTypeDao = anyTypeDao;
        this.transactionManager = transactionManager;
        this.indexTaskFactory = indexTaskFactory;
        this.bulkFieldPrefetcher = bulkFieldPrefetcher;
        this.executorServiceFactory = executorServiceFactory;
    }

    BatchIndexer createConcurrentIndexer(ReIndexSpec reIndexSpec, SearchIndexWriter contentWriter, SearchIndexWriter changeWriter) {
        return new ConcurrentBatchIndexer(new LuceneBatchIndexer(this.anyTypeDao, this.transactionManager, this.indexTaskFactory, contentWriter, changeWriter, this.bulkFieldPrefetcher), reIndexSpec.getConcurrencyLevel(), this.executorServiceFactory);
    }
}

