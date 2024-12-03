/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.NewMetaMethod;

public class NewInstanceMetaMethod
extends NewMetaMethod {
    public NewInstanceMetaMethod(CachedMethod method) {
        super(method);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public int getModifiers() {
        return 1;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        int size = arguments.length;
        Object[] newArguments = new Object[size + 1];
        newArguments[0] = object;
        System.arraycopy(arguments, 0, newArguments, 1, size);
        return super.invoke(null, newArguments);
    }
}

