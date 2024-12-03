/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.searchindexer;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.event.events.cluster.ClusterReindexRequiredEvent;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalManager;
import com.atlassian.confluence.impl.search.queue.ChangeIndexTaskQueue;
import com.atlassian.confluence.impl.search.queue.ContentIndexTaskQueue;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchIndexerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SearchIndexerAdapter.class);
    private final ContentIndexTaskQueue contentIndexTaskQueue;
    private final ChangeIndexTaskQueue changeIndexTaskQueue;
    private final JournalManager journalManager;
    private final DarkFeatureManager salDarkFeatureManager;
    private final IndexManager indexManager;
    private final EventPublisher eventPublisher;

    public SearchIndexerAdapter(ContentIndexTaskQueue contentIndexTaskQueue, ChangeIndexTaskQueue changeIndexTaskQueue, JournalManager journalManager, DarkFeatureManager salDarkFeatureManager, IndexManager indexManager, EventPublisher eventPublisher) {
        this.contentIndexTaskQueue = contentIndexTaskQueue;
        this.changeIndexTaskQueue = changeIndexTaskQueue;
        this.journalManager = journalManager;
        this.salDarkFeatureManager = salDarkFeatureManager;
        this.indexManager = indexManager;
        this.eventPublisher = eventPublisher;
    }

    public void reindexObjectsAsync(ParallelTasksExecutor parallelTasksExecutor, Collection<ImportedObjectV2> importedObjects) {
        parallelTasksExecutor.runTaskAsync(() -> {
            this.enqueueIndexRecords(importedObjects, this.contentIndexTaskQueue.getJournalIdentifier(), JournalEntryType.ADD_DOCUMENT);
            this.enqueueIndexRecords(importedObjects, this.changeIndexTaskQueue.getJournalIdentifier(), JournalEntryType.ADD_CHANGE_DOCUMENT);
            return null;
        }, "indexing content");
    }

    private void enqueueIndexRecords(Collection<ImportedObjectV2> importedObjects, JournalIdentifier journalIdentifier, JournalEntryType journalEntryType) {
        List<JournalEntry> journalEntries = importedObjects.stream().map(importedObject -> this.convertToJournalEntry((ImportedObjectV2)importedObject, journalIdentifier, journalEntryType)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        this.journalManager.enqueue(journalEntries);
    }

    public void unIndexAll() {
        log.debug("unIndexAll started");
        this.indexManager.unIndexAll();
        log.debug("unIndexAll finished");
    }

    public void reIndexAll() {
        boolean shouldPublishReindexEvent;
        log.debug("reIndexAll started");
        boolean bl = shouldPublishReindexEvent = this.salDarkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false) == false;
        if (shouldPublishReindexEvent) {
            this.eventPublisher.publish((Object)new ClusterReindexRequiredEvent("global import"));
        }
        this.indexManager.reIndex();
        log.debug("reIndexAll finished");
    }

    private Optional<JournalEntry> convertToJournalEntry(ImportedObjectV2 importedObjectV2, JournalIdentifier journalIdentifier, JournalEntryType journalEntryType) {
        Object rawIdValue = importedObjectV2.getId();
        if (rawIdValue == null) {
            log.warn("Id cannot be null. Object {}", (Object)importedObjectV2);
            return Optional.empty();
        }
        long id = SearchIndexerAdapter.convertToLong(importedObjectV2.getId());
        HibernateHandle objectHandle = new HibernateHandle(importedObjectV2.getEntityClass().getName(), id);
        return Optional.of(new JournalEntry(journalIdentifier, String.valueOf((Object)journalEntryType), objectHandle.toString()));
    }

    private static long convertToLong(@Nonnull Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        }
        return (Long)value;
    }
}

