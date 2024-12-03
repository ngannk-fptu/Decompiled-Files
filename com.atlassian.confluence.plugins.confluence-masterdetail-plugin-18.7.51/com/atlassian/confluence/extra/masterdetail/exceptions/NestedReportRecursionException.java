/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail.exceptions;

public class NestedReportRecursionException
extends RuntimeException {
    public NestedReportRecursionException(String message) {
        super(message);
    }

    public NestedReportRecursionException(String message, Throwable innerException) {
        super(message, innerException);
    }
}

