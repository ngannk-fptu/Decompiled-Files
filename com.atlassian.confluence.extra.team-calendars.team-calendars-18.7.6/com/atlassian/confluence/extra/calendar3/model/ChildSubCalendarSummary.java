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
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import javax.xml.bind.annotation.XmlElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildSubCalendarSummary
extends SubCalendarSummary {
    private static final Logger LOG = LoggerFactory.getLogger(ChildSubCalendarSummary.class);
    private final String parentId;

    public ChildSubCalendarSummary(String parentId, String id, String type, String name, String description, String color, String creator) {
        super(id, type, name, description, color, creator);
        this.parentId = parentId;
    }

    public ChildSubCalendarSummary() {
        this(null, null, null, null, null, null, null);
    }

    @XmlElement
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            thisObject.put("parentId", (Object)this.getParentId());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }
}

