/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import java.net.URI;

public abstract class AbstractOptionallyAuthenticatedRequest
extends AbstractRequest {
    private final ClientAuthentication clientAuth;

    public AbstractOptionallyAuthenticatedRequest(URI uri, ClientAuthentication clientAuth) {
        super(uri);
        this.clientAuth = clientAuth;
    }

    public ClientAuthentication getClientAuthentication() {
        return this.clientAuth;
    }
}

