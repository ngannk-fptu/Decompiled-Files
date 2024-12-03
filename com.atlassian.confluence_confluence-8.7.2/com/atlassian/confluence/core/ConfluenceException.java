/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

public class ConfluenceException
extends Exception {
    public ConfluenceException(String message) {
        super(message);
    }

    public ConfluenceException(String message, Throwable innerException) {
        super(message, innerException);
    }
}

