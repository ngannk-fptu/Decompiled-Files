/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.CasIdentifier
 *  com.atlassian.vcache.IdentifiedValue
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.IdentifiedValue;
import java.util.Objects;

public class DefaultIdentifiedValue<T>
implements IdentifiedValue<T> {
    private final CasIdentifier identifier;
    private final T value;

    public DefaultIdentifiedValue(CasIdentifier identifier, T value) {
        this.identifier = Objects.requireNonNull(identifier);
        this.value = Objects.requireNonNull(value);
    }

    public CasIdentifier identifier() {
        return this.identifier;
    }

    public T value() {
        return this.value;
    }

    public String toString() {
        return "DefaultIdentifiedValue{identifier=" + this.identifier + ", value=" + this.value + '}';
    }
}

