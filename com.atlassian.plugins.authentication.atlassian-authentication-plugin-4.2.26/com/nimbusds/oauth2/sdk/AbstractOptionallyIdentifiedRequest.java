/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AbstractOptionallyAuthenticatedRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.net.URI;

public abstract class AbstractOptionallyIdentifiedRequest
extends AbstractOptionallyAuthenticatedRequest {
    private final ClientID clientID;

    public AbstractOptionallyIdentifiedRequest(URI uri, ClientAuthentication clientAuth) {
        super(uri, clientAuth);
        this.clientID = null;
    }

    public AbstractOptionallyIdentifiedRequest(URI uri, ClientID clientID) {
        super(uri, null);
        this.clientID = clientID;
    }

    public ClientID getClientID() {
        return this.clientID;
    }
}

