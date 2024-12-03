/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.NewMetaMethod;

public class NewStaticMetaMethod
extends NewMetaMethod {
    public NewStaticMetaMethod(CachedMethod method) {
        super(method);
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public int getModifiers() {
        return 9;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        int size = arguments.length;
        Object[] newArguments = new Object[size + 1];
        System.arraycopy(arguments, 0, newArguments, 1, size);
        newArguments[0] = null;
        return super.invoke(null, newArguments);
    }
}

