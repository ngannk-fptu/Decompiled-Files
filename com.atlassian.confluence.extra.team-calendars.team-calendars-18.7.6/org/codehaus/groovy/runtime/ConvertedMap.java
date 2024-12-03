/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.lang.reflect.Method;
import java.util.Map;
import org.codehaus.groovy.runtime.ConversionHandler;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class ConvertedMap
extends ConversionHandler {
    protected ConvertedMap(Map closures) {
        super(closures);
    }

    @Override
    public Object invokeCustom(Object proxy, Method method, Object[] args) throws Throwable {
        Map m = (Map)this.getDelegate();
        Closure cl = (Closure)m.get(method.getName());
        if (cl == null && "toString".equals(method.getName())) {
            return m.toString();
        }
        if (cl == null) {
            throw new UnsupportedOperationException();
        }
        return cl.call(args);
    }

    @Override
    public String toString() {
        return DefaultGroovyMethods.toString(this.getDelegate());
    }

    @Override
    protected boolean checkMethod(Method method) {
        return ConvertedMap.isCoreObjectMethod(method);
    }

    public static boolean isCoreObjectMethod(Method method) {
        return ConversionHandler.isCoreObjectMethod(method) && !"toString".equals(method.getName());
    }
}

