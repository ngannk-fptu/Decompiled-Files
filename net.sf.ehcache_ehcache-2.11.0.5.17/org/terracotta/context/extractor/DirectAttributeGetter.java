/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import org.terracotta.context.extractor.AttributeGetter;

class DirectAttributeGetter<T>
implements AttributeGetter<T> {
    private final T object;

    DirectAttributeGetter(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return this.object;
    }
}

