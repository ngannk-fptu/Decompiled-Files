/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.whisper.plugin.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
class PropertyRest {
    @JsonProperty
    String key;
    @JsonProperty
    String value;

    public PropertyRest() {
    }

    PropertyRest(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

