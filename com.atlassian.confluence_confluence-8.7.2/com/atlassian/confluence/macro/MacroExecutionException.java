/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

public class MacroExecutionException
extends Exception {
    public MacroExecutionException() {
    }

    public MacroExecutionException(String message) {
        super(message);
    }

    public MacroExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MacroExecutionException(Throwable cause) {
        super(cause);
    }
}

