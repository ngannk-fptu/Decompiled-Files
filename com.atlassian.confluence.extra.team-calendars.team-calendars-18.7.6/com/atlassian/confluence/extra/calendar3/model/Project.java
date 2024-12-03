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
public class Project
implements JsonSerializable,
Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(Project.class);
    @XmlElement
    public final String name;
    @XmlElement
    public final String key;

    public Project(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Project() {
        this(null, null);
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project)o;
        if (this.key != null ? !this.key.equals(project.key) : project.key != null) {
            return false;
        }
        return this.name != null ? this.name.equals(project.name) : project.name == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            thisObj.put("key", (Object)this.getKey());
            thisObj.put("name", (Object)this.getName());
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObj;
    }
}

