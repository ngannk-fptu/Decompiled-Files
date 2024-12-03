/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.authorization;

public enum TokenResponseError {
    INVALID_CLIENT("invalid_client"),
    INVALID_GRANT("invalid_grant"),
    INVALID_REQUEST("invalid_request"),
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type");

    public final String name;

    private TokenResponseError(String name) {
        this.name = name;
    }
}

