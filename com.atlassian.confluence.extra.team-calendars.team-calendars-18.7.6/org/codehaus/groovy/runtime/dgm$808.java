/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.util.Date;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;

public class dgm$808
extends GeneratedMetaMethod {
    public dgm$808(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DateGroovyMethods.upto((Date)object, (Date)objectArray[0], (Closure)objectArray[1]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DateGroovyMethods.upto((Date)object, (Date)objectArray[0], (Closure)objectArray[1]);
        return null;
    }
}

