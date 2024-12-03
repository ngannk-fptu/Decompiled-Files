/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.IntRange;
import java.util.BitSet;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$246
extends GeneratedMetaMethod {
    public dgm$246(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.getAt((BitSet)object, (IntRange)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultGroovyMethods.getAt((BitSet)object, (IntRange)objectArray[0]);
    }
}

