/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.security;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.Permission;
import java.security.PrivilegedAction;

public class SecurityUtils {
    private static final MethodHandle doPrivileged = SecurityUtils.lookup();

    private static MethodHandle lookup() {
        try {
            Class<?> klass = ClassLoader.getPlatformClassLoader().loadClass("java.security.AccessController");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return lookup.findStatic(klass, "doPrivileged", MethodType.methodType(Object.class, PrivilegedAction.class));
        }
        catch (Throwable x) {
            return null;
        }
    }

    public static Object getSecurityManager() {
        try {
            return System.class.getMethod("getSecurityManager", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    public static void checkPermission(Permission permission) throws SecurityException {
        Object securityManager = SecurityUtils.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        try {
            securityManager.getClass().getMethod("checkPermission", new Class[0]).invoke(securityManager, permission);
        }
        catch (SecurityException x) {
            throw x;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        MethodHandle methodHandle = doPrivileged;
        if (methodHandle == null) {
            return action.run();
        }
        return SecurityUtils.doPrivileged(methodHandle, action);
    }

    private static <T> T doPrivileged(MethodHandle doPrivileged, PrivilegedAction<T> action) {
        try {
            return (T)doPrivileged.invoke(action);
        }
        catch (Error | RuntimeException x) {
            throw x;
        }
        catch (Throwable x) {
            throw new RuntimeException(x);
        }
    }

    private SecurityUtils() {
    }
}

