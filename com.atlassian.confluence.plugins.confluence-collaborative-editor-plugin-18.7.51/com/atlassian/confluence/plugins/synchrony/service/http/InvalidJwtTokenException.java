/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

public class InvalidJwtTokenException
extends RuntimeException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}

