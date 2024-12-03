/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.fugue.Iterables
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsTracker;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarsToSpaceMigrator;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl.AbstractCalendarsToSpaceMigrator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.fugue.Iterables;
import java.util.Collection;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedCalendarsToSpaceMigrator
extends AbstractCalendarsToSpaceMigrator
implements CalendarsToSpaceMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedCalendarsToSpaceMigrator.class);
    private final EmbeddedSubCalendarsTracker embeddedSubCalendarsTracker;
    private final SpaceManager spaceManager;

    public EmbeddedCalendarsToSpaceMigrator(EmbeddedSubCalendarsTracker embeddedSubCalendarsTracker, SpaceManager spaceManager) {
        this.embeddedSubCalendarsTracker = embeddedSubCalendarsTracker;
        this.spaceManager = spaceManager;
    }

    @Override
    public void doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper) {
        LOGGER.info("Start migrating all calendars that do not have related space to spaces but are embedded in ONLY one page");
        ActiveObjects activeObjects = activeObjectsWrapper.getActiveObjects();
        this.configure(activeObjects);
        Collection<String> embedSubCalendars = this.embeddedSubCalendarsTracker.getEmbedSubCalendars();
        for (String calendarId : embedSubCalendars) {
            try {
                SubCalendarEntity subCalendar;
                SubCalendarEntity[] subCalendarEntities;
                String spaceKey;
                Space space;
                ContentEntityObject page;
                Collection<ContentEntityObject> pages = this.embeddedSubCalendarsTracker.getContentEmbeddingSubCalendar(calendarId);
                if (pages.size() != 1 || !((page = (ContentEntityObject)Iterables.first(pages).getOrNull()) instanceof AbstractPage) || (space = this.spaceManager.getSpace(spaceKey = ((AbstractPage)page).getSpaceKey())) == null || (subCalendarEntities = (SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().where("ID = ?", new Object[]{calendarId}))) == null || subCalendarEntities.length <= 0 || !"com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore".equals((subCalendar = subCalendarEntities[0]).getStoreKey()) || !StringUtils.isBlank(subCalendar.getSpaceKey())) continue;
                subCalendar.setSpaceKey(spaceKey);
                subCalendar.save();
                this.addCalendarToSpaceView(activeObjects, subCalendar);
            }
            catch (Exception e) {
                LOGGER.error("Error when migrating calendar with id:" + calendarId, (Throwable)e);
            }
        }
    }
}

