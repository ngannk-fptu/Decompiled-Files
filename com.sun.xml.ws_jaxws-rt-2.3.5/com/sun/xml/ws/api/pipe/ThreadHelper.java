/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;

final class ThreadHelper {
    private static final String SAFE_THREAD_NAME = "sun.misc.ManagedLocalsThread";
    private static final ThreadFactory threadFactory = AccessController.doPrivileged(new PrivilegedAction<ThreadFactory>(){

        @Override
        public ThreadFactory run() {
            try {
                Class<Thread> cls = Thread.class;
                Constructor<Thread> ctr = cls.getConstructor(ThreadGroup.class, Runnable.class, String.class, Long.TYPE, Boolean.TYPE);
                return new JDK9ThreadFactory(ctr);
            }
            catch (NoSuchMethodException cls) {
                try {
                    Class<?> cls2 = Class.forName(ThreadHelper.SAFE_THREAD_NAME);
                    Constructor<?> ctr = cls2.getConstructor(Runnable.class);
                    return new SunMiscThreadFactory(ctr);
                }
                catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
                    return new LegacyThreadFactory();
                }
            }
        }
    });

    private ThreadHelper() {
    }

    static Thread createNewThread(Runnable r) {
        return threadFactory.newThread(r);
    }

    private static class LegacyThreadFactory
    implements ThreadFactory {
        private LegacyThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    private static class SunMiscThreadFactory
    implements ThreadFactory {
        final Constructor<?> ctr;

        SunMiscThreadFactory(Constructor<?> ctr) {
            this.ctr = ctr;
        }

        @Override
        public Thread newThread(final Runnable r) {
            return AccessController.doPrivileged(new PrivilegedAction<Thread>(){

                @Override
                public Thread run() {
                    try {
                        return (Thread)ctr.newInstance(r);
                    }
                    catch (Exception e) {
                        return new Thread(r);
                    }
                }
            });
        }
    }

    private static class JDK9ThreadFactory
    implements ThreadFactory {
        final Constructor<Thread> ctr;

        JDK9ThreadFactory(Constructor<Thread> ctr) {
            this.ctr = ctr;
        }

        @Override
        public Thread newThread(Runnable r) {
            try {
                return this.ctr.newInstance(null, r, "toBeReplaced", 0, false);
            }
            catch (ReflectiveOperationException x) {
                InternalError ie = new InternalError(x.getMessage());
                ie.initCause(ie);
                throw ie;
            }
        }
    }
}

