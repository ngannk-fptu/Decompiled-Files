/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.rest.model.ErrorDescription;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class ResponseErrorJson {
    private final int status;
    private final String message;
    private final List<ErrorDescription> errors;
    private final String timestamp;

    public ResponseErrorJson(@JsonProperty(value="status") int status, @JsonProperty(value="message") String message, @JsonProperty(value="errors") List<ErrorDescription> errors, @JsonProperty(value="timestamp") String timestamp) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    @JsonProperty(value="status")
    public Integer getStatus() {
        return this.status;
    }

    @JsonProperty(value="message")
    public String getMessage() {
        return this.message;
    }

    @JsonProperty(value="errors")
    public List<ErrorDescription> getErrors() {
        return this.errors;
    }

    @JsonProperty(value="timestamp")
    public String getTimestamp() {
        return this.timestamp;
    }
}

