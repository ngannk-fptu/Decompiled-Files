/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ozymandias.error;

public class ModuleAccessError {
    private final String errorMessage;

    public ModuleAccessError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

