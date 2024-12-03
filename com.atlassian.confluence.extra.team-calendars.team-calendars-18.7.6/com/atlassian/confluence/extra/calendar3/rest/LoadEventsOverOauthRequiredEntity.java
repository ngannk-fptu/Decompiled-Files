/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.rest.OAuthRequiredEntity;
import javax.xml.bind.annotation.XmlElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadEventsOverOauthRequiredEntity
extends OAuthRequiredEntity {
    private static final Logger LOG = LoggerFactory.getLogger(LoadEventsOverOauthRequiredEntity.class);
    @XmlElement
    private String subCalendarId;
    @XmlElement
    private String subCalendarName;

    public LoadEventsOverOauthRequiredEntity(String oAuthUrl, String subCalendarId, String subCalendarName) {
        super(oAuthUrl);
        this.setSubCalendarId(subCalendarId);
        this.setSubCalendarName(subCalendarName);
    }

    public LoadEventsOverOauthRequiredEntity() {
        this(null, null, null);
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getSubCalendarName() {
        return this.subCalendarName;
    }

    public void setSubCalendarName(String subCalendarName) {
        this.subCalendarName = subCalendarName;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            thisObject.put("subCalendarId", (Object)this.getSubCalendarId());
            thisObject.put("subCalendarName", (Object)this.getSubCalendarName());
        }
        catch (JSONException jsonException) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonException);
        }
        return thisObject;
    }
}

