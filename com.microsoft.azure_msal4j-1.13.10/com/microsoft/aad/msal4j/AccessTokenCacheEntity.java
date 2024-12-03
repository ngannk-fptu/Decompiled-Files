/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.Credential;
import com.microsoft.aad.msal4j.StringHelper;
import java.util.ArrayList;

class AccessTokenCacheEntity
extends Credential {
    @JsonProperty(value="credential_type")
    private String credentialType;
    @JsonProperty(value="realm")
    protected String realm;
    @JsonProperty(value="target")
    private String target;
    @JsonProperty(value="cached_at")
    private String cachedAt;
    @JsonProperty(value="expires_on")
    private String expiresOn;
    @JsonProperty(value="extended_expires_on")
    private String extExpiresOn;
    @JsonProperty(value="refresh_on")
    private String refreshOn;

    AccessTokenCacheEntity() {
    }

    String getKey() {
        ArrayList<String> keyParts = new ArrayList<String>();
        keyParts.add(StringHelper.isBlank(this.homeAccountId) ? "" : this.homeAccountId);
        keyParts.add(this.environment);
        keyParts.add(this.credentialType);
        keyParts.add(this.clientId);
        keyParts.add(this.realm);
        keyParts.add(this.target);
        return String.join((CharSequence)"-", keyParts).toLowerCase();
    }

    public String credentialType() {
        return this.credentialType;
    }

    public String realm() {
        return this.realm;
    }

    public String target() {
        return this.target;
    }

    public String cachedAt() {
        return this.cachedAt;
    }

    public String expiresOn() {
        return this.expiresOn;
    }

    public String extExpiresOn() {
        return this.extExpiresOn;
    }

    public String refreshOn() {
        return this.refreshOn;
    }

    public AccessTokenCacheEntity credentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public AccessTokenCacheEntity realm(String realm) {
        this.realm = realm;
        return this;
    }

    public AccessTokenCacheEntity target(String target) {
        this.target = target;
        return this;
    }

    public AccessTokenCacheEntity cachedAt(String cachedAt) {
        this.cachedAt = cachedAt;
        return this;
    }

    public AccessTokenCacheEntity expiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
        return this;
    }

    public AccessTokenCacheEntity extExpiresOn(String extExpiresOn) {
        this.extExpiresOn = extExpiresOn;
        return this;
    }

    public AccessTokenCacheEntity refreshOn(String refreshOn) {
        this.refreshOn = refreshOn;
        return this;
    }
}

