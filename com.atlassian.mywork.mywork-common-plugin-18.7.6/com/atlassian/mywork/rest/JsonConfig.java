/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.rest;

import com.atlassian.mywork.model.Registration;
import java.util.Locale;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonConfig {
    @JsonProperty
    public String application;
    @JsonProperty
    public String appId;
    @JsonProperty
    private String url;
    @JsonProperty
    public Map<String, String> i18n;
    @JsonProperty
    public JsonNode actions;
    @JsonProperty
    public Map<String, String> properties;

    private JsonConfig() {
    }

    public JsonConfig(Registration registration, Locale locale) {
        this.application = registration.getApplication();
        this.appId = registration.getAppId();
        this.url = registration.getDisplayURL();
        this.i18n = registration.getValues(locale);
        this.actions = registration.getActions();
        this.properties = registration.getProperties();
    }

    public String getUrl() {
        return this.url;
    }
}

