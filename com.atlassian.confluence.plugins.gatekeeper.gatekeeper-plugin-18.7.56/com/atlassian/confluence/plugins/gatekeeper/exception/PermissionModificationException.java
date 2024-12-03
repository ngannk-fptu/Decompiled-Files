/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.exception;

public class PermissionModificationException
extends Exception {
    public PermissionModificationException(String message) {
        super(message);
    }

    public PermissionModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}

