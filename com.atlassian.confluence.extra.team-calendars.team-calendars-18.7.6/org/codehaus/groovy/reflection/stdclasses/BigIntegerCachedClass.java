/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import java.math.BigInteger;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.stdclasses.NumberCachedClass;

public class BigIntegerCachedClass
extends NumberCachedClass {
    public BigIntegerCachedClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo);
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return argument instanceof BigInteger;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return classToTransformFrom == null || classToTransformFrom == Integer.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == BigInteger.class || classToTransformFrom == Long.class || classToTransformFrom == Integer.TYPE || classToTransformFrom == Short.TYPE || classToTransformFrom == Byte.TYPE || classToTransformFrom == Long.TYPE || BigInteger.class.isAssignableFrom(classToTransformFrom);
    }
}

