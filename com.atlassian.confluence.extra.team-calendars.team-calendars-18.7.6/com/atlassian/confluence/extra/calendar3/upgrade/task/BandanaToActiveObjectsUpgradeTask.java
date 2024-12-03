/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.extra.calendar3.DefaultActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.events.ActiveObjectsInitializedEvent;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarModelVersion;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectMigrationManager;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BandanaToActiveObjectsUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(BandanaToActiveObjectsUpgradeTask.class);
    private BuildInformationManager buildInformationManager;
    private BandanaToActiveObjectMigrationManager bandanaToActiveObjectMigrationManager;
    private final EventPublisher eventPublisher;
    private final QueryDSLMapper queryDSLMapper;
    private final QueryDSLSupplier queryDSLSupplier;

    public BuildInformationManager getBuildInformationManager() {
        return this.buildInformationManager;
    }

    public BandanaToActiveObjectsUpgradeTask(BuildInformationManager buildInformationManager, BandanaToActiveObjectMigrationManager bandanaToActiveObjectMigrationManager, EventPublisher eventPublisher, QueryDSLMapper queryDSLMapper, QueryDSLSupplier queryDSLSupplier) {
        this.buildInformationManager = buildInformationManager;
        this.bandanaToActiveObjectMigrationManager = bandanaToActiveObjectMigrationManager;
        this.eventPublisher = eventPublisher;
        this.queryDSLMapper = queryDSLMapper;
        this.queryDSLSupplier = queryDSLSupplier;
    }

    public ModelVersion getModelVersion() {
        this.eventPublisher.publish((Object)new ActiveObjectsInitializedEvent());
        return ModelVersion.valueOf((String)CalendarModelVersion.CALENDAR_MODEL_VERSION_11);
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        this.configure(ao);
        LOGGER.info("Start migrating for Bandana to AO");
        try {
            this.bandanaToActiveObjectMigrationManager.doMigrate(new DefaultActiveObjectsServiceWrapper(ao, this.queryDSLMapper, this.queryDSLSupplier));
        }
        catch (Exception e) {
            LOGGER.error("Error during migration Bandana to ActiveObject", (Throwable)e);
        }
        LOGGER.info("Finished migrating for Bandana to AO");
    }

    private void configure(ActiveObjects ao) {
        ao.migrate(new Class[]{SubCalendarEntity.class, ExtraSubCalendarPropertyEntity.class, SubCalendarUserRestrictionEntity.class, SubCalendarGroupRestrictionEntity.class, EventEntity.class, InviteeEntity.class, EventRecurrenceExclusionEntity.class});
    }
}

