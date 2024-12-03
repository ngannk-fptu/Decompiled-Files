/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.extender.internal.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;

public abstract class PrivilegedUtils {
    private static final GetTCCLAction getTCCLAction = new GetTCCLAction();

    public static ClassLoader getTCCL() {
        return getTCCLAction.getTCCL();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T executeWithCustomTCCL(final ClassLoader customClassLoader, UnprivilegedExecution<T> execution) {
        T t;
        final Thread currentThread = Thread.currentThread();
        ClassLoader oldTCCL = getTCCLAction.getTCCL();
        try {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    currentThread.setContextClassLoader(customClassLoader);
                    return null;
                }
            });
            t = execution.run();
        }
        catch (Throwable throwable) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(currentThread, oldTCCL){
                final /* synthetic */ Thread val$currentThread;
                final /* synthetic */ ClassLoader val$oldTCCL;
                {
                    this.val$currentThread = thread;
                    this.val$oldTCCL = classLoader;
                }

                @Override
                public Object run() {
                    this.val$currentThread.setContextClassLoader(this.val$oldTCCL);
                    return null;
                }
            });
            throw throwable;
        }
        AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
        return t;
    }

    public static <T> T executeWithCustomTCCL(final ClassLoader customClassLoader, UnprivilegedThrowableExecution<T> execution) throws Throwable {
        T t;
        final Thread currentThread = Thread.currentThread();
        ClassLoader oldTCCL = getTCCLAction.getTCCL();
        try {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    currentThread.setContextClassLoader(customClassLoader);
                    return null;
                }
            });
            t = execution.run();
        }
        catch (PrivilegedActionException pae) {
            try {
                throw pae.getCause();
            }
            catch (Throwable throwable) {
                AccessController.doPrivileged(new PrivilegedAction<Object>(currentThread, oldTCCL){
                    final /* synthetic */ Thread val$currentThread;
                    final /* synthetic */ ClassLoader val$oldTCCL;
                    {
                        this.val$currentThread = thread;
                        this.val$oldTCCL = classLoader;
                    }

                    @Override
                    public Object run() {
                        this.val$currentThread.setContextClassLoader(this.val$oldTCCL);
                        return null;
                    }
                });
                throw throwable;
            }
        }
        AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
        return t;
    }

    public static interface UnprivilegedExecution<T> {
        public T run();
    }

    public static interface UnprivilegedThrowableExecution<T> {
        public T run() throws Throwable;
    }

    private static class GetTCCLAction
    implements PrivilegedAction<ClassLoader> {
        private GetTCCLAction() {
        }

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }

        public ClassLoader getTCCL() {
            return AccessController.doPrivileged(this);
        }
    }
}

