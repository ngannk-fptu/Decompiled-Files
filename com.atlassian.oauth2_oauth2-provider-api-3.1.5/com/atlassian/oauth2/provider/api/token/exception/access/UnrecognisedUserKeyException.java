/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.token.exception.access;

import com.atlassian.oauth2.provider.api.token.exception.access.AccessFailedException;

public class UnrecognisedUserKeyException
extends AccessFailedException {
    public UnrecognisedUserKeyException(String message) {
        super(message);
    }
}

