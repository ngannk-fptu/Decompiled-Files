/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.provider.rest.exception.BadRequestException;

public class InvalidGrantException
extends BadRequestException {
    private static final String INVALID_GRANT = "invalid_grant";

    public InvalidGrantException(String message) {
        super(INVALID_GRANT, message);
    }
}

