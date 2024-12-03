/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.Request;
import java.net.URI;

public abstract class AbstractRequest
implements Request {
    private final URI uri;

    public AbstractRequest(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getEndpointURI() {
        return this.uri;
    }
}

