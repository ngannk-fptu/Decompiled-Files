/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.index.IndexRecoveryService
 *  com.atlassian.core.util.Clock
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.service.index.IndexRecoveryService;
import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import com.atlassian.confluence.impl.journal.JournalDao;
import com.atlassian.confluence.plugin.descriptor.IndexRecovererModuleDescriptor;
import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.util.Clock;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.google.common.base.Preconditions;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalCleaner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(JournalCleaner.class);
    private final ClusterConfigurationHelper clusterConfigurationHelper;
    private final PluginAccessor pluginAccessor;
    private final IndexRecoveryService indexRecoveryService;
    private final JournalDao journalDao;
    private final Clock clock;
    private final long defaultTimeToLiveMillis;

    public JournalCleaner(ClusterConfigurationHelper clusterConfigurationHelper, PluginAccessor pluginAccessor, IndexRecoveryService indexRecoveryService, JournalDao journalDao, long defaultTimeToLiveMillis) {
        this(clusterConfigurationHelper, pluginAccessor, indexRecoveryService, journalDao, new DefaultClock(), defaultTimeToLiveMillis);
    }

    public JournalCleaner(ClusterConfigurationHelper clusterConfigurationHelper, PluginAccessor pluginAccessor, IndexRecoveryService indexRecoveryService, JournalDao journalDao, Clock clock, long defaultTimeToLiveMillis) {
        this.clock = (Clock)Preconditions.checkNotNull((Object)clock);
        this.clusterConfigurationHelper = (ClusterConfigurationHelper)Preconditions.checkNotNull((Object)clusterConfigurationHelper);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
        this.indexRecoveryService = (IndexRecoveryService)Preconditions.checkNotNull((Object)indexRecoveryService);
        this.journalDao = (JournalDao)Preconditions.checkNotNull((Object)journalDao);
        Preconditions.checkArgument((defaultTimeToLiveMillis > 0L ? 1 : 0) != 0);
        this.defaultTimeToLiveMillis = defaultTimeToLiveMillis;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        boolean snapshot = this.snapshotIndexJournalEntries();
        int count = this.cleanupIndexJournalEntries();
        return JobRunnerResponse.success((String)("Snapshot = " + snapshot + ", expired entry count = " + count));
    }

    private boolean snapshotIndexJournalEntries() {
        boolean shouldSnapshot;
        String property = System.getProperty("create.index.backups");
        boolean bl = shouldSnapshot = property == null ? this.clusterConfigurationHelper.isClusterHomeConfigured() : Boolean.parseBoolean(property);
        if (shouldSnapshot) {
            this.pluginAccessor.getEnabledModuleDescriptorsByClass(IndexRecovererModuleDescriptor.class).forEach(desc -> {
                try {
                    if (!this.indexRecoveryService.createIndexBackup(desc.getJournalId(), desc.getIndexDirName(), desc.getModule())) {
                        log.error("Could not back up index: " + desc.getIndexName());
                    }
                }
                catch (RuntimeException e) {
                    log.error("Could not back up index: " + desc.getIndexName(), (Throwable)e);
                }
            });
        }
        return shouldSnapshot;
    }

    private int cleanupIndexJournalEntries() {
        long ttl = Long.getLong("com.atlassian.confluence.journal.timeToLiveInMillis", this.defaultTimeToLiveMillis);
        return this.journalDao.removeEntriesOlderThan(new Date(this.clock.getCurrentDate().getTime() - ttl));
    }
}

