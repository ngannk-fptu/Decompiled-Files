/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$744
extends GeneratedMetaMethod {
    public dgm$744(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.unique((List)object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Closure)objectArray[1]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultGroovyMethods.unique((List)object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Closure)objectArray[1]);
    }
}

