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
public class SubCalendarSummary
implements Serializable,
JsonSerializable,
Comparable {
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarSummary.class);
    private final String id;
    private final String type;
    private final String name;
    private final String description;
    private final String color;
    private final String creator;

    public SubCalendarSummary(String id, String type, String name, String description, String color, String creator) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.color = color;
        this.creator = creator;
    }

    public SubCalendarSummary() {
        this(null, null, null, null, null, null);
    }

    @XmlElement
    public String getId() {
        return this.id;
    }

    @XmlElement
    public String getType() {
        return this.type;
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    @XmlElement
    public String getDescription() {
        return this.description;
    }

    @XmlElement
    public String getColor() {
        return this.color;
    }

    @XmlElement
    public String getCreator() {
        return this.creator;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("id", (Object)this.getId());
            thisObject.put("type", (Object)this.getType());
            thisObject.put("name", (Object)this.getName());
            thisObject.put("description", (Object)this.getDescription());
            thisObject.put("color", (Object)this.getColor());
            thisObject.put("creator", (Object)this.getCreator());
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }

    public int compareTo(Object o) {
        SubCalendarSummary that = (SubCalendarSummary)o;
        if (that == null) {
            return -1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), that.getName());
    }
}

