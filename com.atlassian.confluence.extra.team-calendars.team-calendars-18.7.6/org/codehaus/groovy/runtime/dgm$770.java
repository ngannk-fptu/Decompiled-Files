/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.BitSet;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$770
extends GeneratedMetaMethod {
    public dgm$770(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.xor((BitSet)object, (BitSet)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultGroovyMethods.xor((BitSet)object, (BitSet)objectArray[0]);
    }
}

