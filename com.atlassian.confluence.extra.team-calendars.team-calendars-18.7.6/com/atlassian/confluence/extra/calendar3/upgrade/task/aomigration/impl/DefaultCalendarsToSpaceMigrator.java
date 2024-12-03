/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarInSpaceEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarsToSpaceMigrator;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl.AbstractCalendarsToSpaceMigrator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import net.java.ao.DatabaseProvider;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCalendarsToSpaceMigrator
extends AbstractCalendarsToSpaceMigrator
implements CalendarsToSpaceMigrator {
    private final SpaceManager spaceManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCalendarsToSpaceMigrator.class);

    public DefaultCalendarsToSpaceMigrator(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public void doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper) {
        LOGGER.info("Start migrating all calendars that have related space to spaces");
        ActiveObjects activeObjects = activeObjectsWrapper.getActiveObjects();
        this.configure(activeObjects);
        this.clearSpaceCalendars(activeObjects);
        SubCalendarEntity[] subCalendarEntities = (SubCalendarEntity[])activeObjects.find(SubCalendarEntity.class, Query.select().where("PARENT_ID IS NULL AND SPACE_KEY IS NOT NULL", new Object[0]));
        LOGGER.info("Found: " + subCalendarEntities.length + " calendars");
        for (SubCalendarEntity subCalendarEntity : subCalendarEntities) {
            if (!"com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore".equals(subCalendarEntity.getStoreKey()) || !StringUtils.isNotBlank(subCalendarEntity.getSpaceKey())) continue;
            Space space = this.spaceManager.getSpace(subCalendarEntity.getSpaceKey());
            if (space == null) {
                subCalendarEntity.setSpaceKey(null);
                subCalendarEntity.save();
                continue;
            }
            this.addCalendarToSpaceView(activeObjects, subCalendarEntity);
        }
    }

    private void clearSpaceCalendars(ActiveObjects activeObjects) {
        LOGGER.info("Clearing all space calendars");
        SubCalendarInSpaceEntity[] subCalendarInSpaceEntities = (SubCalendarInSpaceEntity[])activeObjects.find(SubCalendarInSpaceEntity.class, Query.select().limit(1));
        if (subCalendarInSpaceEntities != null && subCalendarInSpaceEntities.length > 0) {
            DatabaseProvider provider = subCalendarInSpaceEntities[0].getEntityManager().getProvider();
            activeObjects.deleteWithSQL(SubCalendarInSpaceEntity.class, provider.quote("ID") + " > ?", new Object[]{0});
        }
    }
}

