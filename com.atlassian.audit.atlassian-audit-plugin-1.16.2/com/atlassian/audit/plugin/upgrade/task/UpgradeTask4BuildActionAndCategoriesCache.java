/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.plugin.upgrade.task;

import com.atlassian.audit.cache.schedule.BuildCacheJobScheduler;
import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeTask4BuildActionAndCategoriesCache
extends AuditUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpgradeTask4BuildActionAndCategoriesCache.class);
    private static final int BUILD_NUMBER = 4;
    private final BuildCacheJobScheduler buildCacheJobScheduler;

    public UpgradeTask4BuildActionAndCategoriesCache(BuildCacheJobScheduler buildCacheJobScheduler) {
        this.buildCacheJobScheduler = Objects.requireNonNull(buildCacheJobScheduler, "buildCacheJobScheduler");
    }

    public int getBuildNumber() {
        return 4;
    }

    @Nonnull
    public String getShortDescription() {
        return "Schedules building the audit actions and audit categories cache";
    }

    @Nullable
    public Collection<Message> doUpgrade() throws Exception {
        try {
            this.buildCacheJobScheduler.scheduleIfNeeded();
        }
        catch (Exception exception) {
            log.error("Failed to schedule a job to build the audit categories and audit actions (AKA summaries) cache.", (Throwable)exception);
            return Collections.singletonList(new Message(){

                public String getKey() {
                    return "atlassian.audit.backend.upgrade.task.4.failed.reason.scheduler.exception";
                }

                public Serializable[] getArguments() {
                    return new Serializable[0];
                }
            });
        }
        return Collections.emptyList();
    }
}

