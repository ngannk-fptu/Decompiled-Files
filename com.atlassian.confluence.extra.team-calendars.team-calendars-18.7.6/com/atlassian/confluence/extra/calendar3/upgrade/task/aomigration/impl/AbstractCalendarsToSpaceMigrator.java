/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarInSpaceEntity;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCalendarsToSpaceMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCalendarsToSpaceMigrator.class);

    protected void addCalendarToSpaceView(ActiveObjects activeObjects, SubCalendarEntity entity) {
        try {
            SubCalendarInSpaceEntity[] subCalendarInSpaceEntities = (SubCalendarInSpaceEntity[])activeObjects.find(SubCalendarInSpaceEntity.class, Query.select().where("SPACE_KEY = ? AND SUB_CALENDAR_ID = ?", new Object[]{entity.getSpaceKey(), entity.getID()}));
            if (subCalendarInSpaceEntities == null || subCalendarInSpaceEntities.length == 0) {
                activeObjects.create(SubCalendarInSpaceEntity.class, new DBParam[]{new DBParam("SPACE_KEY", (Object)entity.getSpaceKey()), new DBParam("SUB_CALENDAR_ID", (Object)entity.getID())});
            }
        }
        catch (Exception e) {
            LOGGER.debug("Error when migrating calendar with id:" + entity.getID() + " to space: " + entity.getSpaceKey(), (Throwable)e);
        }
    }

    protected void configure(ActiveObjects ao) {
        ao.migrate(new Class[]{SubCalendarEntity.class, SubCalendarInSpaceEntity.class});
    }
}

