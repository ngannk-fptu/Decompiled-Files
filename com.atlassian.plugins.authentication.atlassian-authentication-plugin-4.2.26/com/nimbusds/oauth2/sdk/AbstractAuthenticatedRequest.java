/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import java.net.URI;

public abstract class AbstractAuthenticatedRequest
extends AbstractRequest {
    private final ClientAuthentication clientAuth;

    public AbstractAuthenticatedRequest(URI uri, ClientAuthentication clientAuth) {
        super(uri);
        if (clientAuth == null) {
            throw new IllegalArgumentException("The client authentication must not be null");
        }
        this.clientAuth = clientAuth;
    }

    public ClientAuthentication getClientAuthentication() {
        return this.clientAuth;
    }
}

