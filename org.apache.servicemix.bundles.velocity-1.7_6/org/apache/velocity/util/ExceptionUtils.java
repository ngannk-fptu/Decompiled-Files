/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ExceptionUtils {
    private static boolean causesAllowed = true;

    public static RuntimeException createRuntimeException(String message, Throwable cause) {
        return (RuntimeException)ExceptionUtils.createWithCause(RuntimeException.class, message, cause);
    }

    public static Throwable createWithCause(Class clazz, String message, Throwable cause) {
        Constructor constructor;
        Throwable re = null;
        if (causesAllowed) {
            try {
                constructor = clazz.getConstructor(String.class, Throwable.class);
                re = (Throwable)constructor.newInstance(message, cause);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                causesAllowed = false;
            }
        }
        if (re == null) {
            try {
                constructor = clazz.getConstructor(String.class);
                re = (Throwable)constructor.newInstance(message + " caused by " + cause);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException("Error caused " + e);
            }
        }
        return re;
    }

    public static void setCause(Throwable onObject, Throwable cause) {
        if (causesAllowed) {
            try {
                Method method = onObject.getClass().getMethod("initCause", Throwable.class);
                method.invoke((Object)onObject, cause);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                causesAllowed = false;
            }
        }
    }
}

