/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import ognl.AccessibleObjectHandler;
import ognl.AccessibleObjectHandlerPreJDK9;
import ognl.OgnlRuntime;

class AccessibleObjectHandlerJDK9Plus
implements AccessibleObjectHandler {
    private static final Class _clazzUnsafe = AccessibleObjectHandlerJDK9Plus.instantiateClazzUnsafe();
    private static final Object _unsafeInstance = AccessibleObjectHandlerJDK9Plus.instantiateUnsafeInstance(_clazzUnsafe);
    private static final Method _unsafeObjectFieldOffsetMethod = AccessibleObjectHandlerJDK9Plus.instantiateUnsafeObjectFieldOffsetMethod(_clazzUnsafe);
    private static final Method _unsafePutBooleanMethod = AccessibleObjectHandlerJDK9Plus.instantiateUnsafePutBooleanMethod(_clazzUnsafe);
    private static final Field _accessibleObjectOverrideField = AccessibleObjectHandlerJDK9Plus.instantiateAccessibleObjectOverrideField();
    private static final long _accessibleObjectOverrideFieldOffset = AccessibleObjectHandlerJDK9Plus.determineAccessibleObjectOverrideFieldOffset();

    private AccessibleObjectHandlerJDK9Plus() {
    }

    static boolean unsafeOrDescendant(Class clazz) {
        return _clazzUnsafe != null ? _clazzUnsafe.isAssignableFrom(clazz) : false;
    }

    private static Class instantiateClazzUnsafe() {
        Class<?> clazz;
        try {
            clazz = Class.forName("sun.misc.Unsafe");
        }
        catch (Throwable t) {
            clazz = null;
        }
        return clazz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object instantiateUnsafeInstance(Class clazz) {
        Object unsafe;
        if (clazz != null) {
            Field field = null;
            try {
                field = clazz.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = field.get(null);
            }
            catch (Throwable t) {
                unsafe = null;
            }
            finally {
                if (field != null) {
                    try {
                        field.setAccessible(false);
                    }
                    catch (Throwable throwable) {}
                }
            }
        } else {
            unsafe = null;
        }
        return unsafe;
    }

    private static Method instantiateUnsafeObjectFieldOffsetMethod(Class clazz) {
        Method method;
        if (clazz != null) {
            try {
                method = clazz.getMethod("objectFieldOffset", Field.class);
            }
            catch (Throwable t) {
                method = null;
            }
        } else {
            method = null;
        }
        return method;
    }

    private static Method instantiateUnsafePutBooleanMethod(Class clazz) {
        Method method;
        if (clazz != null) {
            try {
                method = clazz.getMethod("putBoolean", Object.class, Long.TYPE, Boolean.TYPE);
            }
            catch (Throwable t) {
                method = null;
            }
        } else {
            method = null;
        }
        return method;
    }

    private static Field instantiateAccessibleObjectOverrideField() {
        Field field;
        try {
            field = AccessibleObject.class.getDeclaredField("override");
        }
        catch (Throwable t) {
            field = null;
        }
        return field;
    }

    private static long determineAccessibleObjectOverrideFieldOffset() {
        long offset = -1L;
        if (_accessibleObjectOverrideField != null && _unsafeObjectFieldOffsetMethod != null && _unsafeInstance != null) {
            try {
                offset = (Long)_unsafeObjectFieldOffsetMethod.invoke(_unsafeInstance, _accessibleObjectOverrideField);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return offset;
    }

    static AccessibleObjectHandler createHandler() {
        if (OgnlRuntime.usingJDK9PlusAccessHandler()) {
            return new AccessibleObjectHandlerJDK9Plus();
        }
        return AccessibleObjectHandlerPreJDK9.createHandler();
    }

    @Override
    public void setAccessible(AccessibleObject accessibleObject, boolean flag) {
        boolean operationComplete = false;
        if (_unsafeInstance != null && _unsafePutBooleanMethod != null && _accessibleObjectOverrideFieldOffset != -1L) {
            try {
                _unsafePutBooleanMethod.invoke(_unsafeInstance, accessibleObject, _accessibleObjectOverrideFieldOffset, flag);
                operationComplete = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (!operationComplete) {
            accessibleObject.setAccessible(flag);
        }
    }
}

