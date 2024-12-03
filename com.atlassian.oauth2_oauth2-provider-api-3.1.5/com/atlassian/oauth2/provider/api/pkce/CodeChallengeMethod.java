/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.pkce;

public enum CodeChallengeMethod {
    PLAIN("plain"),
    S256("S256");

    private final String method;

    private CodeChallengeMethod(String method) {
        this.method = method;
    }

    public static CodeChallengeMethod fromString(String codeChallengeMethodString) {
        if (codeChallengeMethodString == null) {
            return null;
        }
        try {
            return CodeChallengeMethod.valueOf(codeChallengeMethodString.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String toString() {
        return this.method;
    }
}

