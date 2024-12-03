/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import groovy.lang.GString;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.ReflectionCache;

public class StringCachedClass
extends CachedClass {
    private static final Class STRING_CLASS = String.class;
    private static final Class GSTRING_CLASS = GString.class;

    public StringCachedClass(ClassInfo classInfo) {
        super(STRING_CLASS, classInfo);
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return argument instanceof String;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return classToTransformFrom == null || classToTransformFrom == STRING_CLASS || ReflectionCache.isAssignableFrom(GSTRING_CLASS, classToTransformFrom);
    }

    @Override
    public Object coerceArgument(Object argument) {
        return argument instanceof GString ? argument.toString() : argument;
    }
}

