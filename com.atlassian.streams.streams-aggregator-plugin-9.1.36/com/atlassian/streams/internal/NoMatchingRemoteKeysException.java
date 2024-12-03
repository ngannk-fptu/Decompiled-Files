/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.internal;

import com.google.common.collect.ImmutableList;

public class NoMatchingRemoteKeysException
extends RuntimeException {
    private Iterable<String> keys;

    public NoMatchingRemoteKeysException(Iterable<String> keys) {
        this.keys = ImmutableList.copyOf(keys);
    }

    public Iterable<String> getKeys() {
        return this.keys;
    }
}

