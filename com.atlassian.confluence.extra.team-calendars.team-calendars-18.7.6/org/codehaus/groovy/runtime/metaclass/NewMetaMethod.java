/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;

public class NewMetaMethod
extends ReflectionMetaMethod {
    protected static final CachedClass[] EMPTY_TYPE_ARRAY = new CachedClass[0];
    protected CachedClass[] bytecodeParameterTypes;

    public NewMetaMethod(CachedMethod method) {
        super(method);
        CachedClass[] logicalParameterTypes;
        this.bytecodeParameterTypes = method.getParameterTypes();
        int size = this.bytecodeParameterTypes.length;
        if (size <= 1) {
            logicalParameterTypes = EMPTY_TYPE_ARRAY;
        } else {
            logicalParameterTypes = new CachedClass[--size];
            System.arraycopy(this.bytecodeParameterTypes, 1, logicalParameterTypes, 0, size);
        }
        this.setParametersTypes(logicalParameterTypes);
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.getBytecodeParameterTypes()[0];
    }

    public CachedClass[] getBytecodeParameterTypes() {
        return this.bytecodeParameterTypes;
    }

    public CachedClass getOwnerClass() {
        return this.getBytecodeParameterTypes()[0];
    }
}

