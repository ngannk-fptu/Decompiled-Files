/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.exceptions;

public class ExemptionsLimitExceededException
extends RuntimeException {
    private static final long serialVersionUID = 8287746290598464858L;

    public ExemptionsLimitExceededException(String message) {
        super(message);
    }
}

