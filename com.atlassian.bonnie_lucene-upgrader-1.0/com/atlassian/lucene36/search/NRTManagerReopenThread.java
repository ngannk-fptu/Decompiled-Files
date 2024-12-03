/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.NRTManager;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.Closeable;
import java.io.IOException;

public class NRTManagerReopenThread
extends Thread
implements NRTManager.WaitingListener,
Closeable {
    private final NRTManager manager;
    private final long targetMaxStaleNS;
    private final long targetMinStaleNS;
    private boolean finish;
    private long waitingGen;

    public NRTManagerReopenThread(NRTManager manager, double targetMaxStaleSec, double targetMinStaleSec) {
        if (targetMaxStaleSec < targetMinStaleSec) {
            throw new IllegalArgumentException("targetMaxScaleSec (= " + targetMaxStaleSec + ") < targetMinStaleSec (=" + targetMinStaleSec + ")");
        }
        this.manager = manager;
        this.targetMaxStaleNS = (long)(1.0E9 * targetMaxStaleSec);
        this.targetMinStaleNS = (long)(1.0E9 * targetMinStaleSec);
        manager.addWaitingListener(this);
    }

    public synchronized void close() {
        this.manager.removeWaitingListener(this);
        this.finish = true;
        this.notify();
        try {
            this.join();
        }
        catch (InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
    }

    public synchronized void waiting(long targetGen) {
        this.waitingGen = Math.max(this.waitingGen, targetGen);
        this.notify();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public void run() {
        lastReopenStartNS = System.nanoTime();
        try {
            while (true) lbl-1000:
            // 2 sources

            {
                hasWaiting = false;
                var4_4 = this;
                synchronized (var4_4) {
                    while (!this.finish && (sleepNS = (nextReopenStartNS = lastReopenStartNS + ((hasWaiting = this.waitingGen > this.manager.getCurrentSearchingGen()) != false ? this.targetMinStaleNS : this.targetMaxStaleNS)) - System.nanoTime()) > 0L) {
                        try {
                            this.wait(sleepNS / 1000000L, (int)(sleepNS % 1000000L));
                        }
                        catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            this.finish = true;
                            break;
                        }
                    }
                    if (this.finish) {
                        return;
                    }
                }
                lastReopenStartNS = System.nanoTime();
                try {
                    this.manager.maybeRefresh();
                    continue;
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                break;
            }
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        {
            ** while (true)
        }
    }
}

