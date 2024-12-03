/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Macro {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private int schemaVersion;
    @XmlAttribute
    private String body;
    @XmlAttribute
    private String defaultParameterValue;
    @XmlAttribute
    private Map<String, String> params = new HashMap<String, String>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String bodyMarkup) {
        this.body = bodyMarkup;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getDefaultParameterValue() {
        return this.defaultParameterValue;
    }

    public void setDefaultParameterValue(String defaultParameterValue) {
        this.defaultParameterValue = defaultParameterValue;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}

