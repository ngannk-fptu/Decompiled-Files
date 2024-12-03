/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.MissingMethodException;
import org.codehaus.groovy.reflection.CachedMethod;

public class Reflector {
    public Object invoke(CachedMethod method, Object object, Object[] arguments) {
        return this.noSuchMethod(method, object, arguments);
    }

    protected Object noSuchMethod(CachedMethod method, Object object, Object[] arguments) {
        throw new MissingMethodException(method.getName(), method.getDeclaringClass().getTheClass(), arguments, false);
    }
}

