/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.retry.internal;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.auth.Signer;
import java.net.URI;

@Immutable
public class AuthRetryParameters {
    private final Signer signerForRetry;
    private final URI endpointForRetry;

    public AuthRetryParameters(Signer signer, URI endpoint) {
        if (signer == null) {
            throw new NullPointerException("signer");
        }
        if (endpoint == null) {
            throw new NullPointerException("endpoint");
        }
        this.signerForRetry = signer;
        this.endpointForRetry = endpoint;
    }

    public Signer getSignerForRetry() {
        return this.signerForRetry;
    }

    public URI getEndpointForRetry() {
        return this.endpointForRetry;
    }
}

