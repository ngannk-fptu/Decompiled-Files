/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.net.URL;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class dgm$964
extends GeneratedMetaMethod {
    public dgm$964(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return ResourceGroovyMethods.getText((URL)object);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return ResourceGroovyMethods.getText((URL)object);
    }
}

