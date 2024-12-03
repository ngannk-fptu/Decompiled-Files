/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.util;

public final class Preconditions {
    public static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    private Preconditions() {
    }
}

