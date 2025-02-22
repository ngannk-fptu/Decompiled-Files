/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$ReflectUtils;
import com.google.inject.internal.cglib.core.$Transformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class $MethodInfoTransformer
implements $Transformer {
    private static final $MethodInfoTransformer INSTANCE = new $MethodInfoTransformer();

    public static $MethodInfoTransformer getInstance() {
        return INSTANCE;
    }

    public Object transform(Object value) {
        if (value instanceof Method) {
            return $ReflectUtils.getMethodInfo((Method)value);
        }
        if (value instanceof Constructor) {
            return $ReflectUtils.getMethodInfo((Constructor)value);
        }
        throw new IllegalArgumentException("cannot get method info for " + value);
    }
}

