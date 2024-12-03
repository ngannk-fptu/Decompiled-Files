/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Writable;
import java.io.Writer;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class dgm$884
extends GeneratedMetaMethod {
    public dgm$884(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        IOGroovyMethods.write((Writer)object, (Writable)objectArray[0]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        IOGroovyMethods.write((Writer)object, (Writable)objectArray[0]);
        return null;
    }
}

