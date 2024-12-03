/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class Retry
extends Task
implements TaskContainer {
    private Task nestedTask;
    private int retryCount = 1;
    private int retryDelay = 0;

    @Override
    public synchronized void addTask(Task t) {
        if (this.nestedTask != null) {
            throw new BuildException("The retry task container accepts a single nested task (which may be a sequential task container)");
        }
        this.nestedTask = t;
    }

    public void setRetryCount(int n) {
        this.retryCount = n;
    }

    public void setRetryDelay(int retryDelay) {
        if (retryDelay < 0) {
            throw new BuildException("retryDelay must be a non-negative number");
        }
        this.retryDelay = retryDelay;
    }

    @Override
    public void execute() throws BuildException {
        StringBuilder errorMessages = new StringBuilder();
        for (int i = 0; i <= this.retryCount; ++i) {
            try {
                this.nestedTask.perform();
                break;
            }
            catch (Exception e) {
                errorMessages.append(e.getMessage());
                if (i >= this.retryCount) {
                    throw new BuildException(String.format("Task [%s] failed after [%d] attempts; giving up.%nError messages:%n%s", this.nestedTask.getTaskName(), this.retryCount, errorMessages), this.getLocation());
                }
                String msg = this.retryDelay > 0 ? "Attempt [" + i + "]: error occurred; retrying after " + this.retryDelay + " ms..." : "Attempt [" + i + "]: error occurred; retrying...";
                this.log(msg, e, 2);
                errorMessages.append(System.lineSeparator());
                if (this.retryDelay <= 0) continue;
                try {
                    Thread.sleep(this.retryDelay);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                continue;
            }
        }
    }
}

