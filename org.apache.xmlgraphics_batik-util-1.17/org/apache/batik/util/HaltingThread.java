/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

public class HaltingThread
extends Thread {
    protected boolean beenHalted = false;

    public HaltingThread() {
    }

    public HaltingThread(Runnable r) {
        super(r);
    }

    public HaltingThread(String name) {
        super(name);
    }

    public HaltingThread(Runnable r, String name) {
        super(r, name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isHalted() {
        HaltingThread haltingThread = this;
        synchronized (haltingThread) {
            return this.beenHalted;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void halt() {
        HaltingThread haltingThread = this;
        synchronized (haltingThread) {
            this.beenHalted = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearHalted() {
        HaltingThread haltingThread = this;
        synchronized (haltingThread) {
            this.beenHalted = false;
        }
    }

    public static void haltThread() {
        HaltingThread.haltThread(Thread.currentThread());
    }

    public static void haltThread(Thread t) {
        if (t instanceof HaltingThread) {
            ((HaltingThread)t).halt();
        }
    }

    public static boolean hasBeenHalted() {
        return HaltingThread.hasBeenHalted(Thread.currentThread());
    }

    public static boolean hasBeenHalted(Thread t) {
        if (t instanceof HaltingThread) {
            return ((HaltingThread)t).isHalted();
        }
        return false;
    }
}

