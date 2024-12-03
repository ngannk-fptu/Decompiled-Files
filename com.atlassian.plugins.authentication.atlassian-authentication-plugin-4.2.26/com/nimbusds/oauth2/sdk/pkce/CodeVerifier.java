/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.pkce;

import com.nimbusds.oauth2.sdk.auth.Secret;

public class CodeVerifier
extends Secret {
    private static final long serialVersionUID = 1L;
    public static final int MIN_LENGTH = 43;
    public static final int MAX_LENGTH = 128;

    public CodeVerifier(String value) {
        super(value);
        if (value.length() < 43) {
            throw new IllegalArgumentException("The code verifier must be at least 43 characters");
        }
        if (value.length() > 128) {
            throw new IllegalArgumentException("The code verifier must not be longer than 128 characters");
        }
    }

    public CodeVerifier() {
        super(32);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof CodeVerifier && super.equals(object);
    }
}

