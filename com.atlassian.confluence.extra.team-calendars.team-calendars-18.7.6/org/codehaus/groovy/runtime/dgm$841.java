/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class dgm$841
extends GeneratedMetaMethod {
    public dgm$841(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return IOGroovyMethods.leftShift((OutputStream)object, (InputStream)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return IOGroovyMethods.leftShift((OutputStream)object, (InputStream)objectArray[0]);
    }
}

