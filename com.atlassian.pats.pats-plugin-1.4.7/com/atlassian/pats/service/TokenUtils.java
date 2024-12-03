/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.Nonnull;

public final class TokenUtils {
    public static final char DELIMITER = ':';
    public static final int ID_LENGTH = 12;
    public static final int SECRET_LENGTH = 20;
    public static final int TOKEN_LENGTH = 33;

    public static ExtractedTokenInfo extractTokenInfo(String token) {
        byte[] decodedToken = Base64.getDecoder().decode(token);
        if (TokenUtils.isValidToken(decodedToken)) {
            return new ExtractedTokenInfo(TokenUtils.getTokenId(decodedToken), TokenUtils.getSecret(decodedToken));
        }
        throw new IllegalArgumentException("Token format is invalid");
    }

    private static boolean isValidToken(@Nonnull byte[] decodedToken) {
        return decodedToken.length == 33 && decodedToken[12] == 58;
    }

    private static String getTokenId(byte[] decodedToken) {
        return new String(Arrays.copyOfRange(decodedToken, 0, 12), StandardCharsets.UTF_8);
    }

    private static String getSecret(byte[] decodedToken) {
        byte[] secret = Arrays.copyOfRange(decodedToken, 13, decodedToken.length);
        return Base64.getEncoder().encodeToString(secret);
    }

    private TokenUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final class ExtractedTokenInfo {
        private final String tokenId;
        private final String secret;

        public String getTokenId() {
            return this.tokenId;
        }

        public String getSecret() {
            return this.secret;
        }

        public ExtractedTokenInfo(String tokenId, String secret) {
            this.tokenId = tokenId;
            this.secret = secret;
        }
    }
}

