/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

public class BadSAML2AssertionException
extends Exception {
    private static final long serialVersionUID = 7849539907246003512L;

    public BadSAML2AssertionException(String message) {
        super(message);
    }

    public BadSAML2AssertionException(String message, Throwable cause) {
        super(message, cause);
    }
}

