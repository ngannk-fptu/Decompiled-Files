/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class SubCalendarListResponseEntity
extends GeneralResponseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarListResponseEntity.class);
    @XmlElement
    private List<String> payload;

    public SubCalendarListResponseEntity() {
        this.setSuccess(true);
    }

    public SubCalendarListResponseEntity(List<String> payload) {
        this();
        this.setPayload(payload);
    }

    public void setPayload(List<String> payload) {
        this.payload = payload;
    }

    public List<String> getPayload() {
        return this.payload;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = super.toJson();
        if (null != this.getPayload() && !this.getPayload().isEmpty()) {
            JSONArray subCalendarArray = new JSONArray();
            for (String subCalendar : this.getPayload()) {
                subCalendarArray.put((Object)subCalendar);
            }
            try {
                thisObj.put("payload", (Object)subCalendarArray);
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
        }
        return thisObj;
    }
}

