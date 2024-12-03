/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 */
package com.atlassian.confluence.util;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;

public class SchedulerLifecycle
implements LifecycleItem {
    private UpgradeManager upgradeManager;
    private LifecycleAwareSchedulerService schedulerService;

    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    public void setSchedulerService(LifecycleAwareSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void startup(LifecycleContext context) throws SchedulerServiceException {
        if (!this.upgradeManager.needUpgrade()) {
            this.schedulerService.start();
        }
    }

    public void shutdown(LifecycleContext context) throws SchedulerServiceException {
        ScheduleUtil.pauseAndFlushSchedulerService(this.schedulerService);
    }
}

