/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.Watchable;

public abstract class BaseWatchable
implements Watchable,
Runnable {
    private int status = 0;
    private Object statusLock = new Object();
    private Object parserLock = new Object();
    private Gate gate;
    private static boolean SuppressSetErrorStackTrace = false;
    private Thread thread;

    protected BaseWatchable() {
        this.setStatus(1);
    }

    protected abstract int iterate() throws Exception;

    protected void setup() {
    }

    protected void cleanup() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        if (this.getStatus() == 1) {
            this.setup();
        }
        this.setStatus(2);
        Object object = this.parserLock;
        // MONITORENTER : object
        while (!this.isFinished() && this.getStatus() != 5) {
            if (this.isExecutable()) {
                this.setStatus(4);
                try {
                    while (!(this.getStatus() != 4 || this.gate != null && this.gate.iterate())) {
                        this.setStatus(this.iterate());
                    }
                    if (this.getStatus() != 4) continue;
                    this.setStatus(2);
                }
                catch (Exception ex) {
                    this.setError(ex);
                }
                continue;
            }
            Object object2 = this.statusLock;
            // MONITORENTER : object2
            if (!this.isExecutable()) {
                try {
                    this.statusLock.wait();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            // MONITOREXIT : object2
        }
        // MONITOREXIT : object
        if (this.getStatus() == 6 || this.getStatus() == 7) {
            this.cleanup();
        }
        this.thread = null;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    public boolean isFinished() {
        int s = this.getStatus();
        return s == 6 || s == 7;
    }

    public boolean isExecutable() {
        return !(this.status != 2 && this.status != 4 || this.gate != null && this.gate.stop());
    }

    @Override
    public void stop() {
        this.setStatus(5);
    }

    @Override
    public synchronized void go() {
        this.gate = null;
        this.execute(false);
    }

    public synchronized void go(boolean synchronous) {
        this.gate = null;
        this.execute(synchronous);
    }

    @Override
    public synchronized void go(int steps) {
        this.gate = new Gate();
        this.gate.setStopIterations(steps);
        this.execute(false);
    }

    @Override
    public synchronized void go(long millis) {
        this.gate = new Gate();
        this.gate.setStopTime(millis);
        this.execute(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void waitForFinish() {
        Object object = this.statusLock;
        synchronized (object) {
            while (!this.isFinished() && this.getStatus() != 5) {
                try {
                    this.statusLock.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void execute(boolean synchronous) {
        if (this.thread != null) {
            Object object = this.statusLock;
            synchronized (object) {
                this.statusLock.notifyAll();
            }
            return;
        }
        if (this.isFinished()) {
            return;
        }
        if (synchronous) {
            this.thread = Thread.currentThread();
            this.run();
        } else {
            this.thread = new Thread(this);
            this.thread.setName(this.getClass().getName());
            this.thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setStatus(int status) {
        Object object = this.statusLock;
        synchronized (object) {
            this.status = status;
            this.statusLock.notifyAll();
        }
    }

    public static boolean isSuppressSetErrorStackTrace() {
        return SuppressSetErrorStackTrace;
    }

    public static void setSuppressSetErrorStackTrace(boolean suppressTrace) {
        SuppressSetErrorStackTrace = suppressTrace;
    }

    protected void setError(Exception error) {
        if (!SuppressSetErrorStackTrace) {
            error.printStackTrace();
        }
        this.setStatus(7);
    }

    private String getStatusString() {
        switch (this.getStatus()) {
            case 1: {
                return "Not started";
            }
            case 4: {
                return "Running";
            }
            case 3: {
                return "Needs Data";
            }
            case 2: {
                return "Paused";
            }
            case 5: {
                return "Stopped";
            }
            case 6: {
                return "Completed";
            }
            case 7: {
                return "Error";
            }
        }
        return "Unknown";
    }

    class Gate {
        private boolean timeBased;
        private long nextGate;

        Gate() {
        }

        public void setStopTime(long millisFromNow) {
            this.timeBased = true;
            this.nextGate = System.currentTimeMillis() + millisFromNow;
        }

        public void setStopIterations(int iterations) {
            this.timeBased = false;
            this.nextGate = iterations;
        }

        public boolean stop() {
            if (this.timeBased) {
                return System.currentTimeMillis() >= this.nextGate;
            }
            return this.nextGate < 0L;
        }

        public boolean iterate() {
            if (!this.timeBased) {
                --this.nextGate;
            }
            return this.stop();
        }
    }
}

