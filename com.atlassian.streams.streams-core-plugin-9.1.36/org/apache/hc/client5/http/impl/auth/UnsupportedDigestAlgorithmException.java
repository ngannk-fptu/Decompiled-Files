/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.auth;

public class UnsupportedDigestAlgorithmException
extends RuntimeException {
    private static final long serialVersionUID = 319558534317118022L;

    public UnsupportedDigestAlgorithmException() {
    }

    public UnsupportedDigestAlgorithmException(String message) {
        super(message);
    }

    public UnsupportedDigestAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }
}

