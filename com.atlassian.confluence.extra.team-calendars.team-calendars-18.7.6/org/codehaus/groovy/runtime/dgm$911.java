/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$911
extends GeneratedMetaMethod {
    public dgm$911(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        ProcessGroovyMethods.waitForOrKill((Process)object, DefaultTypeTransformation.longUnbox(objectArray[0]));
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        ProcessGroovyMethods.waitForOrKill((Process)object, DefaultTypeTransformation.longUnbox(objectArray[0]));
        return null;
    }
}

