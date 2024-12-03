/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.authorization;

public enum SupportedResponseType {
    CODE("code");

    public final String value;

    private SupportedResponseType(String value) {
        this.value = value;
    }
}

