/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TransportException;

public abstract class InvalidRequestException
extends TransportException {
    public InvalidRequestException(TransportErrorMessage error) {
        super(error);
    }
}

