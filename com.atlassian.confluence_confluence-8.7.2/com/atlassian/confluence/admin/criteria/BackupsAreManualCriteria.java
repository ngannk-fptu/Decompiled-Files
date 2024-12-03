/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.IgnorableAdminTaskCriteria;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupsAreManualCriteria
extends IgnorableAdminTaskCriteria {
    private static final Logger log = LoggerFactory.getLogger(BackupsAreManualCriteria.class);
    private final SettingsManager settingsManager;
    private final ScheduledJobManager scheduledJobManager;

    public BackupsAreManualCriteria(SettingsManager settingsManager, ScheduledJobManager scheduledJobManager) {
        super("backups-are-manual", settingsManager);
        this.settingsManager = settingsManager;
        this.scheduledJobManager = scheduledJobManager;
    }

    @Override
    public boolean isMet() {
        boolean isAutomatic = ScheduleUtil.isBackupEnabled(this.scheduledJobManager, this.settingsManager);
        return !isAutomatic;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        return this.isMet() ? "Disabled" : "Enabled";
    }

    @Override
    public boolean hasLiveValue() {
        return false;
    }
}

