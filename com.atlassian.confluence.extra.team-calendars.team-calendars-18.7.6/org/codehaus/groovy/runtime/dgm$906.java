/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;

public class dgm$906
extends GeneratedMetaMethod {
    public dgm$906(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return ProcessGroovyMethods.getText((Process)object);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return ProcessGroovyMethods.getText((Process)object);
    }
}

