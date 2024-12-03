/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$180
extends GeneratedMetaMethod {
    public dgm$180(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultTypeTransformation.box(DefaultGroovyMethods.equals((Object[])object, (List)objectArray[0]));
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DefaultTypeTransformation.box(DefaultGroovyMethods.equals((Object[])object, (List)objectArray[0]));
    }
}

