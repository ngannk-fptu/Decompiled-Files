/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class ReindexAllSpacesContentIndexTask
implements ConfluenceIndexTask {
    private static final Logger log = LoggerFactory.getLogger(ReindexAllSpacesContentIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.REINDEX_ALL_SPACES;
    private final SpaceDao spaceDao;
    private final IndexTaskFactoryInternal indexTaskFactory;

    public ReindexAllSpacesContentIndexTask(SpaceDao spaceDao, IndexTaskFactoryInternal indexTaskFactory) {
        this.spaceDao = spaceDao;
        this.indexTaskFactory = indexTaskFactory;
    }

    @Override
    public String getDescription() {
        return "index.task.reindex.allspaces.content";
    }

    @Override
    public void perform(SearchIndexWriter writer) {
        this.spaceDao.performOnAll(space -> {
            try {
                this.indexTaskFactory.createUpdateDocumentTask((Searchable)space).perform(writer);
            }
            catch (IOException e) {
                log.error("Unable to reindex space " + space.getName(), (Throwable)e);
            }
        });
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

