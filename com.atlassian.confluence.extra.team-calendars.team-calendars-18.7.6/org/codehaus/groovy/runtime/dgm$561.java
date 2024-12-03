/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Comparator;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$561
extends GeneratedMetaMethod {
    public dgm$561(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.sort((Object[])object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Comparator)objectArray[1]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultGroovyMethods.sort((Object[])object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Comparator)objectArray[1]);
    }
}

