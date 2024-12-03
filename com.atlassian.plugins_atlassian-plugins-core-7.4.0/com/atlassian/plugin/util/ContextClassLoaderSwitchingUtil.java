/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.util.ClassLoaderStack;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class ContextClassLoaderSwitchingUtil {
    public static void runInContext(ClassLoader newClassLoader, Runnable runnable) {
        try {
            ContextClassLoaderSwitchingUtil.runInContext(newClassLoader, Executors.callable(runnable));
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static <T> T runInContext(ClassLoader newClassLoader, Callable<T> callable) throws Exception {
        ClassLoaderStack.push(newClassLoader);
        try {
            T t = callable.call();
            return t;
        }
        finally {
            ClassLoaderStack.pop();
        }
    }
}

