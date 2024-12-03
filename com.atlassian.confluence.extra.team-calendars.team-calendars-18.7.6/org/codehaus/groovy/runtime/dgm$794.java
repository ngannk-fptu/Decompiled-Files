/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.sql.Date;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DateGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$794
extends GeneratedMetaMethod {
    public dgm$794(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DateGroovyMethods.plus((Date)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return DateGroovyMethods.plus((Date)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
    }
}

