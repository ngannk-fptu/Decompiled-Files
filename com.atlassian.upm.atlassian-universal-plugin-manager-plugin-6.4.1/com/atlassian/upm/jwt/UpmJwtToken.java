/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.jwt;

public class UpmJwtToken {
    private final String token;

    public UpmJwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public String toString() {
        return this.getToken();
    }

    public static UpmJwtToken invalidJwtToken() {
        return new UpmJwtToken("INVALID_TOKEN");
    }
}

