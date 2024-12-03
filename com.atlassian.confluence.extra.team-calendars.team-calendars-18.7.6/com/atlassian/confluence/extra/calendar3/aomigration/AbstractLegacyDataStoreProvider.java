/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  net.java.ao.DBParam
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.aomigration.AbstractBandanaSubCalendarProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.UUIDGenerate;
import com.atlassian.confluence.user.UserAccessor;
import net.java.ao.DBParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLegacyDataStoreProvider
extends AbstractBandanaSubCalendarProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLegacyDataStoreProvider.class);

    public AbstractLegacyDataStoreProvider(UserAccessor userAccessor) {
        super(userAccessor);
    }

    @Override
    public boolean requiresNewParent() {
        return true;
    }

    @Override
    public SubCalendarEntity createParent(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        String subCalendarId = (String)this.getProperty(theSubCalendar, "id", false, true, "");
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getParentStore()), new DBParam("ID", (Object)subCalendarId), new DBParam("PARENT_ID", this.getParentId(theSubCalendar)), new DBParam("NAME", (Object)this.getSubCalendarNameTrimmed(theSubCalendar)), new DBParam("DESCRIPTION", this.getProperty(theSubCalendar, "description", true, false, "")), new DBParam("COLOUR", this.getProperty(theSubCalendar, "color", true, true, "subcalendar-blue")), new DBParam("SPACE_KEY", this.getProperty(theSubCalendar, "spaceKey", true, false, "")), new DBParam("TIME_ZONE_ID", this.getProperty(theSubCalendar, "timeZoneId", true, true, null)), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", this.getProperty(theSubCalendar, "creator", true, false, ""))});
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getStoreKey()), new DBParam("ID", (Object)UUIDGenerate.generate()), new DBParam("PARENT_ID", this.getParentId(theSubCalendar)), new DBParam("NAME", (Object)this.getSubCalendarNameTrimmed(theSubCalendar)), new DBParam("DESCRIPTION", this.getProperty(theSubCalendar, "description", true, false, "")), new DBParam("COLOUR", this.getProperty(theSubCalendar, "color", true, true, "subcalendar-blue")), new DBParam("SPACE_KEY", this.getProperty(theSubCalendar, "spaceKey", true, false, "")), new DBParam("TIME_ZONE_ID", this.getProperty(theSubCalendar, "timeZoneId", true, true, null)), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", this.getProperty(theSubCalendar, "creator", true, false, ""))});
    }

    @Override
    protected Object getParentId(JSONObject theSubCalendar) {
        return null;
    }

    private String getParentStore() {
        return "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore";
    }

    protected abstract String getStoreKey();
}

