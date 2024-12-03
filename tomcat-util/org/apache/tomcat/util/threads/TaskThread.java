/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.threads;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.threads.StopPooledThreadException;

public class TaskThread
extends Thread {
    private static final Log log = LogFactory.getLog(TaskThread.class);
    private final long creationTime = System.currentTimeMillis();

    public TaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, new WrappingRunnable(target), name);
    }

    public TaskThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, new WrappingRunnable(target), name, stackSize);
    }

    public final long getCreationTime() {
        return this.creationTime;
    }

    private static class WrappingRunnable
    implements Runnable {
        private Runnable wrappedRunnable;

        WrappingRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }

        @Override
        public void run() {
            try {
                this.wrappedRunnable.run();
            }
            catch (StopPooledThreadException exc) {
                log.debug((Object)"Thread exiting on purpose", (Throwable)exc);
            }
        }
    }
}

