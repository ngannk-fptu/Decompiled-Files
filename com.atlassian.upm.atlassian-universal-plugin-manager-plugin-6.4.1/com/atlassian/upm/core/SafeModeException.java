/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

public class SafeModeException
extends RuntimeException {
    public SafeModeException(String message) {
        super(message);
    }

    public SafeModeException(String message, Throwable cause) {
        super(message, cause);
    }
}

