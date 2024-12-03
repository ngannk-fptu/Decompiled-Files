/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.net.URL;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class dgm$1009
extends GeneratedMetaMethod {
    public dgm$1009(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return ResourceGroovyMethods.splitEachLine((URL)object, (String)objectArray[0], (String)objectArray[1], (Closure)objectArray[2]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return ResourceGroovyMethods.splitEachLine((URL)object, (String)objectArray[0], (String)objectArray[1], (Closure)objectArray[2]);
    }
}

