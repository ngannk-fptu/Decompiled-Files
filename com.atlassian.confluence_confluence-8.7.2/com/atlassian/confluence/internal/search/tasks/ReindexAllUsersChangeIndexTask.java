/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.google.common.base.Throwables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.internal.search.ChangeDocumentIndexPolicy;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.persistence.dao.PersonalInformationDao;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class ReindexAllUsersChangeIndexTask
implements ConfluenceIndexTask {
    private static final Logger log = LoggerFactory.getLogger(ReindexAllUsersChangeIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.REINDEX_ALL_USERS_CHANGE;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final BatchOperationManager batchOperationManager;
    private final PersonalInformationDao personalInformationDao;

    public ReindexAllUsersChangeIndexTask(BatchOperationManager batchOperationManager, PersonalInformationDao personalInformationDao, IndexTaskFactoryInternal indexTaskFactory) {
        this.indexTaskFactory = indexTaskFactory;
        this.batchOperationManager = batchOperationManager;
        this.personalInformationDao = personalInformationDao;
    }

    @Override
    public String getDescription() {
        return "index.task.reindex.users.change";
    }

    @Override
    public void perform(final SearchIndexWriter writer) throws IOException {
        List<Long> listOfInfoId = this.personalInformationDao.findIdsWithAssociatedUser();
        log.info("Found {} PersonalInformation that needs reindexing.", (Object)listOfInfoId.size());
        try {
            this.batchOperationManager.applyInBatches(listOfInfoId, listOfInfoId.size(), new Function<Long, Void>(){

                @Override
                public Void apply(Long id) {
                    PersonalInformation personalInformation = ReindexAllUsersChangeIndexTask.this.personalInformationDao.getById(id);
                    try {
                        if (ChangeDocumentIndexPolicy.shouldIndex(personalInformation)) {
                            ReindexAllUsersChangeIndexTask.this.indexTaskFactory.createRebuildChangeDocumentsIndexTask(personalInformation).perform(writer);
                        }
                    }
                    catch (IOException e) {
                        throw new UncheckedExecutionException((Throwable)e);
                    }
                    return null;
                }

                public String toString() {
                    return "PersonalInfo reindexing for all users";
                }
            });
        }
        catch (UncheckedExecutionException e) {
            Throwable cause = e.getCause();
            Throwables.propagateIfInstanceOf((Throwable)cause, IOException.class);
            throw Throwables.propagate((Throwable)cause);
        }
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, null);
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CHANGE;
    }
}

