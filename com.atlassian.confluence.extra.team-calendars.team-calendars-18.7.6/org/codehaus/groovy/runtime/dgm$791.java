/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.sql.Date;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;

public class dgm$791
extends GeneratedMetaMethod {
    public dgm$791(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DateGroovyMethods.next((Date)object);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DateGroovyMethods.next((Date)object);
    }
}

