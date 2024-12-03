/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.UtilTimerStack
 *  net.java.ao.Query
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarRestrictionsMigrator;
import com.atlassian.confluence.extra.calendar3.events.migration.BandanaProviderProgressCalendarEvent;
import com.atlassian.confluence.extra.calendar3.events.migration.MigrationFinishedCalendarEvent;
import com.atlassian.confluence.extra.calendar3.events.migration.SubCalendarProgressCalendarEvent;
import com.atlassian.confluence.extra.calendar3.exception.CalendarMigrationException;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaSubCalendarAccessor;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectsMigrator;
import com.atlassian.confluence.extra.calendar3.util.UserKeyMigratorTransformer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.UtilTimerStack;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.java.ao.Query;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBandanaToActiveObjectsMigrator
implements BandanaToActiveObjectsMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBandanaToActiveObjectsMigrator.class);
    private BandanaSubCalendarAccessor bandanaSubCalendarAccessor;
    private SettingsManager settingsManager;
    private UserAccessor userAccessor;
    private EventPublisher eventPublisher;
    private SubCalendarRestrictionsMigrator subCalendarRestrictionsMigrator;

    public DefaultBandanaToActiveObjectsMigrator(SettingsManager settingsManager, UserAccessor userAccessor, BandanaSubCalendarAccessor bandanaSubCalendarAccessor, EventPublisher eventPublisher, SubCalendarRestrictionsMigrator subCalendarRestrictionsMigrator) {
        this.bandanaSubCalendarAccessor = bandanaSubCalendarAccessor;
        this.userAccessor = userAccessor;
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
        this.subCalendarRestrictionsMigrator = subCalendarRestrictionsMigrator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void migrateSubCalendar(ActiveObjectsServiceWrapper activeObjectsWrapper, BandanaSubCalendarsProvider provider, String subCalendarId) throws JSONException, IOException, ParserException {
        UtilTimerStack.push((String)("BandanaToActiveObjectsMigrator.migrateSubCalendar() -- " + subCalendarId));
        try {
            SubCalendarEntity subCalendarEntity;
            JSONObject subCalendarJson = this.bandanaSubCalendarAccessor.getSubCalendarJson(provider, subCalendarId);
            Calendar iCalendar = null;
            SubCalendarEntity parentSubCalendarEntity = null;
            iCalendar = this.bandanaSubCalendarAccessor.getSubCalendarContent(provider, subCalendarId);
            if (!this.isUserKeyMigrationPreviouslyDone(iCalendar)) {
                iCalendar = new UserKeyMigratorTransformer(this.settingsManager.getGlobalSettings().getBaseUrl(), this.userAccessor).transform(iCalendar);
            }
            if ((subCalendarEntity = provider.createSubCalendarEntity(activeObjectsWrapper, this.isUserKeyMigrationPreviouslyDone(iCalendar), subCalendarJson)) == null) {
                return;
            }
            if (provider.requiresNewParent()) {
                parentSubCalendarEntity = provider.createParent(activeObjectsWrapper, this.isUserKeyMigrationPreviouslyDone(iCalendar), subCalendarJson);
                if (parentSubCalendarEntity == null) {
                    throw new CalendarMigrationException(String.format("Provider %s should return an instance for parent Sub Calendar", provider.getProviderKey()));
                }
                subCalendarEntity.setParent(parentSubCalendarEntity);
                subCalendarEntity.save();
            }
            this.subCalendarRestrictionsMigrator.migrateRestrictions(activeObjectsWrapper.getActiveObjects(), provider, subCalendarId, parentSubCalendarEntity == null ? subCalendarId : parentSubCalendarEntity.getID());
            if (provider.requiresEventsMigration()) {
                if (iCalendar == null || subCalendarEntity == null) {
                    throw new CalendarMigrationException("Cannot migrate events because SubCalendarEntity or Calendar is null");
                }
                provider.createEvents(activeObjectsWrapper, subCalendarEntity, iCalendar);
            }
        }
        finally {
            UtilTimerStack.pop((String)("BandanaToActiveObjectsMigrator.migrateSubCalendar() -- " + subCalendarId));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void migrateProvider(ActiveObjectsServiceWrapper activeObjectsWrapper, int totalProviders, int index, BandanaSubCalendarsProvider provider) throws JSONException, ParserException, IOException {
        UtilTimerStack.push((String)("BandanaToActiveObjectsMigrator.migrateProvider() -- " + provider.toString()));
        try {
            Set<String> subCalendarIds = this.bandanaSubCalendarAccessor.getSubCalendarIds(provider);
            float currentPercent = (float)(index - 1) / (float)totalProviders;
            float nextPercent = (float)index / (float)totalProviders;
            boolean subCalendarIndex = true;
            int size = subCalendarIds.size() + 1;
            for (String subCalendarId : subCalendarIds) {
                if (!this.hasNotBeenMigrated(activeObjectsWrapper, subCalendarId)) continue;
                try {
                    this.migrateSubCalendar(activeObjectsWrapper, provider, subCalendarId);
                    float subPercent = currentPercent + (float)subCalendarIndex * 0.1f / (float)size;
                    this.eventPublisher.publish((Object)new SubCalendarProgressCalendarEvent(provider, AuthenticatedUserThreadLocal.get(), subCalendarId, subPercent));
                }
                catch (CalendarMigrationException ex) {
                    LOGGER.warn("The migrated data might not correct for sub calendar {} with message : {}", (Object)subCalendarId, (Object)ex.getMessage());
                    this.logDetail(subCalendarId, ex);
                }
                catch (Exception ex) {
                    LOGGER.error("Exception during migration for sub calendar {} with message: {}", (Object)subCalendarId, (Object)ex.getMessage());
                    this.logDetail(subCalendarId, ex);
                }
            }
            this.eventPublisher.publish((Object)new BandanaProviderProgressCalendarEvent(provider, AuthenticatedUserThreadLocal.get(), nextPercent));
        }
        finally {
            UtilTimerStack.pop((String)("BandanaToActiveObjectsMigrator.migrateProvider() -- " + provider.toString()));
        }
    }

    @Override
    public void doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper, List<BandanaSubCalendarsProvider> providers) throws IOException, ParserException, JSONException {
        int size = providers.size() + 1;
        for (int index = 1; index < size; ++index) {
            BandanaSubCalendarsProvider bandanaSubCalendarsProvider = providers.get(index - 1);
            LOGGER.info("Start migrating for {}", (Object)bandanaSubCalendarsProvider.getProviderKey());
            this.migrateProvider(activeObjectsWrapper, size, index, bandanaSubCalendarsProvider);
            LOGGER.info("Start migrating for {} ===> DONE", (Object)bandanaSubCalendarsProvider.getProviderKey());
        }
        this.eventPublisher.publish((Object)new MigrationFinishedCalendarEvent(this, AuthenticatedUserThreadLocal.get()));
    }

    protected boolean isUserKeyMigrationPreviouslyDone(Calendar iCalendar) {
        if (iCalendar == null) {
            return true;
        }
        Object migratedProperty = iCalendar.getProperty("X-MIGRATED-FOR-USER-KEY");
        return migratedProperty != null;
    }

    protected boolean hasNotBeenMigrated(ActiveObjectsServiceWrapper activeObjectsWrapper, String subCalendarId) {
        int count = activeObjectsWrapper.getActiveObjects().count(SubCalendarEntity.class, Query.select().where("ID = ?", new Object[]{subCalendarId}));
        return count <= 0;
    }

    private void logDetail(String subCalendarId, Exception ex) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exception during migration for sub calendar {}", (Object)subCalendarId, (Object)ex);
        }
    }
}

