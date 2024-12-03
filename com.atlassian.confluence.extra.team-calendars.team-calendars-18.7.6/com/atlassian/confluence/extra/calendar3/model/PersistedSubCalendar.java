/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public abstract class PersistedSubCalendar
extends SubCalendar
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(PersistedSubCalendar.class);

    public PersistedSubCalendar getEffectiveParent() {
        PersistedSubCalendar parent = this.getParent();
        return parent == null ? this : parent;
    }

    @XmlElement
    public abstract String getId();

    @XmlElement
    public String getParentId() {
        return this.getParent() == null ? null : this.getParent().getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    @XmlElement
    public String getColor() {
        return super.getColor();
    }

    @Override
    @XmlElement
    public String getTimeZoneId() {
        return super.getTimeZoneId();
    }

    @Override
    @XmlElement
    public String getSpaceKey() {
        return super.getSpaceKey();
    }

    @Override
    @XmlElement
    public Set<String> getDisableEventTypes() {
        return super.getDisableEventTypes();
    }

    @Override
    @XmlElement
    public Set<CustomEventType> getCustomEventTypes() {
        return super.getCustomEventTypes();
    }

    @Override
    @XmlElement
    public Set<EventTypeReminder> getEventTypeReminders() {
        return super.getEventTypeReminders();
    }

    @Override
    @XmlElement
    public String getType() {
        return super.getType();
    }

    @XmlElement
    public String getTypeKey() {
        return "calendar3.subcalendar.type." + this.getType();
    }

    @XmlElement
    public abstract String getCreator();

    @XmlElement
    public abstract String getSpaceName();

    @XmlElement
    public abstract boolean isWatchable();

    @XmlElement
    public abstract boolean isRestrictable();

    @XmlElement
    public abstract boolean isEventInviteesSupported();

    public abstract Object clone();

    public Set<String> getChildSubCalendarIds() {
        return Collections.emptySet();
    }

    public int getReminderPeriodFor(SubCalendarEvent event) {
        PersistedSubCalendar childSubCalendar = event.getSubCalendar();
        PersistedSubCalendar parentSubCalendar = childSubCalendar.getParent();
        if (parentSubCalendar == null || !this.getId().equals(parentSubCalendar.getId())) {
            return -1;
        }
        if (StringUtils.isEmpty((CharSequence)event.getCustomEventTypeId())) {
            for (EventTypeReminder eventTypeReminder : this.getEventTypeReminders()) {
                if (!eventTypeReminder.getEventTypeId().equals(event.getEventType())) continue;
                return eventTypeReminder.getPeriodInMins();
            }
        }
        for (CustomEventType customEventType : this.getCustomEventTypes()) {
            if (!customEventType.getCustomEventTypeId().equals(event.getCustomEventTypeId())) continue;
            return customEventType.getPeriodInMins();
        }
        return -1;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            if (this.getParent() != null) {
                thisObject.put("parentId", (Object)this.getParentId());
            }
            thisObject.put("id", (Object)this.getId());
            thisObject.put("type", (Object)this.getType());
            thisObject.put("typeKey", (Object)this.getTypeKey());
            thisObject.put("name", (Object)this.getName());
            thisObject.put("description", (Object)this.getDescription());
            thisObject.put("color", (Object)this.getColor());
            thisObject.put("creator", (Object)this.getCreator());
            String spaceKey = this.getSpaceKey();
            if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
                thisObject.put("spaceKey", (Object)this.getSpaceKey());
                thisObject.put("spaceName", (Object)this.getSpaceName());
            }
            thisObject.put("timeZoneId", (Object)this.getTimeZoneId());
            thisObject.put("watchable", this.isWatchable());
            thisObject.put("restrictable", this.isRestrictable());
            thisObject.put("eventInviteesSupported", this.isEventInviteesSupported());
            if (this.getDisableEventTypes() != null) {
                JSONArray disableEventTypeArray = new JSONArray();
                for (String disableEventType : this.getDisableEventTypes()) {
                    disableEventTypeArray.put((Object)disableEventType);
                }
                thisObject.put("disableEventTypes", (Object)disableEventTypeArray);
            }
            if (this.getCustomEventTypes() != null) {
                JSONArray customEventTypeArray = new JSONArray();
                for (CustomEventType customEventType : this.getCustomEventTypes()) {
                    customEventTypeArray.put((Object)customEventType.toJson());
                }
                thisObject.put("customEventTypes", (Object)customEventTypeArray);
            }
            if (this.getEventTypeReminders() != null) {
                JSONArray eventReminderArrays = new JSONArray();
                for (EventTypeReminder eventTypeReminder : this.getEventTypeReminders()) {
                    eventReminderArrays.put((Object)eventTypeReminder.toJson());
                }
                thisObject.put("sanboxEventTypeReminders", (Object)eventReminderArrays);
            }
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

