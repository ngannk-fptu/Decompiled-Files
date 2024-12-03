/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.MixinInMetaClass;

public class MixinInstanceMetaMethod
extends MetaMethod {
    private final MetaMethod method;
    private final MixinInMetaClass mixinInMetaClass;

    public MixinInstanceMetaMethod(MetaMethod method, MixinInMetaClass mixinInMetaClass) {
        this.method = method;
        this.mixinInMetaClass = mixinInMetaClass;
    }

    @Override
    public int getModifiers() {
        return this.method.getModifiers();
    }

    @Override
    public String getName() {
        return this.method.getName();
    }

    @Override
    public Class getReturnType() {
        return this.method.getReturnType();
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.mixinInMetaClass.getInstanceClass();
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        this.method.getParameterTypes();
        return this.method.invoke(this.mixinInMetaClass.getMixinInstance(object), this.method.correctArguments(arguments));
    }

    @Override
    protected Class[] getPT() {
        return this.method.getNativeParameterTypes();
    }
}

