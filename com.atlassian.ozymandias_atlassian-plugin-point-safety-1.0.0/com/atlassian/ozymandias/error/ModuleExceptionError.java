/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ozymandias.error;

import com.atlassian.ozymandias.error.ModuleAccessError;

public class ModuleExceptionError
extends ModuleAccessError {
    private final Throwable exception;

    public ModuleExceptionError(Throwable exception) {
        super("An error occurred accessing the a plugin module");
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }
}

