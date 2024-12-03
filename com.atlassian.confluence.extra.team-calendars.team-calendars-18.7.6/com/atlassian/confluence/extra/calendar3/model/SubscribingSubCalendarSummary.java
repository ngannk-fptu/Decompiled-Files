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

import com.atlassian.confluence.extra.calendar3.model.ChildSubCalendarSummary;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class SubscribingSubCalendarSummary
extends ChildSubCalendarSummary {
    private static final Logger LOG = LoggerFactory.getLogger(SubscribingSubCalendarSummary.class);
    private final String subscriptionId;

    public SubscribingSubCalendarSummary(String parentId, String id, String type, String name, String description, String color, String creator, String subscriptionId) {
        super(parentId, id, type, name, description, color, creator);
        this.subscriptionId = subscriptionId;
    }

    public SubscribingSubCalendarSummary() {
        this(null, null, null, null, null, null, null, null);
    }

    @XmlElement
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            thisObject.put("subscriptionId", (Object)this.getSubscriptionId());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

