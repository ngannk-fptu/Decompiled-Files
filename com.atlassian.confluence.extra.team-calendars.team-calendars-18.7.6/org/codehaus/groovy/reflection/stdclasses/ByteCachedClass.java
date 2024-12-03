/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;

public class ByteCachedClass
extends NumberCachedClass {
    private boolean allowNull;

    public ByteCachedClass(Class klazz, ClassInfo classInfo, boolean allowNull) {
        super(klazz, classInfo);
        this.allowNull = allowNull;
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Byte) {
            return argument;
        }
        if (argument instanceof Number) {
            return ((Number)argument).byteValue();
        }
        return argument;
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return this.allowNull && argument == null || argument instanceof Byte;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return this.allowNull && classToTransformFrom == null || classToTransformFrom == Byte.class || classToTransformFrom == Byte.TYPE;
    }
}

