/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.util.internal;

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
        block7: {
            ClassLoader oldTCCL;
            Thread currentThread;
            block6: {
                currentThread = Thread.currentThread();
                oldTCCL = getTCCLAction.getTCCL();
                boolean hasSecurity = System.getSecurityManager() != null;
                try {
                    if (hasSecurity) {
                        AccessController.doPrivileged(new PrivilegedAction<Object>(){

                            @Override
                            public Object run() {
                                currentThread.setContextClassLoader(customClassLoader);
                                return null;
                            }
                        });
                    } else {
                        currentThread.setContextClassLoader(customClassLoader);
                    }
                    t = execution.run();
                    if (!hasSecurity) break block6;
                }
                catch (Throwable throwable) {
                    if (hasSecurity) {
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
                    } else {
                        currentThread.setContextClassLoader(oldTCCL);
                    }
                    throw throwable;
                }
                AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
                break block7;
            }
            currentThread.setContextClassLoader(oldTCCL);
        }
        return t;
    }

    public static <T> T executeWithCustomTCCL(final ClassLoader customClassLoader, UnprivilegedThrowableExecution<T> execution) throws Throwable {
        T t;
        block9: {
            ClassLoader oldTCCL;
            Thread currentThread;
            block8: {
                currentThread = Thread.currentThread();
                oldTCCL = getTCCLAction.getTCCL();
                boolean hasSecurity = System.getSecurityManager() != null;
                try {
                    if (hasSecurity) {
                        AccessController.doPrivileged(new PrivilegedAction<Object>(){

                            @Override
                            public Object run() {
                                currentThread.setContextClassLoader(customClassLoader);
                                return null;
                            }
                        });
                    } else {
                        currentThread.setContextClassLoader(customClassLoader);
                    }
                    t = execution.run();
                    if (!hasSecurity) break block8;
                }
                catch (PrivilegedActionException pae) {
                    try {
                        throw pae.getCause();
                    }
                    catch (Throwable throwable) {
                        if (hasSecurity) {
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
                        } else {
                            currentThread.setContextClassLoader(oldTCCL);
                        }
                        throw throwable;
                    }
                }
                AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
                break block9;
            }
            currentThread.setContextClassLoader(oldTCCL);
        }
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

