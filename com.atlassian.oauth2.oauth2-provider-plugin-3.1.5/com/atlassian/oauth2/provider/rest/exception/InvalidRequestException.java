/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.provider.rest.exception.BadRequestException;

public class InvalidRequestException
extends BadRequestException {
    private static final String INVALID_REQUEST = "invalid_request";

    public InvalidRequestException(String message) {
        super(INVALID_REQUEST, message);
    }
}

