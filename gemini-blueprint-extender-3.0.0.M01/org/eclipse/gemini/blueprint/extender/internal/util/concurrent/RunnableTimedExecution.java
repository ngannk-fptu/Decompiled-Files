/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.internal.util.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.util.concurrent.Counter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

public abstract class RunnableTimedExecution {
    private static final Log log = LogFactory.getLog(RunnableTimedExecution.class);

    public static boolean execute(Runnable task, long waitTime) {
        return RunnableTimedExecution.execute(task, waitTime, null);
    }

    public static boolean execute(Runnable task, long waitTime, TaskExecutor taskExecutor) {
        Assert.notNull((Object)task);
        Counter counter = new Counter("counter for task: " + task);
        MonitoredRunnable wrapper = new MonitoredRunnable(task, counter);
        boolean internallyManaged = false;
        if (taskExecutor == null) {
            taskExecutor = new SimpleTaskExecutor();
            internallyManaged = true;
        }
        counter.increment();
        taskExecutor.execute((Runnable)wrapper);
        if (counter.waitForZero(waitTime)) {
            log.error((Object)(task + " did not finish in " + waitTime + "ms; consider taking a snapshot and then shutdown the VM in case the thread still hangs"));
            if (internallyManaged) {
                try {
                    ((DisposableBean)taskExecutor).destroy();
                }
                catch (Exception e) {
                    log.error((Object)"Exception thrown while destroying internally managed thread executor", (Throwable)e);
                }
            }
            return true;
        }
        return false;
    }

    private static class SimpleTaskExecutor
    implements TaskExecutor,
    DisposableBean {
        private Thread thread;

        private SimpleTaskExecutor() {
        }

        public void execute(Runnable task) {
            this.thread = new Thread(task);
            this.thread.setName("Thread for runnable [" + task + "]");
            this.thread.start();
        }

        public void destroy() throws Exception {
            if (this.thread != null) {
                this.thread.interrupt();
            }
        }
    }

    private static class MonitoredRunnable
    implements Runnable {
        private Runnable task;
        private Counter counter;

        public MonitoredRunnable(Runnable task, Counter counter) {
            this.task = task;
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                this.task.run();
            }
            finally {
                this.counter.decrement();
            }
        }
    }
}

