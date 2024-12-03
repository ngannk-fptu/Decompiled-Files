/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

public class ExecuteWatchdog
implements TimeoutObserver {
    private Process process;
    private volatile boolean watch = false;
    private Exception caught = null;
    private volatile boolean killedProcess = false;
    private Watchdog watchdog;

    public ExecuteWatchdog(long timeout) {
        this.watchdog = new Watchdog(timeout);
        this.watchdog.addTimeoutObserver(this);
    }

    @Deprecated
    public ExecuteWatchdog(int timeout) {
        this((long)timeout);
    }

    public synchronized void start(Process process) {
        if (process == null) {
            throw new NullPointerException("process is null.");
        }
        if (this.process != null) {
            throw new IllegalStateException("Already running.");
        }
        this.caught = null;
        this.killedProcess = false;
        this.watch = true;
        this.process = process;
        this.watchdog.start();
    }

    public synchronized void stop() {
        this.watchdog.stop();
        this.cleanUp();
    }

    @Override
    public synchronized void timeoutOccured(Watchdog w) {
        try {
            try {
                this.process.exitValue();
            }
            catch (IllegalThreadStateException itse) {
                if (this.watch) {
                    this.killedProcess = true;
                    this.process.destroy();
                }
            }
        }
        catch (Exception e) {
            this.caught = e;
        }
        finally {
            this.cleanUp();
        }
    }

    protected synchronized void cleanUp() {
        this.watch = false;
        this.process = null;
    }

    public synchronized void checkException() throws BuildException {
        if (this.caught != null) {
            throw new BuildException("Exception in ExecuteWatchdog.run: " + this.caught.getMessage(), this.caught);
        }
    }

    public boolean isWatching() {
        return this.watch;
    }

    public boolean killedProcess() {
        return this.killedProcess;
    }
}

