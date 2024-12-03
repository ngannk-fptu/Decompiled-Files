/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model;

import javax.annotation.Nullable;

public class IllegalRestRepresentationStateException
extends IllegalStateException {
    private final String context;

    public IllegalRestRepresentationStateException(@Nullable String context) {
        super(String.format("Required value '%s' not present", context));
        this.context = context;
    }

    public IllegalRestRepresentationStateException(@Nullable String context, @Nullable String message) {
        super(message);
        this.context = context;
    }

    public IllegalRestRepresentationStateException(@Nullable String context, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    @Nullable
    public String getContext() {
        return this.context;
    }
}

