/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
final class InjectorHelper {
    private static Unsafe UNSAFE;
    private static long OFFSET;

    private InjectorHelper() {
    }

    static Method getMethod(Class<?> c, String methodname, Class<?> ... params) {
        try {
            Method m = c.getDeclaredMethod(methodname, params);
            InjectorHelper.setAccessible(m);
            return m;
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    private static <T extends AccessibleObject> T setAccessible(T accessor) {
        if (OFFSET != -1L) {
            UNSAFE.putBoolean(accessor, OFFSET, true);
        } else {
            accessor.setAccessible(true);
        }
        return accessor;
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = AccessController.doPrivileged(new PrivilegedAction<Field>(){

                @Override
                public Field run() {
                    try {
                        return Unsafe.class.getDeclaredField("theUnsafe");
                    }
                    catch (NoSuchFieldException | SecurityException ex) {
                        Logger.getLogger(InjectorHelper.class.getName()).log(Level.WARNING, null, ex);
                        return null;
                    }
                }
            });
            if (f != null) {
                InjectorHelper.setAccessible(f);
                return (Unsafe)f.get(null);
            }
        }
        catch (Throwable t) {
            Logger.getLogger(InjectorHelper.class.getName()).log(Level.WARNING, null, t);
        }
        return null;
    }

    static {
        OFFSET = -1L;
        UNSAFE = InjectorHelper.getUnsafe();
        if (UNSAFE != null) {
            Field f = AccessController.doPrivileged(new PrivilegedAction<Field>(){

                @Override
                public Field run() {
                    try {
                        return AccessibleObject.class.getDeclaredField("override");
                    }
                    catch (NoSuchFieldException | SecurityException ex) {
                        return null;
                    }
                }
            });
            if (f == null) {
                try {
                    f = dummy.class.getDeclaredField("override");
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    // empty catch block
                }
            }
            OFFSET = UNSAFE.objectFieldOffset(f);
        }
    }

    static class dummy {
        boolean override;
        Object other;

        dummy() {
        }
    }
}

