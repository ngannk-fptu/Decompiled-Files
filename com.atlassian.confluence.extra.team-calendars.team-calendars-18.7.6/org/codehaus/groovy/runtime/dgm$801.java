/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Date;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$801
extends GeneratedMetaMethod {
    public dgm$801(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        DateGroovyMethods.putAt((Date)object, DefaultTypeTransformation.intUnbox(objectArray[0]), DefaultTypeTransformation.intUnbox(objectArray[1]));
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        DateGroovyMethods.putAt((Date)object, DefaultTypeTransformation.intUnbox(objectArray[0]), DefaultTypeTransformation.intUnbox(objectArray[1]));
        return null;
    }
}

