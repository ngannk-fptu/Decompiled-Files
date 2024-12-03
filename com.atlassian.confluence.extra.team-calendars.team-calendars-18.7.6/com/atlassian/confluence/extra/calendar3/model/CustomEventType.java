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
public class CustomEventType
implements Serializable,
JsonSerializable,
Comparable {
    private static final Logger LOG = LoggerFactory.getLogger(CustomEventType.class);
    private String customEventTypeId;
    private String title;
    private String icon;
    private String parentSubCalendarId;
    private String created;
    private int periodInMins;

    public CustomEventType(String customEventTypeId, String title, String icon, String parentSubCalendarId, String created, int periodInMins) {
        this.customEventTypeId = customEventTypeId;
        this.title = title;
        this.icon = icon;
        this.parentSubCalendarId = parentSubCalendarId;
        this.created = created;
        this.periodInMins = periodInMins;
    }

    public CustomEventType() {
        this(null, null, null, null, null, 0);
    }

    @XmlElement
    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    @XmlElement
    public String getTitle() {
        return this.title;
    }

    @XmlElement
    public String getIcon() {
        return this.icon;
    }

    @XmlElement
    public String getParentSubCalendarId() {
        return this.parentSubCalendarId;
    }

    @XmlElement
    public String getCreated() {
        return this.created;
    }

    @XmlElement
    public int getPeriodInMins() {
        return this.periodInMins;
    }

    public int compareTo(Object obj) {
        CustomEventType that = (CustomEventType)obj;
        if (that == null) {
            return -1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(this.getTitle(), that.getTitle());
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("customEventTypeId", (Object)this.getCustomEventTypeId());
            thisObject.put("title", (Object)this.getTitle());
            thisObject.put("icon", (Object)this.getIcon());
            thisObject.put("parentSubCalendarId", (Object)this.getParentSubCalendarId());
            thisObject.put("created", (Object)this.getCreated());
            thisObject.put("periodInMins", this.getPeriodInMins());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

