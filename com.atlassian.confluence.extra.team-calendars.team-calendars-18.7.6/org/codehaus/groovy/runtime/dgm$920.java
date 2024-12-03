/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.File;
import java.io.Writer;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class dgm$920
extends GeneratedMetaMethod {
    public dgm$920(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        ResourceGroovyMethods.append((File)object, (Writer)objectArray[0]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        ResourceGroovyMethods.append((File)object, (Writer)objectArray[0]);
        return null;
    }
}

