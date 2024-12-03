/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class EventResponseEntity
extends GeneralResponseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(GeneralResponseEntity.class);
    @XmlElement
    private SubCalendarEvent event;
    @XmlElement
    private SubCalendarsResponseEntity.ExtendedSubCalendar subCalendar;

    public EventResponseEntity(SubCalendarEvent subCalendarEvent, SubCalendarsResponseEntity.ExtendedSubCalendar subCalendar) {
        this.event = subCalendarEvent;
        this.subCalendar = subCalendar;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            if (null != this.event) {
                thisObject.put("event", (Object)this.event.toJson());
            }
            if (null != this.subCalendar) {
                thisObject.put("subCalendar", (Object)this.subCalendar.toJson());
            }
        }
        catch (JSONException jsonException) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonException);
        }
        return thisObject;
    }
}

