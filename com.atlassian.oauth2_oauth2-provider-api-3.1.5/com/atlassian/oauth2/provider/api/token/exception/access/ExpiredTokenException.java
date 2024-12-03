/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.token.exception.access;

import com.atlassian.oauth2.provider.api.token.exception.access.AccessFailedException;

public class ExpiredTokenException
extends AccessFailedException {
    public ExpiredTokenException(String message) {
        super(message);
    }
}

