/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

public class ResourceModelIssue {
    Object source;
    String message;
    boolean fatal;

    public ResourceModelIssue(Object source, String message) {
        this(source, message, false);
    }

    public ResourceModelIssue(Object source, String message, boolean fatal) {
        this.source = source;
        this.message = message;
        this.fatal = fatal;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isFatal() {
        return this.fatal;
    }

    public Object getSource() {
        return this.source;
    }
}

