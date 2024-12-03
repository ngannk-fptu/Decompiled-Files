/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.projectcreate.crud.exception;

public class CreateSpaceFailureException
extends Exception {
    String message;

    public CreateSpaceFailureException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

