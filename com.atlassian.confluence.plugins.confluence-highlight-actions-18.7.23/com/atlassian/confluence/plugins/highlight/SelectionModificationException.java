/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight;

public class SelectionModificationException
extends Exception {
    private final Type type;
    private final String message;

    public SelectionModificationException(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public static enum Type {
        NO_OBJECT_TO_MODIFY,
        NO_PERMISSION,
        STALE_OBJECT_TO_MODIFY,
        INCORRECT_MODIFICATION;

    }
}

