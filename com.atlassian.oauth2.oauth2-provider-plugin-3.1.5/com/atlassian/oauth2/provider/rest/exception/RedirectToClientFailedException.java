/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

public class RedirectToClientFailedException
extends RuntimeException {
    public RedirectToClientFailedException(Exception e) {
        super("Failed to send redirect to client", e);
    }
}

