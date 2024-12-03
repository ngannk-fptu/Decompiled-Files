/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class ProcessDestroyer
implements Runnable {
    private static final int THREAD_DIE_TIMEOUT = 20000;
    private final Set<Process> processes = new HashSet<Process>();
    private Method addShutdownHookMethod;
    private Method removeShutdownHookMethod;
    private ProcessDestroyerImpl destroyProcessThread = null;
    private boolean added = false;
    private boolean running = false;

    ProcessDestroyer() {
        try {
            this.addShutdownHookMethod = Runtime.class.getMethod("addShutdownHook", Thread.class);
            this.removeShutdownHookMethod = Runtime.class.getMethod("removeShutdownHook", Thread.class);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addShutdownHook() {
        if (this.addShutdownHookMethod != null && !this.running) {
            this.destroyProcessThread = new ProcessDestroyerImpl();
            try {
                this.addShutdownHookMethod.invoke((Object)Runtime.getRuntime(), this.destroyProcessThread);
                this.added = true;
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t != null && t.getClass() == IllegalStateException.class) {
                    this.running = true;
                }
                e.printStackTrace();
            }
        }
    }

    private void removeShutdownHook() {
        if (this.removeShutdownHookMethod != null && this.added && !this.running) {
            try {
                if (!Boolean.TRUE.equals(this.removeShutdownHookMethod.invoke((Object)Runtime.getRuntime(), this.destroyProcessThread))) {
                    System.err.println("Could not remove shutdown hook");
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t != null && t.getClass() == IllegalStateException.class) {
                    this.running = true;
                }
                e.printStackTrace();
            }
            this.destroyProcessThread.setShouldDestroy(false);
            if (!this.destroyProcessThread.getThreadGroup().isDestroyed()) {
                this.destroyProcessThread.start();
            }
            try {
                this.destroyProcessThread.join(20000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.destroyProcessThread = null;
            this.added = false;
        }
    }

    public boolean isAddedAsShutdownHook() {
        return this.added;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Process process) {
        Set<Process> set = this.processes;
        synchronized (set) {
            if (this.processes.isEmpty()) {
                this.addShutdownHook();
            }
            return this.processes.add(process);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Process process) {
        Set<Process> set = this.processes;
        synchronized (set) {
            boolean processRemoved = this.processes.remove(process);
            if (processRemoved && this.processes.isEmpty()) {
                this.removeShutdownHook();
            }
            return processRemoved;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Set<Process> set = this.processes;
        synchronized (set) {
            this.running = true;
            this.processes.forEach(Process::destroy);
        }
    }

    private class ProcessDestroyerImpl
    extends Thread {
        private boolean shouldDestroy;

        public ProcessDestroyerImpl() {
            super("ProcessDestroyer Shutdown Hook");
            this.shouldDestroy = true;
        }

        @Override
        public void run() {
            if (this.shouldDestroy) {
                ProcessDestroyer.this.run();
            }
        }

        public void setShouldDestroy(boolean shouldDestroy) {
            this.shouldDestroy = shouldDestroy;
        }
    }
}

