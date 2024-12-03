/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

import java.util.Optional;
import java.util.function.Supplier;

public interface RequestContext {
    public String partitionIdentifier();

    public <T> T computeIfAbsent(Object var1, Supplier<T> var2);

    public <T> Optional<T> get(Object var1);
}

