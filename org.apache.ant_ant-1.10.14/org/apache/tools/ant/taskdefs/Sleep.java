/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Sleep
extends Task {
    private boolean failOnError = true;
    private int seconds = 0;
    private int hours = 0;
    private int minutes = 0;
    private int milliseconds = 0;

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public void doSleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    private long getSleepTime() {
        return (((long)this.hours * 60L + (long)this.minutes) * 60L + (long)this.seconds) * 1000L + (long)this.milliseconds;
    }

    public void validate() throws BuildException {
        if (this.getSleepTime() < 0L) {
            throw new BuildException("Negative sleep periods are not supported");
        }
    }

    @Override
    public void execute() throws BuildException {
        try {
            this.validate();
            long sleepTime = this.getSleepTime();
            this.log("sleeping for " + sleepTime + " milliseconds", 3);
            this.doSleep(sleepTime);
        }
        catch (Exception e) {
            if (this.failOnError) {
                throw new BuildException(e);
            }
            this.log(e.toString(), 0);
        }
    }
}

