/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.sql.Timestamp;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$789
extends GeneratedMetaMethod {
    public dgm$789(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DateGroovyMethods.minus((Timestamp)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DateGroovyMethods.minus((Timestamp)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }
}

