/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import org.terracotta.context.extractor.MethodAttributeGetter;

class WeakMethodAttributeGetter<T>
extends MethodAttributeGetter<T> {
    private final WeakReference<Object> targetRef;

    WeakMethodAttributeGetter(Object target, Method method) {
        super(method);
        this.targetRef = new WeakReference<Object>(target);
    }

    @Override
    Object target() {
        return this.targetRef.get();
    }
}

