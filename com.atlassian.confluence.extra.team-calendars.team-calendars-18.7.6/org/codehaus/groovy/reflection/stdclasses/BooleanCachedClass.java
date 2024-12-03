/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;

public class BooleanCachedClass
extends CachedClass {
    private boolean allowNull;

    public BooleanCachedClass(Class klazz, ClassInfo classInfo, boolean allowNull) {
        super(klazz, classInfo);
        this.allowNull = allowNull;
    }

    @Override
    public boolean isDirectlyAssignable(Object argument) {
        return this.allowNull && argument == null || argument instanceof Boolean;
    }

    @Override
    public boolean isAssignableFrom(Class classToTransformFrom) {
        return this.allowNull && classToTransformFrom == null || classToTransformFrom == Boolean.class || classToTransformFrom == Boolean.TYPE;
    }
}

