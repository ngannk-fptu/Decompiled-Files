/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class dgm$865
extends GeneratedMetaMethod {
    public dgm$865(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return IOGroovyMethods.splitEachLine((InputStream)object, (Pattern)objectArray[0], (String)objectArray[1], (Closure)objectArray[2]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return IOGroovyMethods.splitEachLine((InputStream)object, (Pattern)objectArray[0], (String)objectArray[1], (Closure)objectArray[2]);
    }
}

