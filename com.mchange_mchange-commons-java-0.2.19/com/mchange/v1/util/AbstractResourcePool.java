/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.BrokenObjectException;
import com.mchange.v1.util.RunnableQueue;
import com.mchange.v1.util.SimpleRunnableQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractResourcePool {
    private static final boolean TRACE = true;
    private static final boolean DEBUG = true;
    private static RunnableQueue sharedQueue = new SimpleRunnableQueue();
    Set managed = new HashSet();
    List unused = new LinkedList();
    int start;
    int max;
    int inc;
    int num_acq_attempts = Integer.MAX_VALUE;
    int acq_attempt_delay = 50;
    RunnableQueue rq;
    boolean initted = false;
    boolean broken = false;

    protected AbstractResourcePool(int n, int n2, int n3) {
        this(n, n2, n3, sharedQueue);
    }

    protected AbstractResourcePool(int n, int n2, int n3, RunnableQueue runnableQueue) {
        this.start = n;
        this.max = n2;
        this.inc = n3;
        this.rq = runnableQueue;
    }

    protected abstract Object acquireResource() throws Exception;

    protected abstract void refurbishResource(Object var1) throws BrokenObjectException;

    protected abstract void destroyResource(Object var1) throws Exception;

    protected synchronized void init() throws Exception {
        for (int i = 0; i < this.start; ++i) {
            this.assimilateResource();
        }
        this.initted = true;
    }

    protected Object checkoutResource() throws BrokenObjectException, InterruptedException, Exception {
        return this.checkoutResource(0L);
    }

    protected synchronized Object checkoutResource(long l) throws BrokenObjectException, InterruptedException, TimeoutException, Exception {
        if (!this.initted) {
            this.init();
        }
        this.ensureNotBroken();
        int n = this.unused.size();
        if (n == 0) {
            int n2 = this.managed.size();
            if (n2 < this.max) {
                this.postAcquireMore();
            }
            this.awaitAvailable(l);
        }
        Object e = this.unused.get(0);
        this.unused.remove(0);
        try {
            this.refurbishResource(e);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            this.removeResource(e);
            return this.checkoutResource(l);
        }
        this.trace();
        return e;
    }

    protected synchronized void checkinResource(Object object) throws BrokenObjectException {
        if (!this.managed.contains(object)) {
            throw new IllegalArgumentException("ResourcePool: Tried to check-in a foreign resource!");
        }
        this.unused.add(object);
        this.notifyAll();
        this.trace();
    }

    protected synchronized void markBad(Object object) throws Exception {
        this.removeResource(object);
    }

    protected synchronized void close() throws Exception {
        this.broken = true;
        Iterator iterator = this.managed.iterator();
        while (iterator.hasNext()) {
            try {
                this.removeResource(iterator.next());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void postAcquireMore() throws InterruptedException {
        this.rq.postRunnable(new AcquireTask());
    }

    private void awaitAvailable(long l) throws InterruptedException, TimeoutException {
        int n;
        while ((n = this.unused.size()) == 0) {
            this.wait(l);
        }
        if (n == 0) {
            throw new TimeoutException();
        }
    }

    private void acquireMore() throws Exception {
        int n = this.managed.size();
        for (int i = 0; i < Math.min(this.inc, this.max - n); ++i) {
            this.assimilateResource();
        }
    }

    private void assimilateResource() throws Exception {
        Object object = this.acquireResource();
        this.managed.add(object);
        this.unused.add(object);
        this.notifyAll();
        this.trace();
    }

    private void removeResource(Object object) throws Exception {
        this.managed.remove(object);
        this.unused.remove(object);
        this.destroyResource(object);
        this.trace();
    }

    private void ensureNotBroken() throws BrokenObjectException {
        if (this.broken) {
            throw new BrokenObjectException(this);
        }
    }

    private synchronized void unexpectedBreak() {
        this.broken = true;
        Iterator iterator = this.unused.iterator();
        while (iterator.hasNext()) {
            try {
                this.removeResource(iterator.next());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void trace() {
        System.err.println(this + "  [managed: " + this.managed.size() + ", unused: " + this.unused.size() + ']');
    }

    protected class TimeoutException
    extends Exception {
        protected TimeoutException() {
        }
    }

    class AcquireTask
    implements Runnable {
        boolean success = false;

        AcquireTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            for (int i = 0; !this.success && i < AbstractResourcePool.this.num_acq_attempts; ++i) {
                try {
                    if (i > 0) {
                        Thread.sleep(AbstractResourcePool.this.acq_attempt_delay);
                    }
                    AbstractResourcePool abstractResourcePool = AbstractResourcePool.this;
                    synchronized (abstractResourcePool) {
                        AbstractResourcePool.this.acquireMore();
                    }
                    this.success = true;
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (!this.success) {
                AbstractResourcePool.this.unexpectedBreak();
            }
        }
    }
}

