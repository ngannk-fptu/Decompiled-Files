/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class JiraLink
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(JiraLink.class);
    @XmlElement
    private final String id;
    @XmlElement
    private final String name;

    public JiraLink(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public JiraLink(ApplicationLink jiraLink) {
        this(jiraLink.getId().get(), jiraLink.getName());
    }

    public JiraLink() {
        this(null);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            thisObj.put("id", (Object)this.getId());
            thisObj.put("name", (Object)this.getName());
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObj;
    }
}

