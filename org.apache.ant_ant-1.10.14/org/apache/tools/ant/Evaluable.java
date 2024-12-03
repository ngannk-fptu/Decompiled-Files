/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.function.Supplier;

public interface Evaluable<T>
extends Supplier<T> {
    public T eval();

    @Override
    default public T get() {
        return this.eval();
    }
}

