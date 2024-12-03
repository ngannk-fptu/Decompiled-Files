/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.properties;

import com.hazelcast.config.properties.ValidationException;

public interface ValueValidator<T extends Comparable<T>> {
    public void validate(T var1) throws ValidationException;
}

