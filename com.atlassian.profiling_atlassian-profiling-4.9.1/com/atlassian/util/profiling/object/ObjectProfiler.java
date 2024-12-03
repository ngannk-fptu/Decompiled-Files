/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling.object;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.object.Profilable;
import com.atlassian.util.profiling.object.TimerInvocationHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Internal
public class ObjectProfiler {
    public static Object getProfiledObject(Class interfaceClazz, Object o) {
        if (!ObjectProfiler.isActive()) {
            return o;
        }
        if (interfaceClazz.isInterface()) {
            TimerInvocationHandler timerHandler = new TimerInvocationHandler(o);
            return Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class[]{interfaceClazz}, (InvocationHandler)timerHandler);
        }
        return o;
    }

    public static String getTrimmedClassName(Method method) {
        String classname = method.getDeclaringClass().getName();
        return classname.substring(classname.lastIndexOf(46) + 1);
    }

    public static Object profile(String caption, Profilable profilable) throws RuntimeException {
        Object o;
        try (Ticker ignored = Timers.start(caption);){
            o = profilable.profile();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Object profiledInvoke(Method method, Object instance, Object[] args) throws Exception {
        if (!ObjectProfiler.isActive()) {
            try {
                return method.invoke(instance, args);
            }
            catch (InvocationTargetException unProfiledInvocationException) {
                if (unProfiledInvocationException.getCause() == null) throw unProfiledInvocationException;
                ObjectProfiler.throwIfUnchecked(unProfiledInvocationException.getCause());
                throw new RuntimeException(unProfiledInvocationException.getCause());
            }
        }
        String logLine = ObjectProfiler.getTrimmedClassName(method) + "." + method.getName() + "()";
        try (Ticker ignored = Timers.start(logLine);){
            Object returnValue = method.invoke(instance, args);
            if (returnValue != null && method.getReturnType().isInterface()) {
                Set interfaces = ObjectProfiler.getAllInterfaces(returnValue.getClass());
                TimerInvocationHandler timerHandler = new TimerInvocationHandler(returnValue);
                Object object = Proxy.newProxyInstance(returnValue.getClass().getClassLoader(), interfaces.toArray(new Class[0]), (InvocationHandler)timerHandler);
                return object;
            }
            Object object = returnValue;
            return object;
        }
        catch (InvocationTargetException profiledInvocationException) {
            if (profiledInvocationException.getCause() == null) throw profiledInvocationException;
            ObjectProfiler.throwIfUnchecked(profiledInvocationException.getCause());
            throw new RuntimeException(profiledInvocationException.getCause());
        }
    }

    protected static Set getAllInterfaces(Class clazz) {
        HashSet interfaces = new HashSet();
        for (Class cls = clazz; cls != null; cls = cls.getSuperclass()) {
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
        }
        return interfaces;
    }

    private static boolean isActive() {
        return Timers.getConfiguration().isEnabled();
    }

    private static void throwIfUnchecked(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
    }
}

