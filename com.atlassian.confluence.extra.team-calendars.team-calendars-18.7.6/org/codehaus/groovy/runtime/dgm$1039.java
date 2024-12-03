/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.net.ServerSocket;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.SocketGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class dgm$1039
extends GeneratedMetaMethod {
    public dgm$1039(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return SocketGroovyMethods.accept((ServerSocket)object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Closure)objectArray[1]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return SocketGroovyMethods.accept((ServerSocket)object, DefaultTypeTransformation.booleanUnbox(objectArray[0]), (Closure)objectArray[1]);
    }
}

