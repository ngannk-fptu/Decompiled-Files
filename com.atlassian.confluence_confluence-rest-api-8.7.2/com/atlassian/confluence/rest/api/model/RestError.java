/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  javax.ws.rs.core.Response$StatusType
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.rest.api.model.validation.RestValidationResult;
import javax.ws.rs.core.Response;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@ExperimentalApi
public class RestError {
    @JsonProperty
    private final int statusCode;
    @JsonProperty
    private final RestValidationResult data;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String reason;

    @JsonCreator
    private RestError() {
        this.statusCode = 0;
        this.data = null;
        this.message = null;
        this.reason = null;
    }

    RestError(Response.StatusType statusType, String message, @Nullable RestValidationResult data) {
        this(statusType.getStatusCode(), statusType.getReasonPhrase(), message, data);
    }

    RestError(int status, String message, @Nullable RestValidationResult data) {
        this(status, null, message, data);
    }

    RestError(int status, String reason, String message, @Nullable RestValidationResult data) {
        this.statusCode = status;
        this.message = message;
        this.data = data;
        this.reason = reason;
    }

    public @Nullable ValidationResult getData() {
        return this.data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getReason() {
        return this.reason;
    }
}

