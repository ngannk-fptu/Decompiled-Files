/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.api.exception;

import com.atlassian.plugins.authentication.api.exception.InsufficientLoginOptionsException;

public class CannotDisableLoginFormException
extends InsufficientLoginOptionsException {
    public CannotDisableLoginFormException(String message) {
        super(message);
    }
}

