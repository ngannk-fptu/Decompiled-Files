/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ReflectionUtils
 */
package org.eclipse.gemini.blueprint.util.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ReflectionUtils {
    public static Object invokeMethod(Method method, Object target) {
        return ReflectionUtils.invokeMethod(method, target, null);
    }

    public static Object invokeMethod(Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException ex) {
            org.springframework.util.ReflectionUtils.handleReflectionException((Exception)ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
        catch (InvocationTargetException ex) {
            ReflectionUtils.handleInvocationTargetException(ex);
            return null;
        }
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        Throwable cause = ex.getTargetException();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new InvocationCheckedExceptionWrapper((Exception)cause);
    }

    public static Exception getInvocationException(Exception exception) {
        return exception instanceof InvocationCheckedExceptionWrapper ? ((InvocationCheckedExceptionWrapper)exception).getTargetException() : exception;
    }

    public static class InvocationCheckedExceptionWrapper
    extends RuntimeException {
        private static final long serialVersionUID = 5496580030934775697L;

        public InvocationCheckedExceptionWrapper(Exception cause) {
            super(cause);
        }

        public Exception getTargetException() {
            return (Exception)this.getCause();
        }
    }
}

