/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.io.InputStream;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$824
extends GeneratedMetaMethod {
    public dgm$824(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return IOGroovyMethods.eachLine((InputStream)object, (String)objectArray[0], DefaultTypeTransformation.intUnbox(objectArray[1]), (Closure)objectArray[2]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return IOGroovyMethods.eachLine((InputStream)object, (String)objectArray[0], DefaultTypeTransformation.intUnbox(objectArray[1]), (Closure)objectArray[2]);
    }
}

