/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;

public final class TagTuple {
    private final String handle;
    private final String suffix;

    public TagTuple(String handle, String suffix) {
        Objects.requireNonNull(suffix, "Suffix must be provided.");
        this.handle = handle;
        this.suffix = suffix;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getSuffix() {
        return this.suffix;
    }
}

