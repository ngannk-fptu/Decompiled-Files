/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.exception;

import com.atlassian.plugins.authentication.api.exception.InsufficientLoginOptionsException;

public class CannotDisableIdpException
extends InsufficientLoginOptionsException {
    public CannotDisableIdpException(String message) {
        super(message);
    }
}

