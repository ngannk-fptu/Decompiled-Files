/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarContentTypeMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.StatusProvider;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.impl.AbstractStatusProvider;
import java.util.Iterator;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCalendarContentTypeMigrationManager
extends AbstractStatusProvider
implements CalendarContentTypeMigrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCalendarContentTypeMigrationManager.class);
    private CalendarContentTypeManager calendarContentTypeManager;

    public DefaultCalendarContentTypeMigrationManager(CalendarContentTypeManager calendarContentTypeManager) {
        this.calendarContentTypeManager = calendarContentTypeManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean doMigrate(ActiveObjectsServiceWrapper activeObjectsWrapper) {
        if (this.status.compareAndSet((Object)StatusProvider.RunningStatus.NOT_RUNNING, (Object)StatusProvider.RunningStatus.RUNNING)) {
            try {
                SubCalendarEntity[] subCalendarEntities;
                for (SubCalendarEntity subCalendarEntity : subCalendarEntities = this.getParentSubCalendarEnttity(activeObjectsWrapper)) {
                    LOGGER.debug("create calendarContentType with ID {} and Name {}", (Object)subCalendarEntity.getID(), (Object)subCalendarEntity.getName());
                    this.calendarContentTypeManager.createCalendarContentTypeFor(subCalendarEntity);
                }
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
    public boolean deleteAllCalendarContentTypes() {
        try {
            Iterator<CustomContentEntityObject> calendarContentTypeIterators = this.calendarContentTypeManager.getAllSubCalendarContent();
            while (calendarContentTypeIterators.hasNext()) {
                CustomContentEntityObject customContentEntityObject = calendarContentTypeIterators.next();
                LOGGER.debug("remove calendarContentType {}", (Object)customContentEntityObject.getTitle());
                this.calendarContentTypeManager.removeCalendarContentEntity(customContentEntityObject);
            }
            return true;
        }
        catch (Exception exception) {
            LOGGER.error("Error removing CalendarContentType entity", (Throwable)exception);
            return false;
        }
    }

    protected SubCalendarEntity[] getParentSubCalendarEnttity(ActiveObjectsServiceWrapper activeObjectsWrapper) {
        return (SubCalendarEntity[])activeObjectsWrapper.getActiveObjects().find(SubCalendarEntity.class, Query.select().where("PARENT_ID IS NULL AND SUBSCRIPTION_ID IS NULL", new Object[0]));
    }
}

