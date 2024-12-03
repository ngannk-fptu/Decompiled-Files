/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.executor;

import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.executor.AbstractExecutorThreadFactory;
import com.hazelcast.util.executor.HazelcastManagedThread;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolExecutorThreadFactory
extends AbstractExecutorThreadFactory {
    private final String threadNamePrefix;
    private final AtomicInteger idGen = new AtomicInteger(0);
    private final Queue<Integer> idQ = new LinkedBlockingQueue<Integer>(1000);

    public PoolExecutorThreadFactory(String threadNamePrefix, ClassLoader classLoader) {
        super(classLoader);
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    protected Thread createThread(Runnable r) {
        Integer id = this.idQ.poll();
        if (id == null) {
            id = this.idGen.incrementAndGet();
        }
        String name = this.threadNamePrefix + id;
        return this.createThread(r, name, id);
    }

    protected ManagedThread createThread(Runnable r, String name, int id) {
        return new ManagedThread(r, name, id);
    }

    protected class ManagedThread
    extends HazelcastManagedThread {
        private final int id;

        public ManagedThread(Runnable target, String name, int id) {
            super(target, name);
            this.id = id;
        }

        @Override
        protected void afterRun() {
            try {
                PoolExecutorThreadFactory.this.idQ.offer(this.id);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
    }
}

