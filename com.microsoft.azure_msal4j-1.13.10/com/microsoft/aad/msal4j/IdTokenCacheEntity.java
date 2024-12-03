/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.Credential;
import java.util.ArrayList;

class IdTokenCacheEntity
extends Credential {
    @JsonProperty(value="credential_type")
    private String credentialType;
    @JsonProperty(value="realm")
    protected String realm;

    IdTokenCacheEntity() {
    }

    String getKey() {
        ArrayList<String> keyParts = new ArrayList<String>();
        keyParts.add(this.homeAccountId);
        keyParts.add(this.environment);
        keyParts.add(this.credentialType);
        keyParts.add(this.clientId);
        keyParts.add(this.realm);
        keyParts.add("");
        return String.join((CharSequence)"-", keyParts).toLowerCase();
    }

    public String credentialType() {
        return this.credentialType;
    }

    public String realm() {
        return this.realm;
    }

    public IdTokenCacheEntity credentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public IdTokenCacheEntity realm(String realm) {
        this.realm = realm;
        return this;
    }
}

