/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import java.net.URI;

public abstract class ProtectedResourceRequest
extends AbstractRequest {
    private final AccessToken accessToken;

    protected ProtectedResourceRequest(URI uri, AccessToken accessToken) {
        super(uri);
        this.accessToken = accessToken;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }
}

