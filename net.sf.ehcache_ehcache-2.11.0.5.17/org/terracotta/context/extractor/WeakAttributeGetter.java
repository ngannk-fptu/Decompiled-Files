/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.lang.ref.WeakReference;
import org.terracotta.context.extractor.AttributeGetter;

class WeakAttributeGetter<T>
implements AttributeGetter<T> {
    private final WeakReference<T> reference;

    public WeakAttributeGetter(T object) {
        this.reference = new WeakReference<T>(object);
    }

    @Override
    public T get() {
        return this.reference.get();
    }
}

