/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class Duration
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(Duration.class);
    @XmlElement
    private String startDateFieldName;
    @XmlElement
    private String endDateFieldName;

    public Duration() {
        this(null, null);
    }

    public Duration(String startDateFieldName, String endDateFieldName) {
        this.setStartDateFieldName(startDateFieldName);
        this.setEndDateFieldName(endDateFieldName);
    }

    public String getStartDateFieldName() {
        return this.startDateFieldName;
    }

    public void setStartDateFieldName(String startDateFieldName) {
        this.startDateFieldName = startDateFieldName;
    }

    public String getEndDateFieldName() {
        return this.endDateFieldName;
    }

    public void setEndDateFieldName(String endDateFieldName) {
        this.endDateFieldName = endDateFieldName;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            if (StringUtils.isNotBlank((CharSequence)this.getStartDateFieldName())) {
                thisObj.put("startDateFieldName", (Object)this.getStartDateFieldName());
            }
            if (StringUtils.isNotBlank((CharSequence)this.getEndDateFieldName())) {
                thisObj.put("endDateFieldName", (Object)this.getEndDateFieldName());
            }
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObj;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Duration duration = (Duration)o;
        if (this.endDateFieldName != null ? !this.endDateFieldName.equals(duration.endDateFieldName) : duration.endDateFieldName != null) {
            return false;
        }
        return this.startDateFieldName != null ? this.startDateFieldName.equals(duration.startDateFieldName) : duration.startDateFieldName == null;
    }

    public int hashCode() {
        int result = this.startDateFieldName != null ? this.startDateFieldName.hashCode() : 0;
        result = 31 * result + (this.endDateFieldName != null ? this.endDateFieldName.hashCode() : 0);
        return result;
    }
}

