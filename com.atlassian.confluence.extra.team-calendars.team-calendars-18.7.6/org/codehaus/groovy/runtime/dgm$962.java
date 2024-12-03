/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.net.URL;
import java.util.Map;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class dgm$962
extends GeneratedMetaMethod {
    public dgm$962(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return ResourceGroovyMethods.getBytes((URL)object, (Map)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return ResourceGroovyMethods.getBytes((URL)object, (Map)objectArray[0]);
    }
}

