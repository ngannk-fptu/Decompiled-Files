/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.RemoveDuplicatedExcludeDateUpgradeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveDuplicatedInviteeDateUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveDuplicatedExcludeDateUpgradeTask.class);

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_19);
    }

    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        LOGGER.info("====================Start Upgrade Task RemoveDuplicatedExcludeDateUpgradeTask==============================");
        LOGGER.info("====================Let's watch dog service to do it job==============================");
        LOGGER.info("====================Finish Upgrade Task RemoveDuplicatedInviteeDateUpgradeTask==============================");
    }
}

