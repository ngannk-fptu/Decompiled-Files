/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.binary.Hex
 */
package com.atlassian.security.random;

import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;
import com.atlassian.security.random.SecureTokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public final class DefaultSecureTokenGenerator
implements SecureTokenGenerator {
    private static final SecureTokenGenerator INSTANCE = new DefaultSecureTokenGenerator(DefaultSecureRandomService.getInstance());
    private static final int TOKEN_LENGTH_BYTES = 20;
    private final SecureRandomService randomService;

    DefaultSecureTokenGenerator(SecureRandomService randomService) {
        this.randomService = randomService;
    }

    public static SecureTokenGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generateToken() {
        byte[] bytes = new byte[20];
        this.randomService.nextBytes(bytes);
        return new String(Hex.encodeHex((byte[])bytes));
    }

    @Override
    public String generateNonce() {
        byte[] bytes = new byte[20];
        this.randomService.nextBytes(bytes);
        return new String(Base64.encodeBase64((byte[])bytes));
    }
}

