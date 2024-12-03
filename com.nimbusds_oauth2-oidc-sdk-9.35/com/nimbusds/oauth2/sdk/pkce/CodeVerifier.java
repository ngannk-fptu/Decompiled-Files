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
        if (!CodeVerifier.isLegal(value)) {
            throw new IllegalArgumentException("Illegal char(s) in code verifier, see RFC 7636, section 4.1");
        }
    }

    public CodeVerifier() {
        super(32);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof CodeVerifier && super.equals(object);
    }

    static boolean isLegal(String s) {
        if (s == null) {
            return true;
        }
        for (char c : s.toCharArray()) {
            if (CodeVerifier.isLegal(c)) continue;
            return false;
        }
        return true;
    }

    static boolean isLegal(char c) {
        if (c > '\u007f') {
            return false;
        }
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' || c == '.' || c == '_' || c == '~';
    }
}

