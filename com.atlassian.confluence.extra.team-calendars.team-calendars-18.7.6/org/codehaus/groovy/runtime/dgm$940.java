/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.io.FileType;
import groovy.lang.Closure;
import java.io.File;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

public class dgm$940
extends GeneratedMetaMethod {
    public dgm$940(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        ResourceGroovyMethods.eachFileMatch((File)object, (FileType)((Object)objectArray[0]), objectArray[1], (Closure)objectArray[2]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        ResourceGroovyMethods.eachFileMatch((File)object, (FileType)((Object)objectArray[0]), objectArray[1], (Closure)objectArray[2]);
        return null;
    }
}

