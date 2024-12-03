/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorDescription {
    private final String key;
    private final String message;

    @JsonCreator
    public ErrorDescription(@JsonProperty(value="key") String key, @JsonProperty(value="message") String message) {
        this.key = key;
        this.message = message;
    }

    @JsonProperty(value="key")
    public String getKey() {
        return this.key;
    }

    @JsonProperty(value="message")
    public String getMessage() {
        return this.message;
    }
}

