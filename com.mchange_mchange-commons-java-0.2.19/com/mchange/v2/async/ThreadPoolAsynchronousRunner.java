/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v2.async.AsynchronousRunner;
import com.mchange.v2.async.ThreadPerTaskAsynchronousRunner;
import com.mchange.v2.io.IndentedWriter;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.util.ResourceClosedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public final class ThreadPoolAsynchronousRunner
implements AsynchronousRunner {
    static final MLogger logger = MLog.getLogger(ThreadPoolAsynchronousRunner.class);
    static final int POLL_FOR_STOP_INTERVAL = 5000;
    static final int DFLT_DEADLOCK_DETECTOR_INTERVAL = 10000;
    static final int DFLT_INTERRUPT_DELAY_AFTER_APPARENT_DEADLOCK = 60000;
    static final int DFLT_MAX_INDIVIDUAL_TASK_TIME = 0;
    static final int DFLT_MAX_EMERGENCY_THREADS = 10;
    static final long PURGE_EVERY = 500L;
    int deadlock_detector_interval;
    int interrupt_delay_after_apparent_deadlock;
    int max_individual_task_time;
    int num_threads;
    boolean daemon;
    HashSet managed;
    HashSet available;
    LinkedList pendingTasks;
    Random rnd = new Random();
    Timer myTimer;
    boolean should_cancel_timer;
    TimerTask deadlockDetector = new DeadlockDetector();
    TimerTask replacedThreadInterruptor = null;
    Map stoppedThreadsToStopDates = new HashMap();
    String threadLabel;

    private ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4, Timer timer, boolean bl2, String string) {
        this.num_threads = n;
        this.daemon = bl;
        this.max_individual_task_time = n2;
        this.deadlock_detector_interval = n3;
        this.interrupt_delay_after_apparent_deadlock = n4;
        this.myTimer = timer;
        this.should_cancel_timer = bl2;
        this.threadLabel = string;
        this.recreateThreadsAndTasks();
        timer.schedule(this.deadlockDetector, n3, (long)n3);
    }

    private ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4, Timer timer, boolean bl2) {
        this(n, bl, n2, n3, n4, timer, bl2, null);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4, Timer timer, String string) {
        this(n, bl, n2, n3, n4, timer, false, string);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4, Timer timer) {
        this(n, bl, n2, n3, n4, timer, false);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4, String string) {
        this(n, bl, n2, n3, n4, new Timer(true), true, string);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, int n2, int n3, int n4) {
        this(n, bl, n2, n3, n4, new Timer(true), true);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, Timer timer, String string) {
        this(n, bl, 0, 10000, 60000, timer, false, string);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl, Timer timer) {
        this(n, bl, 0, 10000, 60000, timer, false);
    }

    public ThreadPoolAsynchronousRunner(int n, boolean bl) {
        this(n, bl, 0, 10000, 60000, new Timer(true), true);
    }

    @Override
    public synchronized void postRunnable(Runnable runnable) {
        try {
            this.pendingTasks.add(runnable);
            this.notifyAll();
            if (logger.isLoggable(MLevel.FINEST)) {
                logger.log(MLevel.FINEST, this + ": Adding task to queue -- " + runnable);
            }
        }
        catch (NullPointerException nullPointerException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "NullPointerException while posting Runnable -- Probably we're closed.", nullPointerException);
            }
            throw new ResourceClosedException("Attempted to use a ThreadPoolAsynchronousRunner in a closed or broken state.");
        }
    }

    public synchronized int getThreadCount() {
        return this.managed.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close(boolean bl) {
        ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = this;
        synchronized (threadPoolAsynchronousRunner) {
            if (this.managed == null) {
                return;
            }
            this.deadlockDetector.cancel();
            if (this.should_cancel_timer) {
                this.myTimer.cancel();
            }
            this.myTimer = null;
            for (Runnable runnable : this.managed) {
                runnable.gentleStop();
                if (!bl) continue;
                runnable.interrupt();
            }
            this.managed = null;
            if (!bl) {
                Iterator iterator = this.pendingTasks.iterator();
                while (iterator.hasNext()) {
                    Runnable runnable;
                    runnable = (Runnable)iterator.next();
                    new Thread(runnable).start();
                    iterator.remove();
                }
            }
            this.available = null;
            this.pendingTasks = null;
        }
    }

    @Override
    public void close() {
        this.close(true);
    }

    public synchronized int getActiveCount() {
        return this.managed.size() - this.available.size();
    }

    public synchronized int getIdleCount() {
        return this.available.size();
    }

    public synchronized int getPendingTaskCount() {
        return this.pendingTasks.size();
    }

    public synchronized String getStatus() {
        return this.getMultiLineStatusString();
    }

    public synchronized String getStackTraces() {
        return this.getStackTraces(0);
    }

    private String getStackTraces(int n) {
        assert (Thread.holdsLock(this));
        if (this.managed == null) {
            return null;
        }
        try {
            Method method = Thread.class.getMethod("getStackTrace", null);
            StringWriter stringWriter = new StringWriter(2048);
            IndentedWriter indentedWriter = new IndentedWriter(stringWriter);
            for (int i = 0; i < n; ++i) {
                indentedWriter.upIndent();
            }
            for (Object e : this.managed) {
                Object[] objectArray = (Object[])method.invoke(e, (Object[])null);
                this.printStackTraces(indentedWriter, e, objectArray);
            }
            for (int i = 0; i < n; ++i) {
                indentedWriter.downIndent();
            }
            indentedWriter.flush();
            String string = stringWriter.toString();
            indentedWriter.close();
            return string;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine(this + ": stack traces unavailable because this is a pre-Java 1.5 VM.");
            }
            return null;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, this + ": An Exception occurred while trying to extract PoolThread stack traces.", exception);
            }
            return null;
        }
    }

    private String getJvmStackTraces(int n) {
        try {
            Method method = Thread.class.getMethod("getAllStackTraces", null);
            Map map = (Map)method.invoke(null, (Object[])null);
            StringWriter stringWriter = new StringWriter(2048);
            IndentedWriter indentedWriter = new IndentedWriter(stringWriter);
            for (int i = 0; i < n; ++i) {
                indentedWriter.upIndent();
            }
            for (Map.Entry entry : map.entrySet()) {
                Object k = entry.getKey();
                Object[] objectArray = (Object[])entry.getValue();
                this.printStackTraces(indentedWriter, k, objectArray);
            }
            for (int i = 0; i < n; ++i) {
                indentedWriter.downIndent();
            }
            indentedWriter.flush();
            String string = stringWriter.toString();
            indentedWriter.close();
            return string;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine(this + ": JVM stack traces unavailable because this is a pre-Java 1.5 VM.");
            }
            return null;
        }
        catch (Exception exception) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, this + ": An Exception occurred while trying to extract PoolThread stack traces.", exception);
            }
            return null;
        }
    }

    private void printStackTraces(IndentedWriter indentedWriter, Object object, Object[] objectArray) throws IOException {
        indentedWriter.println(object);
        indentedWriter.upIndent();
        int n = objectArray.length;
        for (int i = 0; i < n; ++i) {
            indentedWriter.println(objectArray[i]);
        }
        indentedWriter.downIndent();
    }

    public synchronized String getMultiLineStatusString() {
        return this.getMultiLineStatusString(0);
    }

    private String getMultiLineStatusString(int n) {
        try {
            int n2;
            StringWriter stringWriter = new StringWriter(2048);
            IndentedWriter indentedWriter = new IndentedWriter(stringWriter);
            for (n2 = 0; n2 < n; ++n2) {
                indentedWriter.upIndent();
            }
            if (this.managed == null) {
                indentedWriter.print("[");
                indentedWriter.print(this);
                indentedWriter.println(" closed.]");
            } else {
                HashSet hashSet = (HashSet)this.managed.clone();
                hashSet.removeAll(this.available);
                indentedWriter.print("Managed Threads: ");
                indentedWriter.println(this.managed.size());
                indentedWriter.print("Active Threads: ");
                indentedWriter.println(hashSet.size());
                indentedWriter.println("Active Tasks: ");
                indentedWriter.upIndent();
                for (PoolThread poolThread : hashSet) {
                    indentedWriter.println(poolThread.getCurrentTask());
                    indentedWriter.upIndent();
                    indentedWriter.print("on thread: ");
                    indentedWriter.println(poolThread.getName());
                    indentedWriter.downIndent();
                }
                indentedWriter.downIndent();
                indentedWriter.println("Pending Tasks: ");
                indentedWriter.upIndent();
                int n3 = this.pendingTasks.size();
                for (int i = 0; i < n3; ++i) {
                    indentedWriter.println(this.pendingTasks.get(i));
                }
                indentedWriter.downIndent();
            }
            for (n2 = 0; n2 < n; ++n2) {
                indentedWriter.downIndent();
            }
            indentedWriter.flush();
            String string = stringWriter.toString();
            indentedWriter.close();
            return string;
        }
        catch (IOException iOException) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Huh? An IOException when working with a StringWriter?!?", iOException);
            }
            throw new RuntimeException("Huh? An IOException when working with a StringWriter?!? " + iOException);
        }
    }

    private void appendStatusString(StringBuffer stringBuffer) {
        if (this.managed == null) {
            stringBuffer.append("[closed]");
        } else {
            HashSet hashSet = (HashSet)this.managed.clone();
            hashSet.removeAll(this.available);
            stringBuffer.append("[num_managed_threads: ");
            stringBuffer.append(this.managed.size());
            stringBuffer.append(", num_active: ");
            stringBuffer.append(hashSet.size());
            stringBuffer.append("; activeTasks: ");
            boolean bl = true;
            Iterator iterator = hashSet.iterator();
            while (iterator.hasNext()) {
                if (bl) {
                    bl = false;
                } else {
                    stringBuffer.append(", ");
                }
                PoolThread poolThread = (PoolThread)iterator.next();
                stringBuffer.append(poolThread.getCurrentTask());
                stringBuffer.append(" (");
                stringBuffer.append(poolThread.getName());
                stringBuffer.append(')');
            }
            stringBuffer.append("; pendingTasks: ");
            int n = this.pendingTasks.size();
            for (int i = 0; i < n; ++i) {
                if (i != 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(this.pendingTasks.get(i));
            }
            stringBuffer.append(']');
        }
    }

    private void recreateThreadsAndTasks() {
        if (this.managed != null) {
            Date date = new Date();
            for (PoolThread poolThread : this.managed) {
                poolThread.gentleStop();
                this.stoppedThreadsToStopDates.put(poolThread, date);
                this.ensureReplacedThreadsProcessing();
            }
        }
        this.managed = new HashSet();
        this.available = new HashSet();
        this.pendingTasks = new LinkedList();
        for (int i = 0; i < this.num_threads; ++i) {
            PoolThread poolThread = new PoolThread(i, this.daemon);
            this.managed.add(poolThread);
            this.available.add(poolThread);
            poolThread.start();
        }
    }

    private void processReplacedThreads() {
        long l = System.currentTimeMillis();
        Iterator iterator = this.stoppedThreadsToStopDates.keySet().iterator();
        while (iterator.hasNext()) {
            PoolThread poolThread = (PoolThread)iterator.next();
            if (!poolThread.isAlive()) {
                iterator.remove();
            } else {
                Date date = (Date)this.stoppedThreadsToStopDates.get(poolThread);
                if (l - date.getTime() > (long)this.interrupt_delay_after_apparent_deadlock) {
                    if (logger.isLoggable(MLevel.WARNING)) {
                        logger.log(MLevel.WARNING, "Task " + poolThread.getCurrentTask() + " (in deadlocked PoolThread) failed to complete in maximum time " + this.interrupt_delay_after_apparent_deadlock + "ms. Trying interrupt().");
                    }
                    poolThread.interrupt();
                    iterator.remove();
                }
            }
            if (!this.stoppedThreadsToStopDates.isEmpty()) continue;
            this.stopReplacedThreadsProcessing();
        }
    }

    private void ensureReplacedThreadsProcessing() {
        if (this.replacedThreadInterruptor == null) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine("Apparently some threads have been replaced. Replacement thread processing enabled.");
            }
            this.replacedThreadInterruptor = new ReplacedThreadInterruptor();
            int n = this.interrupt_delay_after_apparent_deadlock / 4;
            this.myTimer.schedule(this.replacedThreadInterruptor, n, (long)n);
        }
    }

    private void stopReplacedThreadsProcessing() {
        if (this.replacedThreadInterruptor != null) {
            this.replacedThreadInterruptor.cancel();
            this.replacedThreadInterruptor = null;
            if (logger.isLoggable(MLevel.FINE)) {
                logger.fine("Apparently all replaced threads have either completed their tasks or been interrupted(). Replacement thread processing cancelled.");
            }
        }
    }

    private void shuttingDown(PoolThread poolThread) {
        if (this.managed != null && this.managed.contains(poolThread)) {
            this.managed.remove(poolThread);
            this.available.remove(poolThread);
            PoolThread poolThread2 = new PoolThread(poolThread.getIndex(), this.daemon);
            this.managed.add(poolThread2);
            this.available.add(poolThread2);
            poolThread2.start();
        }
    }

    private void runInEmergencyThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        if (this.max_individual_task_time > 0) {
            MaxIndividualTaskTimeEnforcer maxIndividualTaskTimeEnforcer = new MaxIndividualTaskTimeEnforcer(thread, thread + " [One-off emergency thread!!!]", runnable.toString());
            this.myTimer.schedule((TimerTask)maxIndividualTaskTimeEnforcer, this.max_individual_task_time);
        }
    }

    class ReplacedThreadInterruptor
    extends TimerTask {
        ReplacedThreadInterruptor() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
            synchronized (threadPoolAsynchronousRunner) {
                ThreadPoolAsynchronousRunner.this.processReplacedThreads();
            }
        }
    }

    class MaxIndividualTaskTimeEnforcer
    extends TimerTask {
        PoolThread pt;
        Thread interruptMe;
        String threadStr;
        String fixedTaskStr;

        MaxIndividualTaskTimeEnforcer(PoolThread poolThread) {
            this.pt = poolThread;
            this.interruptMe = poolThread;
            this.threadStr = poolThread.toString();
            this.fixedTaskStr = null;
        }

        MaxIndividualTaskTimeEnforcer(Thread thread, String string, String string2) {
            this.pt = null;
            this.interruptMe = thread;
            this.threadStr = string;
            this.fixedTaskStr = string2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            String string;
            if (this.fixedTaskStr != null) {
                string = this.fixedTaskStr;
            } else if (this.pt != null) {
                ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
                synchronized (threadPoolAsynchronousRunner) {
                    string = String.valueOf(this.pt.getCurrentTask());
                }
            } else {
                string = "Unknown task?!";
            }
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("A task has exceeded the maximum allowable task time. Will interrupt() thread [" + this.threadStr + "], with current task: " + string);
            }
            this.interruptMe.interrupt();
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("Thread [" + this.threadStr + "] interrupted.");
            }
        }
    }

    class DeadlockDetector
    extends TimerTask {
        LinkedList last = null;
        LinkedList current = null;

        DeadlockDetector() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Object object;
            boolean bl = false;
            AsynchronousRunner asynchronousRunner = ThreadPoolAsynchronousRunner.this;
            synchronized (asynchronousRunner) {
                if (ThreadPoolAsynchronousRunner.this.pendingTasks.size() == 0) {
                    this.last = null;
                    if (logger.isLoggable(MLevel.FINEST)) {
                        logger.log(MLevel.FINEST, this + " -- Running DeadlockDetector[Exiting. No pending tasks.]");
                    }
                    return;
                }
                this.current = (LinkedList)ThreadPoolAsynchronousRunner.this.pendingTasks.clone();
                if (logger.isLoggable(MLevel.FINEST)) {
                    logger.log(MLevel.FINEST, this + " -- Running DeadlockDetector[last->" + this.last + ",current->" + this.current + ']');
                }
                if (this.current.equals(this.last)) {
                    String string;
                    PrintWriter printWriter;
                    if (logger.isLoggable(MLevel.WARNING)) {
                        logger.warning(this + " -- APPARENT DEADLOCK!!! Creating emergency threads for unassigned pending tasks!");
                        object = new StringWriter(4096);
                        printWriter = new PrintWriter((Writer)object);
                        printWriter.print(this);
                        printWriter.println(" -- APPARENT DEADLOCK!!! Complete Status: ");
                        printWriter.print(ThreadPoolAsynchronousRunner.this.getMultiLineStatusString(1));
                        printWriter.println("Pool thread stack traces:");
                        string = ThreadPoolAsynchronousRunner.this.getStackTraces(1);
                        if (string == null) {
                            printWriter.println("\t[Stack traces of deadlocked task threads not available.]");
                        } else {
                            printWriter.print(string);
                        }
                        printWriter.flush();
                        logger.warning(((StringWriter)object).toString());
                        printWriter.close();
                    }
                    if (logger.isLoggable(MLevel.FINER)) {
                        object = new StringWriter(4096);
                        printWriter = new PrintWriter((Writer)object);
                        printWriter.print(this);
                        printWriter.println(" -- APPARENT DEADLOCK extra info, full JVM thread dump: ");
                        string = ThreadPoolAsynchronousRunner.this.getJvmStackTraces(1);
                        if (string == null) {
                            printWriter.println("\t[Full JVM thread dump not available.]");
                        } else {
                            printWriter.print(string);
                        }
                        printWriter.flush();
                        logger.finer(((StringWriter)object).toString());
                        printWriter.close();
                    }
                    ThreadPoolAsynchronousRunner.this.recreateThreadsAndTasks();
                    bl = true;
                }
            }
            if (bl) {
                asynchronousRunner = new ThreadPerTaskAsynchronousRunner(10, ThreadPoolAsynchronousRunner.this.max_individual_task_time);
                object = this.current.iterator();
                while (object.hasNext()) {
                    asynchronousRunner.postRunnable((Runnable)object.next());
                }
                asynchronousRunner.close(false);
                this.last = null;
            } else {
                this.last = this.current;
            }
            this.current = null;
        }
    }

    class PoolThread
    extends Thread {
        Runnable currentTask;
        boolean should_stop;
        int index;
        TimerTask maxIndividualTaskTimeEnforcer = null;

        PoolThread(int n, boolean bl) {
            this.setName((ThreadPoolAsynchronousRunner.this.threadLabel == null ? this.getClass().getName() : ThreadPoolAsynchronousRunner.this.threadLabel) + "-#" + n);
            this.setDaemon(bl);
            this.index = n;
        }

        public int getIndex() {
            return this.index;
        }

        void gentleStop() {
            this.should_stop = true;
        }

        Runnable getCurrentTask() {
            return this.currentTask;
        }

        private void setMaxIndividualTaskTimeEnforcer() {
            this.maxIndividualTaskTimeEnforcer = new MaxIndividualTaskTimeEnforcer(this);
            ThreadPoolAsynchronousRunner.this.myTimer.schedule(this.maxIndividualTaskTimeEnforcer, ThreadPoolAsynchronousRunner.this.max_individual_task_time);
        }

        private void cancelMaxIndividualTaskTimeEnforcer() {
            this.maxIndividualTaskTimeEnforcer.cancel();
            this.maxIndividualTaskTimeEnforcer = null;
        }

        private void purgeTimer() {
            ThreadPoolAsynchronousRunner.this.myTimer.purge();
            if (logger.isLoggable(MLevel.FINER)) {
                logger.log(MLevel.FINER, this.getClass().getName() + " -- PURGING TIMER");
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void run() {
            long l = ThreadPoolAsynchronousRunner.this.rnd.nextLong();
            while (true) {
                try {
                    Object object;
                    ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
                    synchronized (threadPoolAsynchronousRunner) {
                        while (!this.should_stop && ThreadPoolAsynchronousRunner.this.pendingTasks.size() == 0) {
                            ThreadPoolAsynchronousRunner.this.wait(5000L);
                        }
                        if (this.should_stop) {
                            return;
                        }
                        if (!ThreadPoolAsynchronousRunner.this.available.remove(this)) {
                            throw new InternalError("An unavailable PoolThread tried to check itself out!!!");
                        }
                        this.currentTask = object = (Runnable)ThreadPoolAsynchronousRunner.this.pendingTasks.remove(0);
                    }
                    try {
                        if (ThreadPoolAsynchronousRunner.this.max_individual_task_time > 0) {
                            this.setMaxIndividualTaskTimeEnforcer();
                        }
                        object.run();
                    }
                    catch (RuntimeException runtimeException) {
                        if (!logger.isLoggable(MLevel.WARNING)) continue;
                        logger.log(MLevel.WARNING, this + " -- caught unexpected Exception while executing posted task.", runtimeException);
                    }
                    finally {
                        if (this.maxIndividualTaskTimeEnforcer != null) {
                            this.cancelMaxIndividualTaskTimeEnforcer();
                            l ^= l << 21;
                            l ^= l >>> 35;
                            l ^= l << 4;
                            if (l % 500L == 0L) {
                                this.purgeTimer();
                            }
                        }
                        threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
                        synchronized (threadPoolAsynchronousRunner) {
                            if (this.should_stop) {
                                return;
                            }
                            if (ThreadPoolAsynchronousRunner.this.available != null && !ThreadPoolAsynchronousRunner.this.available.add(this)) {
                                throw new InternalError("An apparently available PoolThread tried to check itself in!!!");
                            }
                            this.currentTask = null;
                        }
                    }
                }
                catch (InterruptedException interruptedException) {
                    ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
                    synchronized (threadPoolAsynchronousRunner) {
                        ThreadPoolAsynchronousRunner.this.shuttingDown(this);
                        return;
                    }
                }
                catch (RuntimeException runtimeException) {
                    if (!logger.isLoggable(MLevel.WARNING)) throw runtimeException;
                    logger.log(MLevel.WARNING, "An unexpected RuntimException is implicated in the closing of " + this, runtimeException);
                    throw runtimeException;
                }
                catch (Error error) {
                    if (!logger.isLoggable(MLevel.WARNING)) throw error;
                    logger.log(MLevel.WARNING, "An Error forced the closing of " + this + ". Will attempt to reconstruct, but this might mean that something bad is happening.", error);
                    throw error;
                }
            }
            finally {
                ThreadPoolAsynchronousRunner threadPoolAsynchronousRunner = ThreadPoolAsynchronousRunner.this;
                synchronized (threadPoolAsynchronousRunner) {
                    ThreadPoolAsynchronousRunner.this.shuttingDown(this);
                }
            }
        }
    }
}

