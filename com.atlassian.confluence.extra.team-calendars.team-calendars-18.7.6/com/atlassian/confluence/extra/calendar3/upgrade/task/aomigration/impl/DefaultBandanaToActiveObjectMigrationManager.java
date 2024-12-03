/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.Query
 *  org.json.JSONException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.events.migration.MigrationFinishedCalendarEvent;
import com.atlassian.confluence.extra.calendar3.events.migration.ProgressCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.DisableEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderUsersEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarInSpaceEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectsMigrator;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl.AbstractStatusProvider;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import net.fortuna.ical4j.data.ParserException;
import net.java.ao.DatabaseProvider;
import net.java.ao.Query;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultBandanaToActiveObjectMigrationManager
extends AbstractStatusProvider
implements BandanaToActiveObjectMigrationManager,
DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBandanaToActiveObjectMigrationManager.class);
    public static final int MAX_HOLDING_MSG = 50;
    protected ArrayBlockingQueue<ProgressCalendarEvent> messages = new ArrayBlockingQueue(50, true);
    private BandanaToActiveObjectsMigrator bandanaToActiveObjectsMigrator;
    private EventPublisher eventPublisher;
    private ActiveObjectsUpgradeTask userNameToUserKeyMigration;
    private List<BandanaSubCalendarsProvider> bandanaSubCalendarsProviders;

    public DefaultBandanaToActiveObjectMigrationManager(EventPublisher eventPublisher, List<BandanaSubCalendarsProvider> bandanaSubCalendarsProviders, BandanaToActiveObjectsMigrator bandanaToActiveObjectsMigrator, ActiveObjectsUpgradeTask userNameToUserKeyMigration) {
        this.bandanaSubCalendarsProviders = bandanaSubCalendarsProviders;
        this.bandanaToActiveObjectsMigrator = bandanaToActiveObjectsMigrator;
        this.userNameToUserKeyMigration = userNameToUserKeyMigration;
        this.eventPublisher = eventPublisher;
        this.eventPublisher.register((Object)this);
    }

    @Override
    public boolean doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper) throws JSONException, ParserException, IOException {
        return this.doMigrate(activeObjectsWrapper, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean withUserNameToUserKeyMigration) throws JSONException, ParserException, IOException {
        if (this.status.compareAndSet((Object)StatusProvider.RunningStatus.NOT_RUNNING, (Object)StatusProvider.RunningStatus.RUNNING)) {
            try {
                if (withUserNameToUserKeyMigration && this.userNameToUserKeyMigration != null) {
                    this.userNameToUserKeyMigration.upgrade(null, activeObjectsWrapper.getActiveObjects());
                }
                this.doMigrateInternal(activeObjectsWrapper);
                boolean bl = true;
                return bl;
            }
            finally {
                this.status.set((Object)StatusProvider.RunningStatus.NOT_RUNNING);
            }
        }
        LOGGER.debug("TC migration process already run. Will discard this request");
        return false;
    }

    @Override
    public boolean forceDeleteAllData(ActiveObjectsServiceWrapper activeObjectsWrapper) {
        ActiveObjects activeObjects = activeObjectsWrapper.getActiveObjects();
        return (Boolean)activeObjects.executeInTransaction(() -> {
            activeObjects.deleteWithSQL(EventRecurrenceExclusionEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(ExtraSubCalendarPropertyEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(InviteeEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(SubCalendarGroupRestrictionEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(SubCalendarUserRestrictionEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(EventEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(DisableEventTypeEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(CustomEventTypeEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(ReminderSettingEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(ReminderUsersEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(JiraReminderEventEntity.class, null, new Object[0]);
            activeObjects.deleteWithSQL(SubCalendarInSpaceEntity.class, null, new Object[0]);
            SubCalendarEntity[] subCalendarEntities = (SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().limit(1));
            if (subCalendarEntities != null && subCalendarEntities.length > 0) {
                DatabaseProvider currentDatabaseProvider = subCalendarEntities[0].getEntityManager().getProvider();
                activeObjects.deleteWithSQL(SubCalendarEntity.class, currentDatabaseProvider.quote("PARENT_ID") + " IS NOT NULL AND " + currentDatabaseProvider.quote("SUBSCRIPTION_ID") + " IS NOT NULL", new Object[0]);
                activeObjects.deleteWithSQL(SubCalendarEntity.class, currentDatabaseProvider.quote("PARENT_ID") + " IS NOT NULL", new Object[0]);
                activeObjects.deleteWithSQL(SubCalendarEntity.class, currentDatabaseProvider.quote("SUBSCRIPTION_ID") + " IS NOT NULL", new Object[0]);
                activeObjects.deleteWithSQL(SubCalendarEntity.class, null, new Object[0]);
            }
            return true;
        });
    }

    protected void doMigrateInternal(ActiveObjectsServiceWrapper activeObjectsWrapper) throws IOException, ParserException, JSONException {
        LOGGER.info("=======================Start Bandana to ActiveObject migration=======================");
        this.bandanaToActiveObjectsMigrator.doMigrate(activeObjectsWrapper, this.bandanaSubCalendarsProviders);
        LOGGER.info("=======================End Bandana to ActiveObject migration=========================");
    }

    @Override
    public String getInProgressMessage(I18NBean i18NBean) {
        return "<div class=\"error\"><span class=\"error\">" + i18NBean.getText("calendar3.error.migrationinprogress") + "</span></div>";
    }

    @Override
    public List<ProgressCalendarEvent> getMigrationEvents() {
        ArrayList<ProgressCalendarEvent> returnMessages = new ArrayList<ProgressCalendarEvent>(50);
        this.messages.drainTo(returnMessages);
        return returnMessages;
    }

    @EventListener
    public void handleProgressCalendarEvent(ProgressCalendarEvent event) {
        if (this.status.get() == StatusProvider.RunningStatus.NOT_RUNNING) {
            LOGGER.debug("TC migration is not running. Will discard this event log [{}]", (Object)event);
            return;
        }
        if (event != null) {
            LOGGER.info(event.toString());
            this.addEvent(event);
        } else {
            LOGGER.error("Null ProgressCalendarEvent was received");
        }
    }

    @EventListener
    public void handleMigrationFinished(MigrationFinishedCalendarEvent event) {
        if (this.status.compareAndSet((Object)StatusProvider.RunningStatus.RUNNING, (Object)StatusProvider.RunningStatus.NOT_RUNNING)) {
            this.addEvent(event);
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    private void addEvent(ProgressCalendarEvent event) {
        if (!this.messages.offer(event) && this.messages.poll() != null) {
            this.messages.offer(event);
        }
    }
}

