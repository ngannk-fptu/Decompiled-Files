/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.File;
import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;

public class dgm$901
extends GeneratedMetaMethod {
    public dgm$901(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return ProcessGroovyMethods.execute((List)object, (String[])objectArray[0], (File)objectArray[1]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return ProcessGroovyMethods.execute((List)object, (String[])objectArray[0], (File)objectArray[1]);
    }
}

