/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.net.Socket;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.SocketGroovyMethods;

public class dgm$1040
extends GeneratedMetaMethod {
    public dgm$1040(String string, CachedClass cachedClass, Class clazz, Class[] classArray) {
        super(string, cachedClass, clazz, classArray);
    }

    public Object invoke(Object object, Object[] objectArray) {
        return SocketGroovyMethods.leftShift((Socket)object, (byte[])objectArray[0]);
    }

    public final Object doMethodInvoke(Object object, Object[] objectArray) {
        objectArray = this.coerceArgumentsToClasses(objectArray);
        return SocketGroovyMethods.leftShift((Socket)object, (byte[])objectArray[0]);
    }
}

