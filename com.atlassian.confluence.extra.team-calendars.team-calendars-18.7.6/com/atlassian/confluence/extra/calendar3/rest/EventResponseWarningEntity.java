/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.model.LocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class EventResponseWarningEntity
extends GeneralResponseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(EventResponseWarningEntity.class);
    @XmlElement
    Collection<LocalizedSubCalendarEvent> subCalendarEvents;
    @XmlElement
    private String warning;

    public EventResponseWarningEntity(String warning, Collection<LocalizedSubCalendarEvent> subCalendarEvents) {
        this.warning = warning;
        this.subCalendarEvents = subCalendarEvents;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            if (StringUtils.isNotEmpty((CharSequence)this.warning)) {
                JSONArray array = new JSONArray();
                array.put((Object)"warning-type");
                array.put((Object)this.warning);
                thisObject.put("warning", (Object)array);
            }
            if (null != this.subCalendarEvents && !this.subCalendarEvents.isEmpty()) {
                JSONArray subCalendarEventArray = new JSONArray();
                for (LocalizedSubCalendarEvent subCalendar : this.subCalendarEvents) {
                    subCalendarEventArray.put((Object)subCalendar.toJson());
                }
                thisObject.put("events", (Object)subCalendarEventArray);
            }
        }
        catch (JSONException jsonException) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonException);
        }
        return thisObject;
    }
}

