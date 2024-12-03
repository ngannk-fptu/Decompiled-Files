/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.StringGroovyMethods;

public class dgm$1134
extends GeneratedMetaMethod {
    public dgm$1134(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return StringGroovyMethods.replaceFirst((CharSequence)object, (CharSequence)objectArray[0], (CharSequence)objectArray[1]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return StringGroovyMethods.replaceFirst((CharSequence)object, (CharSequence)objectArray[0], (CharSequence)objectArray[1]);
    }
}

