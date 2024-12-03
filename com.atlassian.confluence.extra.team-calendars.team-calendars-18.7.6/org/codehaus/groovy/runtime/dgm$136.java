/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$136
extends GeneratedMetaMethod {
    public dgm$136(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DefaultGroovyMethods.downto((Long)object, (Number)objectArray[0], (Closure)objectArray[1]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DefaultGroovyMethods.downto((Long)object, (Number)objectArray[0], (Closure)objectArray[1]);
        return null;
    }
}

