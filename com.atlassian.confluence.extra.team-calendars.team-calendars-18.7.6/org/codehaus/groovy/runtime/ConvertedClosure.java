/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.codehaus.groovy.runtime.ConversionHandler;

public class ConvertedClosure
extends ConversionHandler
implements Serializable {
    private final String methodName;
    private static final long serialVersionUID = 1162833713450835227L;

    public ConvertedClosure(Closure closure, String method) {
        super(closure);
        this.methodName = method;
    }

    public ConvertedClosure(Closure closure) {
        this(closure, null);
    }

    @Override
    public Object invokeCustom(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.methodName != null && !this.methodName.equals(method.getName())) {
            return null;
        }
        return ((Closure)this.getDelegate()).call(args);
    }
}

