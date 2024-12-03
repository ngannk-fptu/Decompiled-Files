/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureRandomService
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.jwt.internal.security;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.security.random.DefaultSecureRandomService;
import org.apache.commons.codec.binary.Base64;

public class SecretGenerator {
    public static String generateUrlSafeSharedSecret(SigningAlgorithm alg) {
        int length;
        switch (alg) {
            case HS256: {
                length = 32;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognised " + SigningAlgorithm.class.getSimpleName() + ": " + (Object)((Object)alg));
            }
        }
        byte[] bytes = new byte[length];
        DefaultSecureRandomService.getInstance().nextBytes(bytes);
        return Base64.encodeBase64URLSafeString((byte[])bytes);
    }
}

