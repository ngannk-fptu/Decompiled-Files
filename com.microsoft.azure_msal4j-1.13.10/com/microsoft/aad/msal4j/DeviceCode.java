/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class DeviceCode {
    @JsonProperty(value="user_code")
    private String userCode;
    @JsonProperty(value="device_code")
    private String deviceCode;
    @JsonProperty(value="verification_uri")
    private String verificationUri;
    @JsonProperty(value="expires_in")
    private long expiresIn;
    @JsonProperty(value="interval")
    private long interval;
    @JsonProperty(value="message")
    private String message;
    private transient String correlationId = null;
    private transient String clientId = null;
    private transient String scopes = null;

    public String userCode() {
        return this.userCode;
    }

    public String deviceCode() {
        return this.deviceCode;
    }

    public String verificationUri() {
        return this.verificationUri;
    }

    public long expiresIn() {
        return this.expiresIn;
    }

    public long interval() {
        return this.interval;
    }

    public String message() {
        return this.message;
    }

    protected String correlationId() {
        return this.correlationId;
    }

    protected DeviceCode correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    protected String clientId() {
        return this.clientId;
    }

    protected DeviceCode clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    protected String scopes() {
        return this.scopes;
    }

    protected DeviceCode scopes(String scopes) {
        this.scopes = scopes;
        return this;
    }
}

