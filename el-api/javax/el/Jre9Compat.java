/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import javax.el.JreCompat;

class Jre9Compat
extends JreCompat {
    private static final Method canAccessMethod;
    private static final Method getModuleMethod;
    private static final Method isExportedMethod;

    Jre9Compat() {
    }

    public static boolean isSupported() {
        return canAccessMethod != null;
    }

    @Override
    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return (Boolean)canAccessMethod.invoke((Object)accessibleObject, base);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    @Override
    public boolean isExported(Class<?> type) {
        try {
            String packageName = type.getPackage().getName();
            Object module = getModuleMethod.invoke(type, new Object[0]);
            return (Boolean)isExportedMethod.invoke(module, packageName);
        }
        catch (ReflectiveOperationException e) {
            return false;
        }
    }

    static {
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            m1 = AccessibleObject.class.getMethod("canAccess", Object.class);
            m2 = Class.class.getMethod("getModule", new Class[0]);
            Class<?> moduleClass = Class.forName("java.lang.Module");
            m3 = moduleClass.getMethod("isExported", String.class);
        }
        catch (NoSuchMethodException moduleClass) {
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        canAccessMethod = m1;
        getModuleMethod = m2;
        isExportedMethod = m3;
    }
}

