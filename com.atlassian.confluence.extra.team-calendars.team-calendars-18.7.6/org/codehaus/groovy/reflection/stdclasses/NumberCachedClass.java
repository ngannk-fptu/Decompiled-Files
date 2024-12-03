/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import java.math.BigInteger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;

public class NumberCachedClass
extends CachedClass {
    public NumberCachedClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo);
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Number) {
            return this.coerceNumber(argument);
        }
        return argument;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return classToTransformFrom == null || Number.class.isAssignableFrom(classToTransformFrom) || classToTransformFrom == Byte.TYPE || classToTransformFrom == Short.TYPE || classToTransformFrom == Integer.TYPE || classToTransformFrom == Long.TYPE || classToTransformFrom == Float.TYPE || classToTransformFrom == Double.TYPE;
    }

    private Object coerceNumber(Object argument) {
        Class param = this.getTheClass();
        if (param == Byte.class) {
            argument = ((Number)argument).byteValue();
        } else if (param == BigInteger.class) {
            argument = new BigInteger(String.valueOf((Number)argument));
        }
        return argument;
    }
}

