/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SerializationUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.config;

import java.util.HashMap;
import java.util.Map;
import jdk.jfr.Configuration;
import org.apache.commons.lang3.SerializationUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class JfcTemplateDetails {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String label;
    @JsonProperty
    private String provider;
    @JsonProperty
    private Map<String, String> settings;

    public JfcTemplateDetails(Configuration configuration) {
        this.name = configuration.getName();
        this.description = configuration.getDescription();
        this.label = configuration.getLabel();
        this.provider = configuration.getProvider();
        this.settings = (Map)((Object)SerializationUtils.clone(new HashMap<String, String>(configuration.getSettings())));
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLabel() {
        return this.label;
    }

    public String getProvider() {
        return this.provider;
    }

    public Map<String, String> getSettings() {
        return this.settings;
    }
}

