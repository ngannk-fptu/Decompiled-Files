/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MetaMethod;
import java.lang.reflect.InvocationTargetException;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.InvokerInvocationException;

public class ReflectionMetaMethod
extends MetaMethod {
    protected final CachedMethod method;

    public ReflectionMetaMethod(CachedMethod method) {
        this.method = method;
        this.setParametersTypes(method.getParameterTypes());
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
        return this.method.cachedClass;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        try {
            return this.method.setAccessible().invoke(object, arguments);
        }
        catch (IllegalArgumentException e) {
            throw new InvokerInvocationException(e);
        }
        catch (IllegalAccessException e) {
            throw new InvokerInvocationException(e);
        }
        catch (InvocationTargetException e) {
            throw e.getCause() instanceof RuntimeException ? (RuntimeException)e.getCause() : new InvokerInvocationException(e);
        }
    }

    @Override
    public String toString() {
        return this.method.toString();
    }

    @Override
    protected Class[] getPT() {
        return this.method.getNativeParameterTypes();
    }

    public MetaMethod getCachedMethod() {
        return this.method;
    }
}

