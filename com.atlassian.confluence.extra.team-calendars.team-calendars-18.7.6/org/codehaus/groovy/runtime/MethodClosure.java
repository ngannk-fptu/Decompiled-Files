/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.MetaMethod;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;

public class MethodClosure
extends Closure {
    public static boolean ALLOW_RESOLVE = false;
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private String method;

    public MethodClosure(Object owner, String method) {
        super(owner);
        this.method = method;
        Class<?> clazz = owner.getClass() == Class.class ? (Class<?>)owner : owner.getClass();
        this.maximumNumberOfParameters = 0;
        this.parameterTypes = EMPTY_CLASS_ARRAY;
        List<MetaMethod> methods = InvokerHelper.getMetaClass(clazz).respondsTo(owner, method);
        for (MetaMethod m : methods) {
            if (m.getParameterTypes().length <= this.maximumNumberOfParameters) continue;
            Class[] pt = m.getNativeParameterTypes();
            this.maximumNumberOfParameters = pt.length;
            this.parameterTypes = pt;
        }
    }

    public String getMethod() {
        return this.method;
    }

    protected Object doCall(Object arguments) {
        return InvokerHelper.invokeMethod(this.getOwner(), this.method, arguments);
    }

    private Object readResolve() {
        if (ALLOW_RESOLVE) {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (ALLOW_RESOLVE) {
            stream.defaultReadObject();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String property) {
        if ("method".equals(property)) {
            return this.getMethod();
        }
        return super.getProperty(property);
    }
}

