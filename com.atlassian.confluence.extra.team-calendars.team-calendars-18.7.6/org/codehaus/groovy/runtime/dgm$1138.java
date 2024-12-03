/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.regex.Matcher;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$1138
extends GeneratedMetaMethod {
    public dgm$1138(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        StringGroovyMethods.setIndex((Matcher)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
        return null;
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        StringGroovyMethods.setIndex((Matcher)object, DefaultTypeTransformation.intUnbox(objectArray[0]));
        return null;
    }
}

