/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.task;

import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;

public interface MonitoredCallable<T, M extends MutableTaskMonitor<T>>
extends Callable<T> {
    @Nonnull
    public M getMonitor();
}

