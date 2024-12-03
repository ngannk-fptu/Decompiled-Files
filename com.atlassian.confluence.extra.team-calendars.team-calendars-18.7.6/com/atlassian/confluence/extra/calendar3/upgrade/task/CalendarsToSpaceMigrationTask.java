/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.extra.calendar3.DefaultActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarsToSpaceMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class CalendarsToSpaceMigrationTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarsToSpaceMigrationTask.class);
    private final CalendarsToSpaceMigrator defaultCalendarsToSpaceMigrator;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier queryDSLSupplier;

    public CalendarsToSpaceMigrationTask(@Qualifier(value="defaultCalendarsToSpaceMigrator") CalendarsToSpaceMigrator defaultCalendarsToSpaceMigrator, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this.defaultCalendarsToSpaceMigrator = defaultCalendarsToSpaceMigrator;
        this.queryDSLMapper = queryDSLMapper;
        this.queryDSLSupplier = queryDSLSupplier;
    }

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_16);
    }

    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        try {
            this.defaultCalendarsToSpaceMigrator.doMigrate(new DefaultActiveObjectsServiceWrapper(activeObjects, this.queryDSLMapper, this.queryDSLSupplier));
        }
        catch (Exception e) {
            LOGGER.warn("CalendarsToSpaceMigrationTask.upgrade failed", (Throwable)e);
        }
    }
}

