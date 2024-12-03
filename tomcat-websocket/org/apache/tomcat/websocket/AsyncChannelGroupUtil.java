/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.ThreadPoolExecutor
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

public class AsyncChannelGroupUtil {
    private static final StringManager sm = StringManager.getManager(AsyncChannelGroupUtil.class);
    private static AsynchronousChannelGroup group = null;
    private static int usageCount = 0;
    private static final Object lock = new Object();

    private AsyncChannelGroupUtil() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static AsynchronousChannelGroup register() {
        Object object = lock;
        synchronized (object) {
            if (usageCount == 0) {
                group = AsyncChannelGroupUtil.createAsynchronousChannelGroup();
            }
            ++usageCount;
            return group;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void unregister() {
        Object object = lock;
        synchronized (object) {
            if (--usageCount == 0) {
                group.shutdown();
                group = null;
            }
        }
    }

    private static AsynchronousChannelGroup createAsynchronousChannelGroup() {
        Thread currentThread = Thread.currentThread();
        ClassLoader original = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(AsyncIOThreadFactory.class.getClassLoader());
            int initialSize = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), (ThreadFactory)new AsyncIOThreadFactory());
            try {
                AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withCachedThreadPool((ExecutorService)executorService, initialSize);
                return asynchronousChannelGroup;
            }
            catch (IOException e) {
                throw new IllegalStateException(sm.getString("asyncChannelGroup.createFail"));
            }
        }
        finally {
            currentThread.setContextClassLoader(original);
        }
    }

    private static class AsyncIOThreadFactory
    implements ThreadFactory {
        private AsyncIOThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            return AccessController.doPrivileged(new NewThreadPrivilegedAction(r));
        }

        static {
            NewThreadPrivilegedAction.load();
        }

        private static class NewThreadPrivilegedAction
        implements PrivilegedAction<Thread> {
            private static AtomicInteger count = new AtomicInteger(0);
            private final Runnable r;

            NewThreadPrivilegedAction(Runnable r) {
                this.r = r;
            }

            @Override
            public Thread run() {
                Thread t = new Thread(this.r);
                t.setName("WebSocketClient-AsyncIO-" + count.incrementAndGet());
                t.setContextClassLoader(this.getClass().getClassLoader());
                t.setDaemon(true);
                return t;
            }

            private static void load() {
            }
        }
    }
}

