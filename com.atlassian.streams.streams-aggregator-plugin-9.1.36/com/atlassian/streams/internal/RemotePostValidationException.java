/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsException
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.StreamsException;

public class RemotePostValidationException
extends StreamsException {
    private final Iterable<String> errors;

    public RemotePostValidationException(Iterable<String> errors) {
        this.errors = errors;
    }

    public Iterable<String> getErrors() {
        return this.errors;
    }
}

