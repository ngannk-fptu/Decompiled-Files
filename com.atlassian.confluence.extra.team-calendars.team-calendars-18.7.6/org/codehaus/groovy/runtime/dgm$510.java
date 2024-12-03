/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.IntRange;
import java.util.BitSet;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$510
extends GeneratedMetaMethod {
    public dgm$510(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DefaultGroovyMethods.putAt((BitSet)object, (IntRange)objectArray[0], DefaultTypeTransformation.booleanUnbox(objectArray[1]));
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DefaultGroovyMethods.putAt((BitSet)object, (IntRange)objectArray[0], DefaultTypeTransformation.booleanUnbox(objectArray[1]));
        return null;
    }
}

