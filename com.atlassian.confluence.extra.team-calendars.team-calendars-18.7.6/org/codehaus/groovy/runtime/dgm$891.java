/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;

public class dgm$891
extends GeneratedMetaMethod {
    public dgm$891(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        ProcessGroovyMethods.consumeProcessOutput((Process)object, (Appendable)objectArray[0], (Appendable)objectArray[1]);
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        ProcessGroovyMethods.consumeProcessOutput((Process)object, (Appendable)objectArray[0], (Appendable)objectArray[1]);
        return null;
    }
}

