/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.ObjectOutputStream;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class dgm$845
extends GeneratedMetaMethod {
    public dgm$845(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        IOGroovyMethods.leftShift((ObjectOutputStream)object, objectArray[0]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        IOGroovyMethods.leftShift((ObjectOutputStream)object, objectArray[0]);
        return null;
    }
}

