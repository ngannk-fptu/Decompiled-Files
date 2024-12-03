/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Comparator;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$716
extends GeneratedMetaMethod {
    public dgm$716(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.toUnique((Iterable)object, (Comparator)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultGroovyMethods.toUnique((Iterable)object, (Comparator)objectArray[0]);
    }
}

