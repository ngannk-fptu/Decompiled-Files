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

class RefreshTokenCacheEntity
extends Credential {
    @JsonProperty(value="credential_type")
    private String credentialType;
    @JsonProperty(value="family_id")
    private String family_id;

    RefreshTokenCacheEntity() {
    }

    boolean isFamilyRT() {
        return !StringHelper.isBlank(this.family_id);
    }

    String getKey() {
        ArrayList<String> keyParts = new ArrayList<String>();
        keyParts.add(this.homeAccountId);
        keyParts.add(this.environment);
        keyParts.add(this.credentialType);
        if (this.isFamilyRT()) {
            keyParts.add(this.family_id);
        } else {
            keyParts.add(this.clientId);
        }
        keyParts.add("");
        keyParts.add("");
        return String.join((CharSequence)"-", keyParts).toLowerCase();
    }

    public String credentialType() {
        return this.credentialType;
    }

    public String family_id() {
        return this.family_id;
    }

    public RefreshTokenCacheEntity credentialType(String credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public RefreshTokenCacheEntity family_id(String family_id) {
        this.family_id = family_id;
        return this;
    }
}

