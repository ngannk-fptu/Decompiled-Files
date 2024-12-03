/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.math.BigInteger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class dgm$488
extends GeneratedMetaMethod {
    public dgm$488(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.power((BigInteger)object, (Integer)objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        return DefaultGroovyMethods.power((BigInteger)object, (Integer)this.getParameterTypes()[0].coerceArgument(objectArray[0]));
    }

    public boolean isValidMethod(Class[] classArray) {
        return classArray == null || this.getParameterTypes()[0].isAssignableFrom(classArray[0]);
    }
}

