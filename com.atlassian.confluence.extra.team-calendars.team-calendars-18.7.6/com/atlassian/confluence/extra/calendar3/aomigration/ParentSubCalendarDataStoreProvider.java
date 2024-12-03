/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  net.java.ao.DBParam
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import net.java.ao.DBParam;
import org.json.JSONObject;

public class ParentSubCalendarDataStoreProvider
extends AbstractBandanaSubCalendarProvider {
    private static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore";

    public ParentSubCalendarDataStoreProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    public String getProviderKey() {
        return STORE_KEY;
    }

    @Override
    public boolean requiresNewParent() {
        return false;
    }

    @Override
    public SubCalendarEntity createParent(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        return null;
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getProviderKey()), new DBParam("ID", this.getProperty(theSubCalendar, "id", false, true, "")), new DBParam("PARENT_ID", this.getProperty(theSubCalendar, "parentId", true, false, null)), new DBParam("NAME", (Object)this.getSubCalendarNameTrimmed(theSubCalendar)), new DBParam("DESCRIPTION", this.getProperty(theSubCalendar, "description", true, false, "")), new DBParam("COLOUR", this.getProperty(theSubCalendar, "color", true, true, "subcalendar-blue")), new DBParam("SPACE_KEY", this.getProperty(theSubCalendar, "spaceKey", true, false, "")), new DBParam("TIME_ZONE_ID", this.getProperty(theSubCalendar, "timeZoneId", true, true, null)), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", this.getProperty(theSubCalendar, "creator", true, false, ""))});
    }

    @Override
    public boolean requiresEventsMigration() {
        return false;
    }
}

