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
import com.atlassian.confluence.extra.calendar3.DefaultActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarContentTypeMigrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarContentTypeUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarContentTypeUpgradeTask.class);
    private CalendarContentTypeMigrationManager calendarContentTypeMigrationManager;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier queryDSLSupplier;

    public CalendarContentTypeUpgradeTask(CalendarContentTypeMigrationManager calendarContentTypeMigrationManager, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this.calendarContentTypeMigrationManager = calendarContentTypeMigrationManager;
        this.queryDSLMapper = queryDSLMapper;
        this.queryDSLSupplier = queryDSLSupplier;
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_12);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        LOG.info("====================Start Run Upgrade Task Custom Content Type Team Calendar==============================");
        try {
            this.calendarContentTypeMigrationManager.doMigrate(new DefaultActiveObjectsServiceWrapper(ao, this.queryDSLMapper, this.queryDSLSupplier));
        }
        catch (Exception ex) {
            LOG.error("Exception during CalendarContentTypeUpgradeTask", (Throwable)ex);
        }
        LOG.info("====================End Run Upgrade Task Custom Content Type Team Calendar==============================");
    }
}

