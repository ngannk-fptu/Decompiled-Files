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
public class OAuthRequiredEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthRequiredEntity.class);
    @XmlElement
    private String oAuthUrl;

    public OAuthRequiredEntity(String oAuthUrl) {
        this.setoAuthUrl(oAuthUrl);
    }

    public OAuthRequiredEntity() {
        this(null);
    }

    public String getoAuthUrl() {
        return this.oAuthUrl;
    }

    public void setoAuthUrl(String oAuthUrl) {
        this.oAuthUrl = oAuthUrl;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("oAuthUrl", (Object)this.getoAuthUrl());
        }
        catch (JSONException jsonException) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonException);
        }
        return thisObject;
    }
}

