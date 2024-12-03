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

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class SubscriptionCountResponseEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCountResponseEntity.class);
    @XmlElement
    private int subscriptionCount;

    public SubscriptionCountResponseEntity(int subscriptionCount) {
        this.setSubscriptionCount(subscriptionCount);
    }

    public int getSubscriptionCount() {
        return this.subscriptionCount;
    }

    public void setSubscriptionCount(int subscriptionCount) {
        this.subscriptionCount = subscriptionCount;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("subscriptionCount", this.getSubscriptionCount());
        }
        catch (JSONException jsone) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsone);
        }
        return thisObject;
    }
}

