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
import com.atlassian.confluence.extra.calendar3.JsonPropertyGetter;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.UserAccessor;
import java.util.LinkedList;
import java.util.List;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.java.ao.DBParam;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBandanaSubCalendarProvider
implements BandanaSubCalendarsProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBandanaSubCalendarProvider.class);
    private final UserAccessor userAccessor;

    public AbstractBandanaSubCalendarProvider(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @Override
    public SubCalendarEntity createSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, boolean subCalendarMigratedForUserKey, JSONObject theSubCalendar) {
        String subCalendarId = (String)this.getProperty(theSubCalendar, "id", false, true, "");
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().create(SubCalendarEntity.class, new DBParam[]{new DBParam("STORE_KEY", (Object)this.getProviderKey()), new DBParam("ID", (Object)subCalendarId), new DBParam("PARENT_ID", this.getParentId(theSubCalendar)), new DBParam("NAME", (Object)this.getSubCalendarNameTrimmed(theSubCalendar)), new DBParam("DESCRIPTION", this.getProperty(theSubCalendar, "description", true, false, "")), new DBParam("COLOUR", this.getProperty(theSubCalendar, "color", true, true, "subcalendar-blue")), new DBParam("SPACE_KEY", this.getProperty(theSubCalendar, "spaceKey", true, false, "")), new DBParam("TIME_ZONE_ID", this.getProperty(theSubCalendar, "timeZoneId", true, false, null)), new DBParam("CREATED", (Object)System.currentTimeMillis()), new DBParam("CREATOR", this.getProperty(theSubCalendar, "creator", true, false, ""))});
    }

    protected String getSubCalendarNameTrimmed(JSONObject theSubCalendar) {
        String subCalendarName = (String)this.getProperty(theSubCalendar, "name", false, true, "");
        if (subCalendarName.length() > 255) {
            LOG.info(String.format("The name of sub-calendar %s from store %s is longer than %d characters. It will be truncated.", this.getProperty(theSubCalendar, "id", false, true, ""), this.getProviderKey(), 255));
            subCalendarName = StringUtils.substring(subCalendarName, 0, 255);
        }
        return subCalendarName;
    }

    @Override
    public List<EventEntity> createEvents(ActiveObjectsServiceWrapper activeObjectsWrapper, SubCalendarEntity subCalendarEntity, Calendar iCalendarObject) {
        LinkedList<EventEntity> eventEntities = new LinkedList<EventEntity>();
        ComponentList vEventComponents = iCalendarObject.getComponents("VEVENT");
        for (VEvent vEventComponent : vEventComponents) {
            EventEntity eventEntity = activeObjectsWrapper.createEventEntity(subCalendarEntity, vEventComponent);
            activeObjectsWrapper.createInviteeEntity(eventEntity, vEventComponent, this.userAccessor);
            activeObjectsWrapper.createEventRecurrenceExclusionEntity(eventEntity, vEventComponent);
            eventEntities.add(eventEntity);
        }
        return eventEntities;
    }

    protected SubCalendarEntity getSubCalendarEntity(ActiveObjectsServiceWrapper activeObjectsWrapper, String id) {
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().get(SubCalendarEntity.class, (Object)id);
    }

    protected Object getProperty(JSONObject theSubCalendar, String property, boolean optional, boolean warnIfAbsent, Object defaultValue) {
        Object objectValue = theSubCalendar.opt(property);
        if (objectValue == null) {
            if (!optional) {
                throw new IllegalArgumentException("Error migrating calendar: " + theSubCalendar.opt("name") + " with id " + theSubCalendar.optString("id") + " (StoreKey " + this.getProviderKey() + "). Property: " + property + " cannot be null.");
            }
            if (warnIfAbsent) {
                LOG.warn("Expected property " + property + " not found when migrating calendar " + theSubCalendar.opt("name") + " with id " + theSubCalendar.optString("id") + " (StoreKey " + this.getProviderKey() + "). Setting to default value of: " + defaultValue);
            }
            return defaultValue;
        }
        return objectValue;
    }

    protected Object getParentId(JSONObject theSubCalendar) {
        return this.getProperty(theSubCalendar, "parentId", false, false, null);
    }

    protected class BandanaProviderJsonPropertyGetter
    implements JsonPropertyGetter<Object> {
        protected BandanaProviderJsonPropertyGetter() {
        }

        @Override
        public Object getProperty(JSONObject theSubCalendar, String property, boolean optional, boolean warnIfAbsent, Object defaultValue) {
            return AbstractBandanaSubCalendarProvider.this.getProperty(theSubCalendar, property, optional, warnIfAbsent, defaultValue);
        }
    }
}

