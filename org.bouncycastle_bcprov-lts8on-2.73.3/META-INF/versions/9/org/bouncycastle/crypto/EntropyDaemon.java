/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class EntropyDaemon
implements Runnable {
    private static final Logger LOG = Logger.getLogger(EntropyDaemon.class.getName());
    private final LinkedList<Runnable> tasks = new LinkedList();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addTask(Runnable task) {
        LinkedList<Runnable> linkedList = this.tasks;
        synchronized (linkedList) {
            this.tasks.add(task);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Runnable task;
            LinkedList<Runnable> linkedList = this.tasks;
            synchronized (linkedList) {
                task = this.tasks.poll();
            }
            if (task != null) {
                try {
                    task.run();
                }
                catch (Throwable throwable) {}
                continue;
            }
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("entropy thread interrupted - exiting");
        }
    }
}

