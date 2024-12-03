/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff;

public class DiffException
extends RuntimeException {
    public DiffException() {
    }

    public DiffException(String message) {
        super(message);
    }

    public DiffException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiffException(Throwable cause) {
        super(cause);
    }
}

