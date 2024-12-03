/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.impl.backuprestore.statistics;

import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.event.api.EventPublisher;

public class StatisticsCollectorFactory {
    public StatisticsCollector createStatisticsCollector(long jobId, JobScope jobScope, JobOperation jobOperation, EventPublisher eventPublisher, BackupRestoreJobDao backupRestoreJobDao, ParallelTasksExecutor parallelTasksExecutor) {
        return new StatisticsCollector(jobId, jobScope, jobOperation, eventPublisher, backupRestoreJobDao, parallelTasksExecutor);
    }
}

