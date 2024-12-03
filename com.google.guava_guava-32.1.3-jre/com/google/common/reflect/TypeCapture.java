/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.reflect;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ElementTypesAreNonnullByDefault;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@ElementTypesAreNonnullByDefault
abstract class TypeCapture<T> {
    TypeCapture() {
    }

    final Type capture() {
        Type superclass = this.getClass().getGenericSuperclass();
        Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", (Object)superclass);
        return ((ParameterizedType)superclass).getActualTypeArguments()[0];
    }
}

