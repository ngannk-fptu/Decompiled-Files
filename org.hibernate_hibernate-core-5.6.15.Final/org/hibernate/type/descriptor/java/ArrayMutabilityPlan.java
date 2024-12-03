/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.lang.reflect.Array;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;

public class ArrayMutabilityPlan<T>
extends MutableMutabilityPlan<T> {
    public static final ArrayMutabilityPlan INSTANCE = new ArrayMutabilityPlan();

    @Override
    public T deepCopyNotNull(T value) {
        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("Value was not an array [" + value.getClass().getName() + "]");
        }
        int length = Array.getLength(value);
        Object copy = Array.newInstance(value.getClass().getComponentType(), length);
        System.arraycopy(value, 0, copy, 0, length);
        return (T)copy;
    }
}

