/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.provider.rest.exception.BadRequestException;

public class UnsupportedGrantTypeException
extends BadRequestException {
    private static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";

    public UnsupportedGrantTypeException(String description) {
        super(UNSUPPORTED_GRANT_TYPE, description);
    }
}

