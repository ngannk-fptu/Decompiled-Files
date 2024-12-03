/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.util.stats.JiraStats
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.scheduler.caesium.impl.stats;

import com.atlassian.jira.util.stats.JiraStats;
import com.atlassian.scheduler.caesium.impl.stats.CaesiumSchedulerStats;
import com.atlassian.scheduler.caesium.impl.stats.ManagedCaesiumSchedulerStats;
import com.atlassian.scheduler.caesium.impl.stats.NoOpCaesiumSchedulerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeCaesiumSchedulerStatsFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SafeCaesiumSchedulerStatsFactory.class);
    private static final String COMMON_PREFIX = "[JIRA-STATS] ";

    public static CaesiumSchedulerStats create() {
        try {
            if (Boolean.getBoolean("atlassian.SchedulerService.stats.disabled")) {
                LOG.warn("{}[{}] stats disabled", (Object)COMMON_PREFIX, (Object)"SchedulerStats");
                return new NoOpCaesiumSchedulerStats();
            }
            return (CaesiumSchedulerStats)JiraStats.create(ManagedCaesiumSchedulerStats.class, ManagedCaesiumSchedulerStats.Data::new, (boolean)false);
        }
        catch (LinkageError e) {
            LOG.info("{}[{}] stats not available in classpath", (Object)COMMON_PREFIX, (Object)"SchedulerStats");
            return new NoOpCaesiumSchedulerStats();
        }
    }
}

