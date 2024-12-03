/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import java.math.BigInteger;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;

public class IntegerCachedClass
extends NumberCachedClass {
    private boolean allowNull;

    public IntegerCachedClass(Class klazz, ClassInfo classInfo, boolean allowNull) {
        super(klazz, classInfo);
        this.allowNull = allowNull;
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Integer) {
            return argument;
        }
        if (argument instanceof Number) {
            return ((Number)argument).intValue();
        }
        return argument;
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return this.allowNull && argument == null || argument instanceof Integer;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return this.allowNull && classToTransformFrom == null || classToTransformFrom == Integer.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == BigInteger.class || classToTransformFrom == Integer.TYPE || classToTransformFrom == Short.TYPE || classToTransformFrom == Byte.TYPE;
    }
}

