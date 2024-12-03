/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.forker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Forker<T> {
    final Executor executor;
    final Map<T, Job> waiting = new HashMap<T, Job>();
    final Set<Job> executing = new HashSet<Job>();
    final AtomicBoolean canceled = new AtomicBoolean();
    private int count;

    public Forker(Executor executor) {
        this.executor = executor;
    }

    public Forker() {
        this.executor = Executors.newFixedThreadPool(8);
    }

    public synchronized void doWhen(Collection<? extends T> dependencies, T target, Runnable runnable) {
        if (this.waiting.containsKey(target)) {
            throw new IllegalArgumentException("You can only add a target once to the forker");
        }
        System.err.println("doWhen " + dependencies + " " + target);
        Job job = new Job();
        job.dependencies = new HashSet<T>(dependencies);
        job.target = target;
        job.runnable = runnable;
        this.waiting.put(target, job);
    }

    public void start(long ms) throws InterruptedException {
        this.check();
        this.count = this.waiting.size();
        System.err.println("Count " + this.count);
        this.schedule();
        if (ms >= 0L) {
            this.sync(ms);
        }
    }

    private void check() {
        HashSet dependencies = new HashSet();
        for (Job job : this.waiting.values()) {
            dependencies.addAll(job.dependencies);
        }
        dependencies.removeAll(this.waiting.keySet());
        if (dependencies.size() > 0) {
            throw new IllegalArgumentException("There are dependencies in the jobs that are not present in the targets: " + dependencies);
        }
    }

    public synchronized void sync(long ms) throws InterruptedException {
        System.err.println("Waiting for sync");
        while (this.count > 0) {
            System.err.println("Waiting for sync " + this.count);
            this.wait(ms);
        }
        System.err.println("Exiting sync " + this.count);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void schedule() {
        if (this.canceled.get()) {
            return;
        }
        ArrayList<Job> torun = new ArrayList<Job>();
        Forker forker = this;
        synchronized (forker) {
            Iterator<Job> iterator = this.waiting.values().iterator();
            while (iterator.hasNext()) {
                Job job = iterator.next();
                if (!job.dependencies.isEmpty()) continue;
                torun.add(job);
                this.executing.add(job);
                iterator.remove();
            }
        }
        for (Runnable runnable : torun) {
            this.executor.execute(runnable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void done(Job done) {
        Forker forker = this;
        synchronized (forker) {
            System.err.println("count = " + this.count);
            this.executing.remove(done);
            --this.count;
            if (this.count == 0) {
                System.err.println("finished");
                this.notifyAll();
                return;
            }
            for (Job job : this.waiting.values()) {
                job.dependencies.remove(done.target);
            }
        }
        this.schedule();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel(long ms) throws InterruptedException {
        System.err.println("canceled " + this.count);
        if (!this.canceled.getAndSet(true)) {
            Forker forker = this;
            synchronized (forker) {
                for (Job job : this.executing) {
                    job.cancel();
                }
            }
        }
        this.sync(ms);
    }

    public int getCount() {
        return this.count;
    }

    class Job
    implements Runnable {
        T target;
        Set<T> dependencies;
        Runnable runnable;
        Throwable exception;
        volatile Thread t;
        volatile AtomicBoolean canceled = new AtomicBoolean(false);

        Job() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Thread.interrupted();
            try {
                Job job = this;
                synchronized (job) {
                    block22: {
                        if (!this.canceled.get()) break block22;
                        return;
                    }
                    this.t = Thread.currentThread();
                }
                this.runnable.run();
            }
            catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
            }
            finally {
                Job job = this;
                synchronized (job) {
                    this.t = null;
                }
                Thread.interrupted();
                Forker.this.done(this);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void cancel() {
            if (!this.canceled.getAndSet(true)) {
                Job job = this;
                synchronized (job) {
                    if (this.t != null) {
                        this.t.interrupt();
                    }
                }
            }
        }
    }
}

