/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import java.util.LinkedList;
import java.util.ListIterator;

public class TimerThread
extends Thread {
    private static TimerThread instance;
    private final LinkedList timerList = new LinkedList();
    private long nextTimeout;

    public static synchronized TimerThread getInstance() {
        if (instance == null) {
            instance = new TimerThread();
            instance.start();
        }
        return instance;
    }

    public TimerThread() {
        super("jTDS TimerThread");
        this.setDaemon(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        LinkedList linkedList = this.timerList;
        synchronized (linkedList) {
            boolean run = true;
            while (run) {
                try {
                    long ms;
                    while ((ms = this.nextTimeout - System.currentTimeMillis()) > 0L || this.nextTimeout == 0L) {
                        this.timerList.wait(this.nextTimeout == 0L ? 0L : ms);
                    }
                    long time = System.currentTimeMillis();
                    while (!this.timerList.isEmpty()) {
                        TimerRequest t = (TimerRequest)this.timerList.getFirst();
                        if (t.time > time) break;
                        t.target.timerExpired();
                        this.timerList.removeFirst();
                    }
                    this.updateNextTimeout();
                }
                catch (InterruptedException e) {
                    run = false;
                    this.timerList.clear();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object setTimer(int timeout, TimerListener l) {
        TimerRequest t = new TimerRequest(timeout, l);
        LinkedList linkedList = this.timerList;
        synchronized (linkedList) {
            if (this.timerList.isEmpty()) {
                this.timerList.add(t);
            } else {
                TimerRequest crt = (TimerRequest)this.timerList.getLast();
                if (t.time >= crt.time) {
                    this.timerList.addLast(t);
                } else {
                    ListIterator<TimerRequest> li = this.timerList.listIterator();
                    while (li.hasNext()) {
                        crt = (TimerRequest)li.next();
                        if (t.time >= crt.time) continue;
                        li.previous();
                        li.add(t);
                        break;
                    }
                }
            }
            if (this.timerList.getFirst() == t) {
                this.nextTimeout = t.time;
                this.timerList.notifyAll();
            }
        }
        return t;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean cancelTimer(Object handle) {
        TimerRequest t = (TimerRequest)handle;
        LinkedList linkedList = this.timerList;
        synchronized (linkedList) {
            boolean result = this.timerList.remove(t);
            if (this.nextTimeout == t.time) {
                this.updateNextTimeout();
            }
            return result;
        }
    }

    public static synchronized void stopTimer() {
        if (instance != null) {
            instance.interrupt();
            instance = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasExpired(Object handle) {
        TimerRequest t = (TimerRequest)handle;
        LinkedList linkedList = this.timerList;
        synchronized (linkedList) {
            return !this.timerList.contains(t);
        }
    }

    private void updateNextTimeout() {
        this.nextTimeout = this.timerList.isEmpty() ? 0L : ((TimerRequest)this.timerList.getFirst()).time;
    }

    private static class TimerRequest {
        final long time;
        final TimerListener target;

        TimerRequest(int timeout, TimerListener target) {
            if (timeout <= 0) {
                throw new IllegalArgumentException("Invalid timeout parameter " + timeout);
            }
            this.time = System.currentTimeMillis() + (long)timeout;
            this.target = target;
        }
    }

    public static interface TimerListener {
        public void timerExpired();
    }
}

