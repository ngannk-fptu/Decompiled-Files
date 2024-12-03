/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import org.terracotta.context.extractor.FieldAttributeGetter;

public class WeakFieldAttributeGetter<T>
extends FieldAttributeGetter<T> {
    private final WeakReference<Object> targetRef;

    public WeakFieldAttributeGetter(Object target, Field field) {
        super(field);
        this.targetRef = new WeakReference<Object>(target);
    }

    @Override
    Object target() {
        return this.targetRef.get();
    }
}

