/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task.longrunning;

public interface LongRunningTask
extends Runnable {
    public int getPercentageComplete();

    public String getName();

    public String getNameKey();

    public String getCurrentStatus();

    public long getElapsedTime();

    public String getPrettyElapsedTime();

    public long getEstimatedTimeRemaining();

    public String getPrettyTimeRemaining();

    public boolean isComplete();

    public boolean isSuccessful();
}

