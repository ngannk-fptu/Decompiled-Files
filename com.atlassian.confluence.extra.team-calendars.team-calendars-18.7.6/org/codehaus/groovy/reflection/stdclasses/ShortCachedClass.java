/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;

public class ShortCachedClass
extends NumberCachedClass {
    private boolean allowNull;

    public ShortCachedClass(Class klazz, ClassInfo classInfo, boolean allowNull) {
        super(klazz, classInfo);
        this.allowNull = allowNull;
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Short) {
            return argument;
        }
        if (argument instanceof Number) {
            return ((Number)argument).shortValue();
        }
        return argument;
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return this.allowNull && argument == null || argument instanceof Short;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return this.allowNull && classToTransformFrom == null || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == Short.TYPE || classToTransformFrom == Byte.TYPE;
    }
}

