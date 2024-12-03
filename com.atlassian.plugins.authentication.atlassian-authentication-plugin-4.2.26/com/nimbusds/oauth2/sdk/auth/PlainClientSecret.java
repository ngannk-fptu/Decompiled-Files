/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;

public abstract class PlainClientSecret
extends ClientAuthentication {
    private final Secret secret;

    protected PlainClientSecret(ClientAuthenticationMethod method, ClientID clientID, Secret secret) {
        super(method, clientID);
        if (secret == null) {
            throw new IllegalArgumentException("The client secret must not be null");
        }
        this.secret = secret;
    }

    public Secret getClientSecret() {
        return this.secret;
    }
}

