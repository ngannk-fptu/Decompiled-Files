/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.util.ResourceClosedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadPerTaskAsynchronousRunner
implements AsynchronousRunner {
    static final int PRESUME_DEADLOCKED_MULTIPLE = 3;
    static final MLogger logger = MLog.getLogger(ThreadPerTaskAsynchronousRunner.class);
    final int max_task_threads;
    final long interrupt_task_delay;
    LinkedList queue = new LinkedList();
    ArrayList running = new ArrayList();
    ArrayList deadlockSnapshot = null;
    boolean still_open = true;
    Thread dispatchThread = new DispatchThread();
    Timer interruptAndDeadlockTimer;

    public ThreadPerTaskAsynchronousRunner(int n) {
        this(n, 0L);
    }

    public ThreadPerTaskAsynchronousRunner(int n, long l) {
        this.max_task_threads = n;
        this.interrupt_task_delay = l;
        if (this.hasIdTimer()) {
            this.interruptAndDeadlockTimer = new Timer(true);
            TimerTask timerTask = new TimerTask(){

                @Override
                public void run() {
                    ThreadPerTaskAsynchronousRunner.this.checkForDeadlock();
                }
            };
            long l2 = l * 3L;
            this.interruptAndDeadlockTimer.schedule(timerTask, l2, l2);
        }
        this.dispatchThread.start();
    }

    private boolean hasIdTimer() {
        return this.interrupt_task_delay > 0L;
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        if (!this.still_open) {
            throw new ResourceClosedException("Attempted to use a ThreadPerTaskAsynchronousRunner in a closed or broken state.");
        }
        this.queue.add(runnable);
        this.notifyAll();
    }

    @Override
    public void close() {
        this.close(true);
    }

    @Override
    public synchronized void close(boolean bl) {
        if (this.still_open) {
            this.still_open = false;
            if (bl) {
                this.queue.clear();
                Iterator iterator = this.running.iterator();
                while (iterator.hasNext()) {
                    ((Thread)iterator.next()).interrupt();
                }
                this.closeThreadResources();
            }
        }
    }

    public synchronized int getRunningCount() {
        return this.running.size();
    }

    public synchronized Collection getRunningTasks() {
        return (Collection)this.running.clone();
    }

    public synchronized int getWaitingCount() {
        return this.queue.size();
    }

    public synchronized Collection getWaitingTasks() {
        return (Collection)this.queue.clone();
    }

    public synchronized boolean isClosed() {
        return !this.still_open;
    }

    public synchronized boolean isDoneAndGone() {
        return !this.dispatchThread.isAlive() && this.running.isEmpty() && this.interruptAndDeadlockTimer == null;
    }

    private synchronized void acknowledgeComplete(TaskThread taskThread) {
        if (!taskThread.isCompleted()) {
            this.running.remove(taskThread);
            taskThread.markCompleted();
            this.notifyAll();
            if (!this.still_open && this.queue.isEmpty() && this.running.isEmpty()) {
                this.closeThreadResources();
            }
        }
    }

    private synchronized void checkForDeadlock() {
        if (this.deadlockSnapshot == null) {
            if (this.running.size() == this.max_task_threads) {
                this.deadlockSnapshot = (ArrayList)this.running.clone();
            }
        } else if (this.running.size() < this.max_task_threads) {
            this.deadlockSnapshot = null;
        } else if (this.deadlockSnapshot.equals(this.running)) {
            int n;
            if (logger.isLoggable(MLevel.WARNING)) {
                StringBuffer stringBuffer = new StringBuffer(1024);
                stringBuffer.append("APPARENT DEADLOCK! (");
                stringBuffer.append(this);
                stringBuffer.append(") Deadlocked threads (unresponsive to interrupt()) are being set aside as hopeless and up to ");
                stringBuffer.append(this.max_task_threads);
                stringBuffer.append(" may now be spawned for new tasks. If tasks continue to deadlock, you may run out of memory. Deadlocked task list: ");
                int n2 = this.deadlockSnapshot.size();
                for (n = 0; n < n2; ++n) {
                    if (n != 0) {
                        stringBuffer.append(", ");
                    }
                    stringBuffer.append(((TaskThread)this.deadlockSnapshot.get(n)).getTask());
                }
                logger.log(MLevel.WARNING, stringBuffer.toString());
            }
            n = this.deadlockSnapshot.size();
            for (int i = 0; i < n; ++i) {
                this.acknowledgeComplete((TaskThread)this.deadlockSnapshot.get(i));
            }
            this.deadlockSnapshot = null;
        } else {
            this.deadlockSnapshot = (ArrayList)this.running.clone();
        }
    }

    private void closeThreadResources() {
        if (this.interruptAndDeadlockTimer != null) {
            this.interruptAndDeadlockTimer.cancel();
            this.interruptAndDeadlockTimer = null;
        }
        this.dispatchThread.interrupt();
    }

    class TaskThread
    extends Thread {
        Runnable r;
        boolean completed;

        TaskThread(Runnable runnable) {
            super("Task-Thread-for-" + ThreadPerTaskAsynchronousRunner.this);
            this.completed = false;
            this.r = runnable;
        }

        Runnable getTask() {
            return this.r;
        }

        synchronized void markCompleted() {
            this.completed = true;
        }

        synchronized boolean isCompleted() {
            return this.completed;
        }

        @Override
        public void run() {
            try {
                if (ThreadPerTaskAsynchronousRunner.this.hasIdTimer()) {
                    TimerTask timerTask = new TimerTask(){

                        @Override
                        public void run() {
                            TaskThread.this.interrupt();
                        }
                    };
                    ThreadPerTaskAsynchronousRunner.this.interruptAndDeadlockTimer.schedule(timerTask, ThreadPerTaskAsynchronousRunner.this.interrupt_task_delay);
                }
                this.r.run();
            }
            finally {
                ThreadPerTaskAsynchronousRunner.this.acknowledgeComplete(this);
            }
        }
    }

    class DispatchThread
    extends Thread {
        DispatchThread() {
            super("Dispatch-Thread-for-" + ThreadPerTaskAsynchronousRunner.this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ThreadPerTaskAsynchronousRunner threadPerTaskAsynchronousRunner = ThreadPerTaskAsynchronousRunner.this;
            synchronized (threadPerTaskAsynchronousRunner) {
                try {
                    while (true) {
                        if (ThreadPerTaskAsynchronousRunner.this.queue.isEmpty() || ThreadPerTaskAsynchronousRunner.this.running.size() == ThreadPerTaskAsynchronousRunner.this.max_task_threads) {
                            ThreadPerTaskAsynchronousRunner.this.wait();
                            continue;
                        }
                        Runnable runnable = (Runnable)ThreadPerTaskAsynchronousRunner.this.queue.remove(0);
                        TaskThread taskThread = new TaskThread(runnable);
                        taskThread.start();
                        ThreadPerTaskAsynchronousRunner.this.running.add(taskThread);
                    }
                }
                catch (InterruptedException interruptedException) {
                    if (ThreadPerTaskAsynchronousRunner.this.still_open) {
                        if (logger.isLoggable(MLevel.WARNING)) {
                            logger.log(MLevel.WARNING, this.getName() + " unexpectedly interrupted! Shutting down!");
                        }
                        ThreadPerTaskAsynchronousRunner.this.close(false);
                    }
                }
            }
        }
    }
}

