/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.math.BigInteger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$139
extends GeneratedMetaMethod {
    public dgm$139(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DefaultGroovyMethods.downto((BigInteger)object, (Number)objectArray[0], (Closure)objectArray[1]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DefaultGroovyMethods.downto((BigInteger)object, (Number)objectArray[0], (Closure)objectArray[1]);
        return null;
    }
}

