/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;

class ErrorResponse {
    private Integer statusCode;
    private String statusMessage;
    @JsonProperty(value="error")
    protected String error;
    @JsonProperty(value="error_description")
    protected String errorDescription;
    @JsonProperty(value="error_codes")
    protected long[] errorCodes;
    @JsonProperty(value="suberror")
    protected String subError;
    @JsonProperty(value="trace_id")
    protected String traceId;
    @JsonProperty(value="timestamp")
    protected String timestamp;
    @JsonProperty(value="correlation_id")
    protected String correlation_id;
    @JsonProperty(value="claims")
    private String claims;

    ErrorResponse() {
    }

    public Integer statusCode() {
        return this.statusCode;
    }

    public String statusMessage() {
        return this.statusMessage;
    }

    public String error() {
        return this.error;
    }

    public String errorDescription() {
        return this.errorDescription;
    }

    public long[] errorCodes() {
        return this.errorCodes;
    }

    public String subError() {
        return this.subError;
    }

    public String traceId() {
        return this.traceId;
    }

    public String timestamp() {
        return this.timestamp;
    }

    public String correlation_id() {
        return this.correlation_id;
    }

    public String claims() {
        return this.claims;
    }

    public ErrorResponse statusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ErrorResponse statusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public ErrorResponse error(String error) {
        this.error = error;
        return this;
    }

    public ErrorResponse errorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }

    public ErrorResponse errorCodes(long[] errorCodes) {
        this.errorCodes = errorCodes;
        return this;
    }

    public ErrorResponse subError(String subError) {
        this.subError = subError;
        return this;
    }

    public ErrorResponse traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public ErrorResponse timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ErrorResponse correlation_id(String correlation_id) {
        this.correlation_id = correlation_id;
        return this;
    }

    public ErrorResponse claims(String claims) {
        this.claims = claims;
        return this;
    }
}

