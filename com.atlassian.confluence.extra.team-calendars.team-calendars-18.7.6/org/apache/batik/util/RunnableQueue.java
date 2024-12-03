/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.batik.util.DoublyLinkedList;
import org.apache.batik.util.HaltingThread;

public class RunnableQueue
implements Runnable {
    public static final RunnableQueueState RUNNING = new RunnableQueueState("Running");
    public static final RunnableQueueState SUSPENDING = new RunnableQueueState("Suspending");
    public static final RunnableQueueState SUSPENDED = new RunnableQueueState("Suspended");
    protected volatile RunnableQueueState state;
    protected final Object stateLock = new Object();
    protected boolean wasResumed;
    private final DoublyLinkedList list = new DoublyLinkedList();
    protected int preemptCount;
    protected RunHandler runHandler;
    protected volatile HaltingThread runnableQueueThread;
    private IdleRunnable idleRunnable;
    private long idleRunnableWaitTime;
    private static volatile int threadCount;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RunnableQueue createRunnableQueue() {
        RunnableQueue result;
        RunnableQueue runnableQueue = result = new RunnableQueue();
        synchronized (runnableQueue) {
            HaltingThread ht = new HaltingThread(result, "RunnableQueue-" + threadCount++);
            ht.setDaemon(true);
            ht.start();
            while (result.getThread() == null) {
                try {
                    result.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public void run() {
        block53: {
            var1_1 = this;
            synchronized (var1_1) {
                this.runnableQueueThread = (HaltingThread)Thread.currentThread();
                this.notify();
            }
            block37: while (true) {
                while (!HaltingThread.hasBeenHalted()) {
                    callSuspended = false;
                    callResumed = false;
                    var5_8 = this.stateLock;
                    synchronized (var5_8) {
                        if (this.state != RunnableQueue.RUNNING) {
                            this.state = RunnableQueue.SUSPENDED;
                            callSuspended = true;
                        }
                    }
                    if (callSuspended) {
                        this.executionSuspended();
                    }
                    var5_8 = this.stateLock;
                    synchronized (var5_8) {
                        while (this.state != RunnableQueue.RUNNING) {
                            this.state = RunnableQueue.SUSPENDED;
                            this.stateLock.notifyAll();
                            try {
                                this.stateLock.wait();
                            }
                            catch (InterruptedException var6_14) {}
                        }
                        if (this.wasResumed) {
                            this.wasResumed = false;
                            callResumed = true;
                        }
                    }
                    if (callResumed) {
                        this.executionResumed();
                    }
                    var5_8 = this.list;
                    synchronized (var5_8) {
                        if (this.state == RunnableQueue.SUSPENDING) {
                            continue;
                        }
                        l = (Link)this.list.pop();
                        if (this.preemptCount != 0) {
                            --this.preemptCount;
                        }
                        if (l != null) ** GOTO lbl67
                        if (this.idleRunnable != null && (this.idleRunnableWaitTime = this.idleRunnable.getWaitTime()) < System.currentTimeMillis()) {
                            rable = this.idleRunnable;
                        } else {
                            try {
                                if (this.idleRunnable != null && this.idleRunnableWaitTime != 0x7FFFFFFFFFFFFFFFL) {
                                    t = this.idleRunnableWaitTime - System.currentTimeMillis();
                                    if (t > 0L) {
                                        this.list.wait(t);
                                    }
                                } else {
                                    this.list.wait();
                                }
                            }
                            catch (InterruptedException var6_16) {
                                // empty catch block
                            }
                            continue;
lbl67:
                            // 1 sources

                            rable = Link.access$100(l);
                        }
                    }
                    try {
                        this.runnableStart(rable);
                        rable.run();
                    }
                    catch (ThreadDeath td) {
                        throw td;
                    }
                    catch (Throwable t) {
                        t.printStackTrace();
                    }
                    if (l != null) {
                        l.unlock();
                    }
                    try {
                        this.runnableInvoked(rable);
                        continue block37;
                    }
                    catch (ThreadDeath td) {
                        throw td;
                    }
                    catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                break block53;
                {
                    continue block37;
                    break;
                }
                break;
            }
            finally {
                while (true) {
                    var3_6 = this.list;
                    synchronized (var3_6) {
                        l = (Link)this.list.pop();
                    }
                    if (l == null) break;
                    l.unlock();
                }
                var3_6 = this;
                synchronized (var3_6) {
                    this.runnableQueueThread = null;
                }
            }
        }
    }

    public HaltingThread getThread() {
        return this.runnableQueueThread;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invokeLater(Runnable r) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        DoublyLinkedList doublyLinkedList = this.list;
        synchronized (doublyLinkedList) {
            this.list.push(new Link(r));
            this.list.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invokeAndWait(Runnable r) throws InterruptedException {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        if (this.runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException("Cannot be called from the RunnableQueue thread");
        }
        LockableLink l = new LockableLink(r);
        DoublyLinkedList doublyLinkedList = this.list;
        synchronized (doublyLinkedList) {
            this.list.push(l);
            this.list.notify();
        }
        l.lock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void preemptLater(Runnable r) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        DoublyLinkedList doublyLinkedList = this.list;
        synchronized (doublyLinkedList) {
            this.list.add(this.preemptCount, new Link(r));
            ++this.preemptCount;
            this.list.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void preemptAndWait(Runnable r) throws InterruptedException {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        if (this.runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException("Cannot be called from the RunnableQueue thread");
        }
        LockableLink l = new LockableLink(r);
        DoublyLinkedList doublyLinkedList = this.list;
        synchronized (doublyLinkedList) {
            this.list.add(this.preemptCount, l);
            ++this.preemptCount;
            this.list.notify();
        }
        l.lock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RunnableQueueState getQueueState() {
        Object object = this.stateLock;
        synchronized (object) {
            return this.state;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void suspendExecution(boolean waitTillSuspended) {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        Object object = this.stateLock;
        synchronized (object) {
            this.wasResumed = false;
            if (this.state == SUSPENDED) {
                this.stateLock.notifyAll();
                return;
            }
            if (this.state == RUNNING) {
                this.state = SUSPENDING;
                DoublyLinkedList doublyLinkedList = this.list;
                synchronized (doublyLinkedList) {
                    this.list.notify();
                }
            }
            if (waitTillSuspended) {
                while (this.state == SUSPENDING) {
                    try {
                        this.stateLock.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resumeExecution() {
        if (this.runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started or has exited");
        }
        Object object = this.stateLock;
        synchronized (object) {
            this.wasResumed = true;
            if (this.state != RUNNING) {
                this.state = RUNNING;
                this.stateLock.notifyAll();
            }
        }
    }

    public Object getIteratorLock() {
        return this.list;
    }

    public Iterator iterator() {
        return new Iterator(){
            Link head;
            Link link;
            {
                this.head = (Link)RunnableQueue.this.list.getHead();
            }

            @Override
            public boolean hasNext() {
                if (this.head == null) {
                    return false;
                }
                if (this.link == null) {
                    return true;
                }
                return this.link != this.head;
            }

            public Object next() {
                if (this.head == null || this.head == this.link) {
                    throw new NoSuchElementException();
                }
                if (this.link == null) {
                    this.link = (Link)this.head.getNext();
                    return this.head.runnable;
                }
                Runnable result = this.link.runnable;
                this.link = (Link)this.link.getNext();
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public synchronized void setRunHandler(RunHandler rh) {
        this.runHandler = rh;
    }

    public synchronized RunHandler getRunHandler() {
        return this.runHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setIdleRunnable(IdleRunnable r) {
        DoublyLinkedList doublyLinkedList = this.list;
        synchronized (doublyLinkedList) {
            this.idleRunnable = r;
            this.idleRunnableWaitTime = 0L;
            this.list.notify();
        }
    }

    protected synchronized void executionSuspended() {
        if (this.runHandler != null) {
            this.runHandler.executionSuspended(this);
        }
    }

    protected synchronized void executionResumed() {
        if (this.runHandler != null) {
            this.runHandler.executionResumed(this);
        }
    }

    protected synchronized void runnableStart(Runnable rable) {
        if (this.runHandler != null) {
            this.runHandler.runnableStart(this, rable);
        }
    }

    protected synchronized void runnableInvoked(Runnable rable) {
        if (this.runHandler != null) {
            this.runHandler.runnableInvoked(this, rable);
        }
    }

    protected static class LockableLink
    extends Link {
        private volatile boolean locked;

        public LockableLink(Runnable r) {
            super(r);
        }

        public boolean isLocked() {
            return this.locked;
        }

        public synchronized void lock() throws InterruptedException {
            this.locked = true;
            this.notify();
            this.wait();
        }

        @Override
        public synchronized void unlock() {
            while (!this.locked) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
            this.locked = false;
            this.notify();
        }
    }

    protected static class Link
    extends DoublyLinkedList.Node {
        private final Runnable runnable;

        public Link(Runnable r) {
            this.runnable = r;
        }

        public void unlock() {
        }
    }

    public static class RunHandlerAdapter
    implements RunHandler {
        @Override
        public void runnableStart(RunnableQueue rq, Runnable r) {
        }

        @Override
        public void runnableInvoked(RunnableQueue rq, Runnable r) {
        }

        @Override
        public void executionSuspended(RunnableQueue rq) {
        }

        @Override
        public void executionResumed(RunnableQueue rq) {
        }
    }

    public static interface RunHandler {
        public void runnableStart(RunnableQueue var1, Runnable var2);

        public void runnableInvoked(RunnableQueue var1, Runnable var2);

        public void executionSuspended(RunnableQueue var1);

        public void executionResumed(RunnableQueue var1);
    }

    public static interface IdleRunnable
    extends Runnable {
        public long getWaitTime();
    }

    public static final class RunnableQueueState {
        private final String value;

        private RunnableQueueState(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return "[RunnableQueueState: " + this.value + ']';
        }
    }
}

