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
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class EventTypeReminder
implements Serializable,
JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(EventTypeReminder.class);
    private String eventTypeId;
    private int periodInMins;
    private boolean isCustomEventType;

    public EventTypeReminder(String eventTypeId, int periodInMins, boolean isCustomEventType) {
        this.eventTypeId = eventTypeId;
        this.periodInMins = periodInMins;
        this.isCustomEventType = isCustomEventType;
    }

    @XmlElement
    public int getPeriodInMins() {
        return this.periodInMins;
    }

    public void setPeriodInMins(int periodInMins) {
        this.periodInMins = periodInMins;
    }

    @XmlElement
    public String getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @XmlElement
    public boolean isCustomEventType() {
        return this.isCustomEventType;
    }

    public void setCustomEventType(boolean isCustomEventType) {
        this.isCustomEventType = isCustomEventType;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("isCustomEventType", this.isCustomEventType());
            thisObject.put("periodInMins", this.getPeriodInMins());
            thisObject.put("eventTypeId", (Object)this.getEventTypeId());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

