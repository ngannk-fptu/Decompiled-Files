/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import java.util.function.Function;

public interface BatchOperation<I, O> {
    public void prepare();

    public Iterable<I> input();

    @Deprecated
    default public Function<I, O> operation() {
        return this.getOperataion();
    }

    public Function<I, O> getOperataion();

    public int getExpectedTotal();

    public void done();
}

