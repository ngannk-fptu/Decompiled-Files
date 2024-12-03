/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codecimpl.util.ImagingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ImagingListenerProxy {
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Throwable;
    static /* synthetic */ Class class$java$lang$Object;

    public static synchronized boolean errorOccurred(String message, Throwable thrown, Object where, boolean isRetryable) throws RuntimeException {
        Method errorOccurred = null;
        Object listener = null;
        try {
            Class<?> jaiClass = Class.forName("javax.media.jai.JAI");
            if (jaiClass == null) {
                return ImagingListenerProxy.defaultImpl(message, thrown, where, isRetryable);
            }
            Method jaiInstance = jaiClass.getMethod("getDefaultInstance", null);
            Method getListener = jaiClass.getMethod("getImagingListener", null);
            Object jai = jaiInstance.invoke(null, null);
            if (jai == null) {
                return ImagingListenerProxy.defaultImpl(message, thrown, where, isRetryable);
            }
            listener = getListener.invoke(jai, null);
            Class<?> listenerClass = listener.getClass();
            errorOccurred = listenerClass.getMethod("errorOccurred", class$java$lang$String == null ? (class$java$lang$String = ImagingListenerProxy.class$("java.lang.String")) : class$java$lang$String, class$java$lang$Throwable == null ? (class$java$lang$Throwable = ImagingListenerProxy.class$("java.lang.Throwable")) : class$java$lang$Throwable, class$java$lang$Object == null ? (class$java$lang$Object = ImagingListenerProxy.class$("java.lang.Object")) : class$java$lang$Object, Boolean.TYPE);
        }
        catch (Throwable e) {
            return ImagingListenerProxy.defaultImpl(message, thrown, where, isRetryable);
        }
        try {
            Boolean result = (Boolean)errorOccurred.invoke(listener, message, thrown, where, new Boolean(isRetryable));
            return result;
        }
        catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            throw new ImagingException(te);
        }
        catch (Throwable e) {
            return ImagingListenerProxy.defaultImpl(message, thrown, where, isRetryable);
        }
    }

    private static synchronized boolean defaultImpl(String message, Throwable thrown, Object where, boolean isRetryable) throws RuntimeException {
        if (thrown instanceof RuntimeException) {
            throw (RuntimeException)thrown;
        }
        System.err.println("Error: " + message);
        System.err.println("Occurs in: " + (where instanceof Class ? ((Class)where).getName() : where.getClass().getName()));
        thrown.printStackTrace(System.err);
        return false;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

