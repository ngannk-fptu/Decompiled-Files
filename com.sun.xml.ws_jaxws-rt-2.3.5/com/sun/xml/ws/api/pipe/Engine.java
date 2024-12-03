/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.ThreadHelper;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Engine {
    private volatile Executor threadPool;
    public final String id;
    private final Container container;

    String getId() {
        return this.id;
    }

    Container getContainer() {
        return this.container;
    }

    Executor getExecutor() {
        return this.threadPool;
    }

    public Engine(String id, Executor threadPool) {
        this(id, ContainerResolver.getDefault().getContainer(), threadPool);
    }

    public Engine(String id, Container container, Executor threadPool) {
        this(id, container);
        this.threadPool = threadPool != null ? this.wrap(threadPool) : null;
    }

    public Engine(String id) {
        this(id, ContainerResolver.getDefault().getContainer());
    }

    public Engine(String id, Container container) {
        this.id = id;
        this.container = container;
    }

    public void setExecutor(Executor threadPool) {
        this.threadPool = threadPool != null ? this.wrap(threadPool) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addRunnable(Fiber fiber) {
        if (this.threadPool == null) {
            Engine engine = this;
            synchronized (engine) {
                this.threadPool = this.wrap(Executors.newCachedThreadPool(new DaemonThreadFactory()));
            }
        }
        this.threadPool.execute(fiber);
    }

    private Executor wrap(Executor ex) {
        return ContainerResolver.getDefault().wrapExecutor(this.container, ex);
    }

    public Fiber createFiber() {
        return new Fiber(this);
    }

    private static class DaemonThreadFactory
    implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix = "jaxws-engine-" + poolNumber.getAndIncrement() + "-thread-";

        DaemonThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = ThreadHelper.createNewThread(r);
            t.setName(this.namePrefix + this.threadNumber.getAndIncrement());
            if (!t.isDaemon()) {
                t.setDaemon(true);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}

