/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorStatusResponse {
    private int statusCode;
    private String message;
    private List<DiagnosticDescription> errors;
    private String timestamp;

    public ErrorStatusResponse() {
    }

    @JsonCreator
    public ErrorStatusResponse(@JsonProperty(value="statusCode") int statusCode, @JsonProperty(value="message") String message, @JsonProperty(value="errors") List<DiagnosticDescription> errors, @JsonProperty(value="timestamp") String timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    @JsonProperty(value="statusCode")
    public Integer getStatusCode() {
        return this.statusCode;
    }

    @JsonProperty(value="message")
    public String getMessage() {
        return this.message;
    }

    @JsonProperty(value="errors")
    public List<DiagnosticDescription> getErrors() {
        return this.errors;
    }

    @JsonProperty(value="timestamp")
    public String getTimestamp() {
        return this.timestamp;
    }
}

