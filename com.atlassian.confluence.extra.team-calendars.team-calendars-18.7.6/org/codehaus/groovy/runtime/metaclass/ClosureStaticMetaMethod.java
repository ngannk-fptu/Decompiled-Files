/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.Closure;
import groovy.lang.ClosureInvokingMethod;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;

public class ClosureStaticMetaMethod
extends MetaMethod
implements ClosureInvokingMethod {
    private final Closure callable;
    private final CachedClass declaringClass;
    private final String name;

    public ClosureStaticMetaMethod(String name, Class declaringClass, Closure c) {
        this(name, declaringClass, c, c.getParameterTypes());
    }

    public ClosureStaticMetaMethod(String name, Class declaringClass, Closure c, Class[] paramTypes) {
        super(paramTypes);
        this.callable = c;
        this.declaringClass = ReflectionCache.getCachedClass(declaringClass);
        this.name = name;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        Closure cloned = (Closure)this.callable.clone();
        cloned.setDelegate(object);
        return cloned.call(arguments);
    }

    @Override
    public int getModifiers() {
        return 9;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class getReturnType() {
        return Object.class;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.declaringClass;
    }

    @Override
    public Closure getClosure() {
        return this.callable;
    }
}

