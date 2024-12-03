/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.authorization;

import com.atlassian.oauth2.provider.api.authorization.TokenResponseError;

public enum TokenResponseErrorDescription {
    INVALID_CLIENT_ID(TokenResponseError.INVALID_CLIENT, "invalid_client_id"),
    INVALID_REDIRECT_URI(TokenResponseError.INVALID_GRANT, "invalid_redirect_uri"),
    INVALID_CODE(TokenResponseError.INVALID_REQUEST, "invalid_code");

    public final TokenResponseError error;
    public final String name;

    private TokenResponseErrorDescription(TokenResponseError error, String name) {
        this.error = error;
        this.name = name;
    }
}

