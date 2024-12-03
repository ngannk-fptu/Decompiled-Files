/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

public class InvalidDPoPProofException
extends Exception {
    private static final long serialVersionUID = -379875576526526089L;

    public InvalidDPoPProofException(String message) {
        super(message);
    }

    public InvalidDPoPProofException(String message, Throwable cause) {
        super(message, cause);
    }
}

