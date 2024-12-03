/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.threadlocal;

import com.atlassian.threadlocal.RegisteredThreadLocals;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BruteForceThreadLocalCleanup {
    private static final Logger log = LoggerFactory.getLogger(BruteForceThreadLocalCleanup.class);

    public static void cleanUp(ClassLoader webAppClassLoader) {
        try {
            RegisteredThreadLocals.reset();
            new BruteForceThreadLocalCleanup().cleanupThreadLocals(webAppClassLoader);
        }
        catch (Exception e) {
            BruteForceThreadLocalCleanup.logErr(String.format("Unable to cleanup ThreadLocal data with Tomcat approach : %s", e.getMessage()));
        }
    }

    public static void cleanUp(ClassLoader webAppClassLoader, Thread targetThread) {
        try {
            new BruteForceThreadLocalCleanup().cleanupThreadLocals(webAppClassLoader, targetThread);
        }
        catch (Exception e) {
            BruteForceThreadLocalCleanup.logErr(String.format("Unable to cleanup ThreadLocal data with Tomcat approach : %s", e.getMessage()));
        }
    }

    private void cleanupThreadLocals(ClassLoader webAppClassLoader) throws Exception {
        ThreadLocalMapReflectedContext threadLocalMapReflectedContext = new ThreadLocalMapReflectedContext().invoke();
        boolean problems = false;
        for (Thread thread : this.getThreads()) {
            problems = this.cleanUpThreadImpl(webAppClassLoader, thread, threadLocalMapReflectedContext);
        }
        if (problems |= this.cleanUpThreadImpl(webAppClassLoader, Thread.currentThread(), threadLocalMapReflectedContext)) {
            BruteForceThreadLocalCleanup.logErr("\n\nIf you see any log messages about ThreadLocals it means your application and/or the libraries it uses have left ThreadLocal variables dangling.\n\nThe code should have called ThreadLocal.remove() after the request thread was finished.  Setting a ThreadLocal to null is not good enough.  It is still leaking inside that Thread.\n\nThe ThreadLocal cleanup code has run and forcibly remove them by calling the reflective equivalent of ThreadLocal.remove() for you.\n\nThis message is only shown in DEVMODE for Atlassian developers.\n\nProduction code will only see these messages if they have DEBUG level logging turned on.\n\nCustomers, however, will also NOT see warnings from Tomcat about these ThreadLocal leaks and hence this will reduce anxiety and support while increasing their confidence in our products.\n\n");
        }
    }

    private boolean cleanupThreadLocals(ClassLoader webAppClassLoader, Thread targetThread) throws Exception {
        ThreadLocalMapReflectedContext threadLocalMapReflectedContext = new ThreadLocalMapReflectedContext().invoke();
        return this.cleanUpThreadImpl(webAppClassLoader, targetThread, threadLocalMapReflectedContext);
    }

    private boolean cleanUpThreadImpl(ClassLoader webAppClassLoader, Thread thread, ThreadLocalMapReflectedContext threadLocalMapReflectedContext) throws IllegalAccessException, NoSuchFieldException {
        boolean problems = false;
        if (thread != null) {
            Object threadLocalMap = threadLocalMapReflectedContext.getThreadLocalsField().get(thread);
            problems = this.checkThreadLocalMapForLeaks(threadLocalMap, threadLocalMapReflectedContext, thread, webAppClassLoader);
            threadLocalMap = threadLocalMapReflectedContext.getInheritableThreadLocalsField().get(thread);
            problems |= this.checkThreadLocalMapForLeaks(threadLocalMap, threadLocalMapReflectedContext, thread, webAppClassLoader);
        }
        return problems;
    }

    private boolean checkThreadLocalMapForLeaks(Object threadLocalMap, ThreadLocalMapReflectedContext threadLocalMapReflectedContext, Thread thread, ClassLoader webAppClassLoader) throws IllegalAccessException, NoSuchFieldException {
        Object[] referenceTable;
        boolean problems = false;
        if (threadLocalMap != null && (referenceTable = (Object[])threadLocalMapReflectedContext.getTableField().get(threadLocalMap)) != null) {
            for (Object referenceObj : referenceTable) {
                if (referenceObj == null) continue;
                boolean potentialLeak = false;
                Reference entryReference = (Reference)referenceObj;
                Object threadLocalObj = entryReference.get();
                if (webAppClassLoader.equals(threadLocalObj) || this.loadedByThisOrChild(threadLocalObj, webAppClassLoader)) {
                    potentialLeak = true;
                }
                Field valueField = entryReference.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                Object value = valueField.get(entryReference);
                if (webAppClassLoader.equals(value) || this.loadedByThisOrChild(value, webAppClassLoader)) {
                    potentialLeak = true;
                }
                if (!potentialLeak) continue;
                BruteForceThreadLocalCleanup.logErr(String.format("%s created a ThreadLocal with key of type [%s] (value [%s]) and a value of type [%s] (value [%s]). This will be cleaned up", this.toString(thread), this.getPrettyClassName(threadLocalObj), this.toString(threadLocalObj), this.getPrettyClassName(value), this.toString(value)));
                this.clearThreadLocal(threadLocalMap, threadLocalObj);
                if (threadLocalObj == null) {
                    this.clearThreadLocalValue(valueField, entryReference, value);
                }
                problems = true;
            }
        }
        return problems;
    }

    private void clearThreadLocal(Object threadLocalMap, Object threadLocalObj) {
        if (threadLocalObj != null) {
            try {
                Method removeMethod = threadLocalMap.getClass().getDeclaredMethod("remove", ThreadLocal.class);
                removeMethod.setAccessible(true);
                removeMethod.invoke(threadLocalMap, threadLocalObj);
            }
            catch (Exception e) {
                BruteForceThreadLocalCleanup.logErr(String.format("Unable to clear thread local %s : %s", this.getPrettyClassName(threadLocalObj), e.getMessage()));
            }
        }
    }

    private void clearThreadLocalValue(Field valueField, Reference<?> entryReference, Object value) {
        if (value != null) {
            try {
                valueField.set(entryReference, null);
            }
            catch (Exception e) {
                BruteForceThreadLocalCleanup.logErr(String.format("Unable to clear thread local value %s : %s", this.getPrettyClassName(value), e.getMessage()));
            }
        }
    }

    private String toString(Object value) {
        try {
            return value.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    private String getPrettyClassName(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> clazz = obj.getClass();
        String name = clazz.getCanonicalName();
        if (name == null) {
            name = clazz.getName();
        }
        return name;
    }

    private boolean loadedByThisOrChild(Object o, ClassLoader webAppClassLoader) {
        if (o == null) {
            return false;
        }
        Class<?> clazz = o instanceof Class ? (Class<?>)o : o.getClass();
        for (ClassLoader cl = clazz.getClassLoader(); cl != null; cl = cl.getParent()) {
            if (cl != webAppClassLoader) continue;
            return true;
        }
        return false;
    }

    private Thread[] getThreads() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        while (tg.getParent() != null) {
            tg = tg.getParent();
        }
        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);
        while (threadCountActual == threadCountGuess) {
            threads = new Thread[threadCountGuess *= 2];
            threadCountActual = tg.enumerate(threads);
        }
        return threads;
    }

    private static void logErr(String msg) {
        if (BruteForceThreadLocalCleanup.isDevMode()) {
            log.error(msg);
        } else {
            log.debug(msg);
        }
    }

    public static boolean isDevMode() {
        return Boolean.getBoolean("jira.dev.mode") || Boolean.getBoolean("atlassian.dev.mode");
    }

    private class ThreadLocalMapReflectedContext {
        private Field threadLocalsField;
        private Field inheritableThreadLocalsField;
        private Field tableField;

        private ThreadLocalMapReflectedContext() {
        }

        public Field getThreadLocalsField() {
            return this.threadLocalsField;
        }

        public Field getInheritableThreadLocalsField() {
            return this.inheritableThreadLocalsField;
        }

        public Field getTableField() {
            return this.tableField;
        }

        public ThreadLocalMapReflectedContext invoke() throws NoSuchFieldException, ClassNotFoundException {
            this.threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            this.threadLocalsField.setAccessible(true);
            this.inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            this.inheritableThreadLocalsField.setAccessible(true);
            Class<?> tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            this.tableField = tlmClass.getDeclaredField("table");
            this.tableField.setAccessible(true);
            return this;
        }
    }
}

