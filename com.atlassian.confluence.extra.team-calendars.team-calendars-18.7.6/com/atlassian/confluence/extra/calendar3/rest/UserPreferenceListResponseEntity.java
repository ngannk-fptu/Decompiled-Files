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
public class UserPreferenceListResponseEntity
extends GeneralResponseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(UserPreferenceListResponseEntity.class);
    @XmlElement
    private List<String> payload;
    @XmlElement
    private Long start;
    @XmlElement
    private Long limit;
    @XmlElement
    private Integer count;

    public UserPreferenceListResponseEntity() {
        this.setSuccess(true);
    }

    public UserPreferenceListResponseEntity(List<String> payload, long start, long limit) {
        this();
        this.setPayload(payload);
        this.setCount(payload.size());
        this.setLimit(limit);
        this.setStart(start);
    }

    public void setPayload(List<String> payload) {
        this.payload = payload;
    }

    public List<String> getPayload() {
        return this.payload;
    }

    public Long getStart() {
        return this.start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getLimit() {
        return this.limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

