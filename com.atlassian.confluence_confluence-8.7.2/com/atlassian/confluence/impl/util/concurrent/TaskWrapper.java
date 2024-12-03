/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public interface TaskWrapper {
    public Runnable wrap(Runnable var1);

    public <T> Callable<T> wrap(Callable<T> var1);

    default public <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> callables) {
        return callables.stream().map(task -> this.wrap((Callable)task)).collect(Collectors.toList());
    }
}

