/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsException
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.StreamsException;

public final class NoSuchModuleException
extends StreamsException {
    private final String key;

    public NoSuchModuleException(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

