/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Date;
import java.util.Map;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;

public class dgm$803
extends GeneratedMetaMethod {
    public dgm$803(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DateGroovyMethods.set((Date)object, (Map<Object, Integer>)((Map)objectArray[0]));
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DateGroovyMethods.set((Date)object, (Map<Object, Integer>)((Map)objectArray[0]));
        return null;
    }
}

