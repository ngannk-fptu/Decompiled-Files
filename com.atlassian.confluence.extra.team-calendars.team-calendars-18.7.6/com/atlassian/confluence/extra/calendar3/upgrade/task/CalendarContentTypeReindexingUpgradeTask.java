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

public class CalendarContentTypeReindexingUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarContentTypeReindexingUpgradeTask.class);
    private CalendarContentTypeMigrationManager calendarContentTypeMigrationManager;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier queryDSLSupplier;

    public CalendarContentTypeReindexingUpgradeTask(CalendarContentTypeMigrationManager calendarContentTypeMigrationManager, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this.calendarContentTypeMigrationManager = calendarContentTypeMigrationManager;
        this.queryDSLMapper = queryDSLMapper;
        this.queryDSLSupplier = queryDSLSupplier;
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_13);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        LOG.info("====================Start Run Upgrade Task for reindexing Custom Content Type Team Calendar==============================");
        try {
            boolean result = this.calendarContentTypeMigrationManager.deleteAllCalendarContentTypes();
            if (result) {
                this.calendarContentTypeMigrationManager.doMigrate(new DefaultActiveObjectsServiceWrapper(ao, this.queryDSLMapper, this.queryDSLSupplier));
            } else {
                LOG.warn("Could not delete all calendar content type");
            }
        }
        catch (Exception ex) {
            LOG.error("Exception during CalendarContentTypeReindexingUpgradeTask", (Throwable)ex);
        }
        LOG.info("====================End Upgrade Task for reindexing Custom Content Type Team Calendar==============================");
    }
}

