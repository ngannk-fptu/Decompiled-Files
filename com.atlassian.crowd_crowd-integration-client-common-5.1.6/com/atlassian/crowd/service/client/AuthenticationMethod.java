/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service.client;

public enum AuthenticationMethod {
    BASIC_AUTH("basic_auth");

    private final String key;

    private AuthenticationMethod(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public static AuthenticationMethod parse(String key) {
        for (AuthenticationMethod authenticationMethod : AuthenticationMethod.values()) {
            if (!authenticationMethod.key.equals(key)) continue;
            return authenticationMethod;
        }
        throw new IllegalArgumentException("Unknown authentication method: " + key);
    }
}

