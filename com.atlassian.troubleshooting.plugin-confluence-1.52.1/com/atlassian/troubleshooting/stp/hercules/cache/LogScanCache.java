/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.hercules.cache;

import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface LogScanCache {
    @Nullable
    public LogScanMonitor get();

    public void set(@Nonnull LogScanMonitor var1);

    public void destroy();
}

