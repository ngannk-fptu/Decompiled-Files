/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;

public class LongCachedClass
extends NumberCachedClass {
    private boolean allowNull;

    public LongCachedClass(Class klazz, ClassInfo classInfo, boolean allowNull) {
        super(klazz, classInfo);
        this.allowNull = allowNull;
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Long) {
            return argument;
        }
        if (argument instanceof Number) {
            return ((Number)argument).longValue();
        }
        return argument;
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return this.allowNull && argument == null || argument instanceof Long;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return this.allowNull && classToTransformFrom == null || classToTransformFrom == Integer.class || classToTransformFrom == Long.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == Integer.TYPE || classToTransformFrom == Long.TYPE || classToTransformFrom == Short.TYPE || classToTransformFrom == Byte.TYPE;
    }
}

