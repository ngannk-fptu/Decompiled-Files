/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

import java.util.Comparator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServices {
    public static final boolean useActualTime;
    private static long currentTime;
    private static final Object timeUpdateMonitor;

    public static long nanoTime() {
        if (useActualTime) {
            return System.nanoTime();
        }
        return currentTime;
    }

    public static long currentTimeMillis() {
        if (useActualTime) {
            return System.currentTimeMillis();
        }
        return currentTime / 1000000L;
    }

    public static void sleepMsecs(long sleepTimeMsec) {
        try {
            if (useActualTime) {
                Thread.sleep(sleepTimeMsec);
            } else {
                TimeServices.waitUntilTime(currentTime + sleepTimeMsec * 1000000L);
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public static void sleepNanos(long sleepTimeNsec) {
        try {
            if (useActualTime) {
                TimeUnit.NANOSECONDS.sleep(sleepTimeNsec);
            } else {
                TimeServices.waitUntilTime(currentTime + sleepTimeNsec);
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void waitUntilTime(long timeToWakeAt) throws InterruptedException {
        Object object = timeUpdateMonitor;
        synchronized (object) {
            while (timeToWakeAt > currentTime) {
                timeUpdateMonitor.wait();
            }
        }
    }

    public static void moveTimeForward(long timeDeltaNsec) throws InterruptedException {
        TimeServices.setCurrentTime(currentTime + timeDeltaNsec);
    }

    public static void moveTimeForwardMsec(long timeDeltaMsec) throws InterruptedException {
        TimeServices.moveTimeForward(timeDeltaMsec * 1000000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setCurrentTime(long newCurrentTime) throws InterruptedException {
        if (newCurrentTime < TimeServices.nanoTime()) {
            throw new IllegalStateException("Can't set current time to the past.");
        }
        if (useActualTime) {
            while (newCurrentTime > TimeServices.nanoTime()) {
                TimeUnit.NANOSECONDS.sleep(newCurrentTime - TimeServices.nanoTime());
            }
            return;
        }
        while (currentTime < newCurrentTime) {
            long timeDelta = Math.min(newCurrentTime - currentTime, 5000000L);
            currentTime += timeDelta;
            Object object = timeUpdateMonitor;
            synchronized (object) {
                timeUpdateMonitor.notifyAll();
                TimeUnit.NANOSECONDS.sleep(50000L);
            }
        }
    }

    static {
        timeUpdateMonitor = new Object();
        String useActualTimeProperty = System.getProperty("LatencyUtils.useActualTime", "true");
        useActualTime = !useActualTimeProperty.equals("false");
    }

    public static class ScheduledExecutor {
        private final ScheduledThreadPoolExecutor actualExecutor;
        final MyExecutorThread internalExecutorThread;
        final PriorityBlockingQueue<RunnableTaskEntry> taskEntries;
        private static CompareRunnableTaskEntryByStartTime compareRunnableTaskEntryByStartTime = new CompareRunnableTaskEntryByStartTime();

        ScheduledExecutor() {
            if (useActualTime) {
                this.actualExecutor = new ScheduledThreadPoolExecutor(1);
                this.internalExecutorThread = null;
                this.taskEntries = null;
            } else {
                this.actualExecutor = null;
                this.taskEntries = new PriorityBlockingQueue<RunnableTaskEntry>(10000, compareRunnableTaskEntryByStartTime);
                this.internalExecutorThread = new MyExecutorThread();
                this.internalExecutorThread.setDaemon(true);
                this.internalExecutorThread.start();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            if (useActualTime) {
                this.actualExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
                return;
            }
            long startTimeNsec = currentTime + TimeUnit.NANOSECONDS.convert(initialDelay, unit);
            long periodNsec = TimeUnit.NANOSECONDS.convert(period, unit);
            RunnableTaskEntry entry = new RunnableTaskEntry(command, startTimeNsec, periodNsec, true);
            Object object = timeUpdateMonitor;
            synchronized (object) {
                this.taskEntries.add(entry);
                timeUpdateMonitor.notifyAll();
            }
        }

        public void shutdown() {
            if (useActualTime) {
                this.actualExecutor.shutdownNow();
                return;
            }
            this.internalExecutorThread.terminate();
        }

        static class CompareRunnableTaskEntryByStartTime
        implements Comparator<RunnableTaskEntry> {
            CompareRunnableTaskEntryByStartTime() {
            }

            @Override
            public int compare(RunnableTaskEntry r1, RunnableTaskEntry r2) {
                long t1 = r1.startTime;
                long t2 = r2.startTime;
                return t1 > t2 ? 1 : (t1 < t2 ? -1 : 0);
            }
        }

        private static class RunnableTaskEntry {
            long startTime;
            Runnable command;
            long period;
            long initialStartTime;
            long executionCount;
            boolean fixedRate;

            RunnableTaskEntry(Runnable command, long startTimeNsec, long periodNsec, boolean fixedRate) {
                this.command = command;
                this.startTime = startTimeNsec;
                this.initialStartTime = startTimeNsec;
                this.period = periodNsec;
                this.fixedRate = fixedRate;
            }

            boolean shouldReschedule() {
                return this.period != 0L;
            }

            public long getStartTime() {
                return this.startTime;
            }

            public Runnable getCommand() {
                return this.command;
            }

            public void setNewStartTime(long timeNow) {
                if (this.period == 0L) {
                    throw new IllegalStateException("should nto try to reschedule an entry that has no interval or rare");
                }
                if (!this.fixedRate) {
                    this.startTime = timeNow + this.period;
                } else {
                    ++this.executionCount;
                    this.startTime = this.initialStartTime + this.executionCount * this.period;
                }
            }
        }

        private class MyExecutorThread
        extends Thread {
            volatile boolean doRun = true;

            private MyExecutorThread() {
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            void terminate() {
                Object object = timeUpdateMonitor;
                synchronized (object) {
                    this.doRun = false;
                    timeUpdateMonitor.notifyAll();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void run() {
                try {
                    while (this.doRun) {
                        Object object = timeUpdateMonitor;
                        synchronized (object) {
                            RunnableTaskEntry entry = ScheduledExecutor.this.taskEntries.peek();
                            while (entry != null && entry.getStartTime() < currentTime) {
                                entry.getCommand().run();
                                if (entry.shouldReschedule()) {
                                    entry.setNewStartTime(currentTime);
                                    ScheduledExecutor.this.taskEntries.add(entry);
                                }
                                entry = ScheduledExecutor.this.taskEntries.peek();
                            }
                            timeUpdateMonitor.wait();
                        }
                    }
                    return;
                }
                catch (InterruptedException interruptedException) {
                    return;
                }
                catch (CancellationException cancellationException) {
                    // empty catch block
                }
            }
        }
    }
}

