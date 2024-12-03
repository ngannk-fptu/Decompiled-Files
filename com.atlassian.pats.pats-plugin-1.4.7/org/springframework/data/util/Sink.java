/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.util;

import java.util.function.Consumer;
import org.springframework.lang.Nullable;

class Sink<T>
implements Consumer<T> {
    private T value;

    Sink() {
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public void accept(@Nullable T t) {
        this.value = t;
    }
}

