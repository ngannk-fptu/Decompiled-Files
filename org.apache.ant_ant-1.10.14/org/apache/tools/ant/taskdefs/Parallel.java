/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.property.LocalProperties;

public class Parallel
extends Task
implements TaskContainer {
    private static final int NUMBER_TRIES = 100;
    private Vector<Task> nestedTasks = new Vector();
    private final Object semaphore = new Object();
    private int numThreads = 0;
    private int numThreadsPerProcessor = 0;
    private long timeout;
    private volatile boolean stillRunning;
    private boolean timedOut;
    private boolean failOnAny;
    private TaskList daemonTasks;
    private StringBuffer exceptionMessage;
    private int numExceptions = 0;
    private Throwable firstException;
    private Location firstLocation;
    private Integer firstExitStatus;

    public void addDaemons(TaskList daemonTasks) {
        if (this.daemonTasks != null) {
            throw new BuildException("Only one daemon group is supported");
        }
        this.daemonTasks = daemonTasks;
    }

    public void setPollInterval(int pollInterval) {
    }

    public void setFailOnAny(boolean failOnAny) {
        this.failOnAny = failOnAny;
    }

    @Override
    public void addTask(Task nestedTask) {
        this.nestedTasks.addElement(nestedTask);
    }

    public void setThreadsPerProcessor(int numThreadsPerProcessor) {
        this.numThreadsPerProcessor = numThreadsPerProcessor;
    }

    public void setThreadCount(int numThreads) {
        this.numThreads = numThreads;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void execute() throws BuildException {
        this.updateThreadCounts();
        if (this.numThreads == 0) {
            this.numThreads = this.nestedTasks.size();
        }
        this.spinThreads();
    }

    private void updateThreadCounts() {
        if (this.numThreadsPerProcessor != 0) {
            this.numThreads = Runtime.getRuntime().availableProcessors() * this.numThreadsPerProcessor;
        }
    }

    private void processExceptions(TaskRunnable[] runnables) {
        if (runnables == null) {
            return;
        }
        for (TaskRunnable runnable : runnables) {
            Throwable t = runnable.getException();
            if (t == null) continue;
            ++this.numExceptions;
            if (this.firstException == null) {
                this.firstException = t;
            }
            if (t instanceof BuildException && this.firstLocation == Location.UNKNOWN_LOCATION) {
                this.firstLocation = ((BuildException)t).getLocation();
            }
            if (t instanceof ExitStatusException && this.firstExitStatus == null) {
                ExitStatusException ex = (ExitStatusException)t;
                this.firstExitStatus = ex.getStatus();
                this.firstLocation = ex.getLocation();
            }
            this.exceptionMessage.append(System.lineSeparator());
            this.exceptionMessage.append(t.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void spinThreads() throws BuildException {
        this.stillRunning = true;
        this.timedOut = false;
        boolean interrupted = false;
        TaskRunnable[] runnables = (TaskRunnable[])this.nestedTasks.stream().map(x$0 -> new TaskRunnable((Task)x$0)).toArray(TaskRunnable[]::new);
        int numTasks = this.nestedTasks.size();
        int maxRunning = numTasks < this.numThreads ? numTasks : this.numThreads;
        TaskRunnable[] running = new TaskRunnable[maxRunning];
        ThreadGroup group = new ThreadGroup("parallel");
        TaskRunnable[] daemons = null;
        if (this.daemonTasks != null && !this.daemonTasks.tasks.isEmpty()) {
            daemons = new TaskRunnable[this.daemonTasks.tasks.size()];
        }
        Object object = this.semaphore;
        synchronized (object) {
        }
        object = this.semaphore;
        synchronized (object) {
            Thread thread;
            if (daemons != null) {
                for (int i = 0; i < daemons.length; ++i) {
                    daemons[i] = new TaskRunnable((Task)this.daemonTasks.tasks.get(i));
                    Thread daemonThread = new Thread(group, daemons[i]);
                    daemonThread.setDaemon(true);
                    daemonThread.start();
                }
            }
            int threadNumber = 0;
            for (int i = 0; i < maxRunning; ++i) {
                running[i] = runnables[threadNumber++];
                thread = new Thread(group, running[i]);
                thread.start();
            }
            if (this.timeout != 0L) {
                Thread timeoutThread = new Thread(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public synchronized void run() {
                        try {
                            long start = System.currentTimeMillis();
                            long end = start + Parallel.this.timeout;
                            long now = System.currentTimeMillis();
                            while (now < end) {
                                this.wait(end - now);
                                now = System.currentTimeMillis();
                            }
                            Object object = Parallel.this.semaphore;
                            synchronized (object) {
                                Parallel.this.stillRunning = false;
                                Parallel.this.timedOut = true;
                                Parallel.this.semaphore.notifyAll();
                            }
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                    }
                };
                timeoutThread.start();
            }
            try {
                block10: while (threadNumber < numTasks && this.stillRunning) {
                    for (int i = 0; i < maxRunning; ++i) {
                        if (running[i] != null && !running[i].isFinished()) continue;
                        running[i] = runnables[threadNumber++];
                        thread = new Thread(group, running[i]);
                        thread.start();
                        continue block10;
                    }
                    this.semaphore.wait();
                }
                block12: while (this.stillRunning) {
                    for (int i = 0; i < maxRunning; ++i) {
                        if (running[i] == null || running[i].isFinished()) continue;
                        this.semaphore.wait();
                        continue block12;
                    }
                    this.stillRunning = false;
                }
            }
            catch (InterruptedException ie) {
                interrupted = true;
            }
            if (!this.timedOut && !this.failOnAny) {
                this.killAll(running);
            }
        }
        if (interrupted) {
            throw new BuildException("Parallel execution interrupted.");
        }
        if (this.timedOut) {
            throw new BuildException("Parallel execution timed out");
        }
        this.exceptionMessage = new StringBuffer();
        this.numExceptions = 0;
        this.firstException = null;
        this.firstExitStatus = null;
        this.firstLocation = Location.UNKNOWN_LOCATION;
        this.processExceptions(daemons);
        this.processExceptions(runnables);
        if (this.numExceptions == 1) {
            if (this.firstException instanceof BuildException) {
                throw (BuildException)this.firstException;
            }
            throw new BuildException(this.firstException);
        }
        if (this.numExceptions > 1) {
            if (this.firstExitStatus == null) {
                throw new BuildException(this.exceptionMessage.toString(), this.firstLocation);
            }
            throw new ExitStatusException(this.exceptionMessage.toString(), this.firstExitStatus, this.firstLocation);
        }
    }

    private void killAll(TaskRunnable[] running) {
        boolean oneAlive;
        int tries = 0;
        do {
            oneAlive = false;
            for (TaskRunnable runnable : running) {
                if (runnable == null || runnable.isFinished()) continue;
                runnable.interrupt();
                Thread.yield();
                oneAlive = true;
            }
            if (!oneAlive) continue;
            ++tries;
            Thread.yield();
        } while (oneAlive && tries < 100);
    }

    public static class TaskList
    implements TaskContainer {
        private List<Task> tasks = new ArrayList<Task>();

        @Override
        public void addTask(Task nestedTask) {
            this.tasks.add(nestedTask);
        }
    }

    private class TaskRunnable
    implements Runnable {
        private Throwable exception;
        private Task task;
        private boolean finished;
        private volatile Thread thread;

        TaskRunnable(Task task) {
            this.task = task;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                LocalProperties.get(Parallel.this.getProject()).copy();
                this.thread = Thread.currentThread();
                this.task.perform();
            }
            catch (Throwable t) {
                this.exception = t;
                if (Parallel.this.failOnAny) {
                    Parallel.this.stillRunning = false;
                }
            }
            finally {
                Object object = Parallel.this.semaphore;
                synchronized (object) {
                    this.finished = true;
                    Parallel.this.semaphore.notifyAll();
                }
            }
        }

        public Throwable getException() {
            return this.exception;
        }

        boolean isFinished() {
            return this.finished;
        }

        void interrupt() {
            this.thread.interrupt();
        }
    }
}

