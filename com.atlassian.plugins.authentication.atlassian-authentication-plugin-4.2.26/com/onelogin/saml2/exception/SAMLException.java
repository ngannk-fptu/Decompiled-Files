/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.exception;

public class SAMLException
extends Exception {
    private static final long serialVersionUID = 1L;

    public SAMLException(String message) {
        super(message);
    }

    public SAMLException(Throwable cause) {
        super(cause);
    }

    public SAMLException(String message, Throwable cause) {
        super(message, cause);
    }
}

