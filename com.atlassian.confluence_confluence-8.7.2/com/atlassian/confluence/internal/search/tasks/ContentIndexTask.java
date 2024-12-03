/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class ContentIndexTask
implements ConfluenceIndexTask {
    private static final Logger log = LoggerFactory.getLogger(ContentIndexTask.class);
    private static final String INDEX_CONTENT_BY_TYPE_AND_STATUS_BATCH_SIZE = "index.content.by.type.and.status.batch.size";
    private static final int DEFAULT_INDEX_BATCH_SIZE = 2000;
    private final List<ContentType> contentTypes;
    private final List<ContentStatus> contentStatuses;
    private final JournalEntryType journalEntryType;
    private final BatchOperationManager batchOperationManager;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final PageDaoInternal pageDao;
    private final int batchSize;

    public ContentIndexTask(List<ContentType> contentTypes, List<ContentStatus> contentStatuses, JournalEntryType journalEntryType, PageDaoInternal pageDao, BatchOperationManager batchOperationManager, IndexTaskFactoryInternal indexTaskFactory) {
        this.contentTypes = Objects.requireNonNull(contentTypes);
        this.contentStatuses = Objects.requireNonNull(contentStatuses);
        this.journalEntryType = Objects.requireNonNull(journalEntryType);
        this.pageDao = Objects.requireNonNull(pageDao);
        this.batchOperationManager = Objects.requireNonNull(batchOperationManager);
        this.indexTaskFactory = Objects.requireNonNull(indexTaskFactory);
        this.batchSize = Integer.getInteger(INDEX_CONTENT_BY_TYPE_AND_STATUS_BATCH_SIZE, 2000);
    }

    @Override
    public final void perform(SearchIndexWriter writer) throws IOException {
        PageResponse pageResponse;
        int start = 0;
        do {
            LimitedRequest pageRequest = LimitedRequestImpl.create((int)start, (int)this.batchSize, (int)this.batchSize);
            pageResponse = PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, this.pageDao.getAbstractPages(this.contentTypes, this.contentStatuses, pageRequest), null);
            this.batchOperationManager.applyInBatches(pageResponse.getResults(), pageResponse.size(), content -> {
                try {
                    this.indexTaskFactory.createAddDocumentTask((Searchable)content).perform(writer);
                }
                catch (IOException exception) {
                    log.error(String.format("Unable to index item %s", content), (Throwable)exception);
                }
                return null;
            });
            log.info("Handled batch of size {}, starting at item {}", (Object)this.batchSize, (Object)(start += this.batchSize));
        } while (pageResponse.hasMore());
    }

    @Override
    public final String getDescription() {
        return "index.task.content.by.type.and.status";
    }

    @Override
    public final Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, this.journalEntryType, "Index Content by type and status");
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CONTENT;
    }
}

