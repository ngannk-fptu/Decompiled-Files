/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.persistence.dao.BlogPostDao;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class ReindexAllBlogsContentIndexTask
implements ConfluenceIndexTask {
    private static final Logger log = LoggerFactory.getLogger(ReindexAllBlogsContentIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.REINDEX_ALL_BLOGS;
    private final BlogPostDao blogPostDao;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final BatchOperationManager batchOperationManager;

    public ReindexAllBlogsContentIndexTask(IndexTaskFactoryInternal indexTaskFactory, BatchOperationManager batchOperationManager) {
        this.indexTaskFactory = indexTaskFactory;
        this.batchOperationManager = batchOperationManager;
        this.blogPostDao = (BlogPostDao)ContainerManager.getComponent((String)"blogPostDao");
    }

    @Override
    public void perform(SearchIndexWriter writer) {
        log.info("Start reindexing content - all blogs");
        List<Long> blogIds = this.blogPostDao.getCurrentBlogPostIds();
        this.batchOperationManager.applyInBatches(blogIds, blogIds.size(), id -> {
            BlogPost blog = this.blogPostDao.getById((long)id);
            this.indexContentAndDependents(blog, writer);
            return null;
        });
        log.info("Complete reindexing content - all blogs");
    }

    private void indexContentAndDependents(Searchable searchable, SearchIndexWriter writer) {
        if (ChangeDocumentIndexPolicy.shouldIndex(searchable)) {
            try {
                this.indexTaskFactory.createDeleteDocumentTask(searchable).perform(writer);
                this.indexTaskFactory.createAddDocumentTask(searchable).perform(writer);
            }
            catch (IOException e) {
                log.error("Unable to reindex item " + searchable, (Throwable)e);
            }
            searchable.getSearchableDependants().forEach(item -> {
                if (item instanceof Searchable) {
                    this.indexContentAndDependents((Searchable)item, writer);
                }
            });
        }
    }

    @Override
    public String getDescription() {
        return "index.task.reindex.blog.content";
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, null);
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CONTENT;
    }
}

