/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;

class Credential {
    @JsonProperty(value="home_account_id")
    protected String homeAccountId;
    @JsonProperty(value="environment")
    protected String environment;
    @JsonProperty(value="client_id")
    protected String clientId;
    @JsonProperty(value="secret")
    protected String secret;
    @JsonProperty(value="user_assertion_hash")
    protected String userAssertionHash;

    Credential() {
    }

    public String homeAccountId() {
        return this.homeAccountId;
    }

    public String environment() {
        return this.environment;
    }

    public String clientId() {
        return this.clientId;
    }

    public String secret() {
        return this.secret;
    }

    public String userAssertionHash() {
        return this.userAssertionHash;
    }

    public Credential homeAccountId(String homeAccountId) {
        this.homeAccountId = homeAccountId;
        return this;
    }

    public Credential environment(String environment) {
        this.environment = environment;
        return this;
    }

    public Credential clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public Credential secret(String secret) {
        this.secret = secret;
        return this;
    }

    public Credential userAssertionHash(String userAssertionHash) {
        this.userAssertionHash = userAssertionHash;
        return this;
    }
}

