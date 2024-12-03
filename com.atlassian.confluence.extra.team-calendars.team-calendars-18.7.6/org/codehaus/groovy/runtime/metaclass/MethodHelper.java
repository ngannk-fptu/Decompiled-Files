/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import java.lang.reflect.Method;

public class MethodHelper {
    public static boolean isStatic(Method method) {
        int flags = 8;
        return (method.getModifiers() & flags) == flags;
    }

    public static boolean isPublic(Method method) {
        int flags = 1;
        return (method.getModifiers() & flags) == flags;
    }
}

