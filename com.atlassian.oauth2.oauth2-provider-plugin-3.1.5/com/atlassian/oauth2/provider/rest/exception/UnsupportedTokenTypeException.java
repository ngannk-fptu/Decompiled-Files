/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.provider.rest.exception.BadRequestException;

public class UnsupportedTokenTypeException
extends BadRequestException {
    private static final String UNSUPPORTED_TOKEN_TYPE = "unsupported_token_type";

    public UnsupportedTokenTypeException(String description) {
        super(UNSUPPORTED_TOKEN_TYPE, description);
    }
}

