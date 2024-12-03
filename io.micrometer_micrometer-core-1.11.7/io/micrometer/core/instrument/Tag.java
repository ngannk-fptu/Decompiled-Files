/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument;

import io.micrometer.core.instrument.ImmutableTag;

public interface Tag
extends Comparable<Tag> {
    public String getKey();

    public String getValue();

    public static Tag of(String key, String value) {
        return new ImmutableTag(key, value);
    }

    @Override
    default public int compareTo(Tag o) {
        return this.getKey().compareTo(o.getKey());
    }
}

