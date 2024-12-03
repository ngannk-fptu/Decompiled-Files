/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SubscribingSubCalendar
extends PersistedSubCalendar {
    private static final Logger LOG = LoggerFactory.getLogger(SubscribingSubCalendar.class);

    @XmlElement
    public abstract String getSubscriptionType();

    @XmlElement
    public abstract String getSubscriptionId();

    @Override
    @XmlElement
    public abstract Set<CustomEventType> getCustomEventTypes();

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            thisObject.put("subscriptionType", (Object)this.getSubscriptionType());
            thisObject.put("subscriptionId", (Object)this.getSubscriptionId());
            if (this.getCustomEventTypes() != null) {
                JSONArray customEventTypeArray = new JSONArray();
                for (CustomEventType customEventType : this.getCustomEventTypes()) {
                    customEventTypeArray.put((Object)customEventType.toJson());
                }
                thisObject.put("customEventTypes", (Object)customEventTypeArray);
            }
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

