/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.upgrade.upgradetask;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.AbstractJournalIndexTaskQueue;
import com.atlassian.confluence.internal.search.queue.JournalIndexTaskQueue;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class SplitJournalUpgradeTask
extends AbstractDeferredRunUpgradeTask {
    public static final String BUILD_NUMBER = "8504";
    @VisibleForTesting
    static final String UPDATE_JOURNAL_NAME_QUERY = "UPDATE journalentry SET journal_name = 'change_index' WHERE type IN ('DELETE_CHANGE_DOCUMENTS', 'UPDATE_CHANGE_DOCUMENT', 'ADD_CHANGE_DOCUMENT', 'REBUILD_CHANGE_DOCUMENTS') AND journal_name = 'main_index'";
    private static final Logger log = LoggerFactory.getLogger(SplitJournalUpgradeTask.class);
    private final DdlExecutor ddlExecutor;
    private final JournalStateStore journalStateStore;
    private final JournalIdentifier contentJournalIdentifier;
    private final JournalIdentifier changeJournalIdentifier;
    private final HibernateTemplate hibernateTemplate;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final JournalIndexTaskQueue changeQueue;

    public SplitJournalUpgradeTask(@NonNull DdlExecutor ddlExecutor, @NonNull JournalStateStore journalStateStore, @NonNull JournalIdentifier contentJournalIdentifier, @NonNull JournalIdentifier changeJournalIdentifier, @NonNull SessionFactory sessionFactory, @NonNull IndexTaskFactoryInternal indexTaskFactory, @NonNull JournalIndexTaskQueue changeQueue) {
        this.ddlExecutor = Objects.requireNonNull(ddlExecutor);
        this.journalStateStore = Objects.requireNonNull(journalStateStore);
        this.contentJournalIdentifier = Objects.requireNonNull(contentJournalIdentifier);
        this.changeJournalIdentifier = Objects.requireNonNull(changeJournalIdentifier);
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.indexTaskFactory = indexTaskFactory;
        this.changeQueue = changeQueue;
    }

    @VisibleForTesting
    SplitJournalUpgradeTask(DdlExecutor ddlExecutor, JournalStateStore journalStateStore, JournalIdentifier contentJournalIdentifier, JournalIdentifier changeJournalIdentifier, HibernateTemplate hibernateTemplate, IndexTaskFactoryInternal indexTaskFactory, JournalIndexTaskQueue changeQueue) {
        this.ddlExecutor = Objects.requireNonNull(ddlExecutor);
        this.journalStateStore = Objects.requireNonNull(journalStateStore);
        this.contentJournalIdentifier = Objects.requireNonNull(contentJournalIdentifier);
        this.changeJournalIdentifier = Objects.requireNonNull(changeJournalIdentifier);
        this.indexTaskFactory = indexTaskFactory;
        this.hibernateTemplate = hibernateTemplate;
        this.changeQueue = changeQueue;
    }

    public void doDeferredUpgrade() throws Exception {
        log.info("SplitJournalUpgradeTask started. This might take some time because we are updating a large table");
        this.ddlExecutor.executeDdlStatements(Collections.singletonList(UPDATE_JOURNAL_NAME_QUERY));
        this.createChangeTasks(JournalEntryType.REINDEX_ALL_BLOGS, journalEntry -> this.indexTaskFactory.createReindexAllBlogsChangeTask());
        this.createChangeTasks(JournalEntryType.REINDEX_ALL_SPACES, journalEntry -> this.indexTaskFactory.createReindexAllSpacesChangeTask());
        this.createChangeTasks(JournalEntryType.REINDEX_ALL_USERS, journalEntry -> this.indexTaskFactory.createReindexAllUsersChangeTask());
        this.createChangeTasks(JournalEntryType.REINDEX_USERS_IN_GROUP, journalEntry -> this.indexTaskFactory.createReindexUsersInGroupChangeTask(journalEntry.getMessage()));
        this.createChangeTasks(JournalEntryType.UNINDEX_CONTENT_TYPE, journalEntry -> this.indexTaskFactory.createUnindexContentTypeChangeTask(journalEntry.getMessage()));
        this.createChangeTasks(JournalEntryType.UNINDEX_SPACE, journalEntry -> this.indexTaskFactory.createUnIndexSpaceChangeIndexTask(journalEntry.getMessage()));
        long mostRecentContentId = this.journalStateStore.getMostRecentId(this.contentJournalIdentifier);
        long mostRecentChangeId = this.journalStateStore.getMostRecentId(this.changeJournalIdentifier);
        log.info("{} most recent journal id is {}", (Object)this.contentJournalIdentifier.getJournalName(), (Object)mostRecentContentId);
        log.info("{} most recent journal id is {}", (Object)this.changeJournalIdentifier.getJournalName(), (Object)mostRecentChangeId);
        if (mostRecentChangeId == 0L) {
            this.journalStateStore.setMostRecentId(this.changeJournalIdentifier, mostRecentContentId);
            log.info("{} most recent journal id set to {}", (Object)this.changeJournalIdentifier.getJournalName(), (Object)mostRecentContentId);
        } else {
            log.info("{} most recent journal id has already been set to nonzero value ({}), doing nothing", (Object)this.changeJournalIdentifier.getJournalName(), (Object)mostRecentChangeId);
        }
        log.info("SplitJournalUpgradeTask ended");
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    @VisibleForTesting
    protected void createChangeTasks(JournalEntryType journalEntryType, Function<JournalEntry, ConfluenceIndexTask> changeTaskCreator) {
        List entries = (List)this.hibernateTemplate.execute(session -> session.createQuery("from JournalEntry where journalId = :journalId and type = :journalEntryType").setParameter("journalId", (Object)AbstractJournalIndexTaskQueue.CONTENT_JOURNAL_ID).setParameter("journalEntryType", (Object)journalEntryType.toString()).list());
        if (entries == null || entries.isEmpty()) {
            return;
        }
        this.changeQueue.enqueueAll(entries.stream().map(changeTaskCreator).collect(Collectors.toList()));
    }
}

