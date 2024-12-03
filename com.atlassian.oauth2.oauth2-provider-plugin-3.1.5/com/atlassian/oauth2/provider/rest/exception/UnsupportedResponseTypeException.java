/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.authorization.TokenResponseError
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.provider.api.authorization.TokenResponseError;
import com.atlassian.oauth2.provider.rest.exception.BadRequestException;

public class UnsupportedResponseTypeException
extends BadRequestException {
    public UnsupportedResponseTypeException(String description) {
        super(TokenResponseError.UNSUPPORTED_RESPONSE_TYPE.name, description);
    }
}

