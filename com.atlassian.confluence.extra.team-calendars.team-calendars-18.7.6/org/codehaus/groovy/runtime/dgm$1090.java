/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$1090
extends GeneratedMetaMethod {
    public dgm$1090(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return StringGroovyMethods.getAt((String)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return StringGroovyMethods.getAt((String)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }
}

