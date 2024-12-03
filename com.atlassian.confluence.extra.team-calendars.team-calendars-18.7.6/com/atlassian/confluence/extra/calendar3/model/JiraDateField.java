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
public class JiraDateField
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(JiraDateField.class);
    private static final String CUSTOMFIELD_KEY_PREFIX = "customfield_";
    @XmlElement
    private String key;
    @XmlElement
    private String name;
    @XmlElement
    private boolean isSearchable;

    public JiraDateField() {
        this(null, null, false);
    }

    @Deprecated
    public JiraDateField(String key, String name) {
        this(key, name, true);
    }

    public JiraDateField(String key, String name, boolean isSearchable) {
        this.key = key;
        this.name = name;
        this.isSearchable = isSearchable;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSearchable() {
        return this.isSearchable;
    }

    @XmlElement
    public boolean isCustomField() {
        return StringUtils.startsWith((CharSequence)this.getKey(), (CharSequence)CUSTOMFIELD_KEY_PREFIX);
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            thisObject.put("key", (Object)this.getKey());
            thisObject.put("name", (Object)this.getName());
            thisObject.put("customfield", this.isCustomField());
            thisObject.put("isSearchable", this.isSearchable());
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObject;
    }
}

