/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose.crypto.impl;

import net.jcip.annotations.Immutable;

@Immutable
public final class AuthenticatedCipherText {
    private final byte[] cipherText;
    private final byte[] authenticationTag;

    public AuthenticatedCipherText(byte[] cipherText, byte[] authenticationTag) {
        if (cipherText == null) {
            throw new IllegalArgumentException("The cipher text must not be null");
        }
        this.cipherText = cipherText;
        if (authenticationTag == null) {
            throw new IllegalArgumentException("The authentication tag must not be null");
        }
        this.authenticationTag = authenticationTag;
    }

    public byte[] getCipherText() {
        return this.cipherText;
    }

    public byte[] getAuthenticationTag() {
        return this.authenticationTag;
    }
}

