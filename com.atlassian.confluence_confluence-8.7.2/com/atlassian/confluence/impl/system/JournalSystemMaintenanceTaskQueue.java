/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.EntryProcessorResult
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.system;

import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.EntryProcessorResult;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.impl.system.MaintenanceTaskExecutionException;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskQueue;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskRegistry;
import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskMarshalling;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalSystemMaintenanceTaskQueue
implements SystemMaintenanceTaskQueue {
    static final String REINDEX_IMPROVEMENT_DARKFEATURE_KEY = "confluence.reindex.improvements";
    static final JournalIdentifier JOURNAL_IDENTIFIER = new JournalIdentifier("system_maintenance");
    private static final int BATCH_SIZE = Integer.getInteger("system.maintenance.task.journal.queue", 20);
    private static final Logger log = LoggerFactory.getLogger(JournalSystemMaintenanceTaskQueue.class);
    private final JournalService journalService;
    private final SystemMaintenanceTaskMarshalling taskMarshalling;
    private final SystemMaintenanceTaskRegistry taskRegistry;
    private final DarkFeatureManager darkFeatureManager;

    public JournalSystemMaintenanceTaskQueue(JournalService journalService, SystemMaintenanceTaskMarshalling taskMarshalling, SystemMaintenanceTaskRegistry taskRegistry, DarkFeatureManager darkFeatureManager) {
        this.journalService = Objects.requireNonNull(journalService);
        this.taskMarshalling = Objects.requireNonNull(taskMarshalling);
        this.taskRegistry = Objects.requireNonNull(taskRegistry);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    @Override
    public void enqueue(SystemMaintenanceTask task) {
        if (this.darkFeatureManager.isEnabledForAllUsers(REINDEX_IMPROVEMENT_DARKFEATURE_KEY).orElse(false).booleanValue()) {
            this.enqueueInternal(task);
        }
    }

    @Override
    public void processEntries() {
        this.journalService.processNewEntries(JOURNAL_IDENTIFIER, BATCH_SIZE, entries -> {
            if (this.darkFeatureManager.isEnabledForAllUsers(REINDEX_IMPROVEMENT_DARKFEATURE_KEY).orElse(false).booleanValue()) {
                return this.processEntriesInternal((Iterable<JournalEntry>)entries);
            }
            return EntryProcessorResult.success((Object)Iterables.size((Iterable)entries));
        });
    }

    private void enqueueInternal(SystemMaintenanceTask task) {
        SystemMaintenanceTaskType.forTask(task).ifPresent(taskType -> {
            JournalEntry journalEntry = new JournalEntry(JOURNAL_IDENTIFIER, taskType.name(), this.taskMarshalling.marshal(task));
            this.journalService.enqueue(journalEntry);
        });
    }

    private EntryProcessorResult<Integer> processEntriesInternal(Iterable<JournalEntry> entries) {
        int successCount = 0;
        for (JournalEntry entry : entries) {
            SystemMaintenanceTask task;
            SystemMaintenanceTaskType taskType;
            try {
                taskType = SystemMaintenanceTaskType.valueOf(entry.getType());
                task = this.taskMarshalling.unmarshal(taskType.getTaskClazz(), entry.getMessage());
            }
            catch (IllegalArgumentException e) {
                log.error("Could not convert the journal record into a SystemMaintenanceTask. The entry data may be malformed. '{}'", (Object)entry, (Object)e);
                continue;
            }
            Optional<SystemMaintenanceTaskRunner> taskRunner = this.taskRegistry.findTaskRunner(taskType);
            if (!taskRunner.isPresent()) {
                log.error("No TaskRunner configured for task with type {}", (Object)taskType);
                continue;
            }
            try {
                taskRunner.get().execute(task);
                ++successCount;
            }
            catch (MaintenanceTaskExecutionException e) {
                log.error("Encountered an unrecoverable error while processing the system task. This task will be retried.", (Throwable)e);
                return EntryProcessorResult.failure((Object)successCount, (long)entry.getId());
            }
            catch (RuntimeException e) {
                log.error("Encountered an unrecoverable error while executing the system task. This task will be skipped. '{}'", (Object)entry, (Object)e);
            }
        }
        return EntryProcessorResult.success((Object)successCount);
    }
}

