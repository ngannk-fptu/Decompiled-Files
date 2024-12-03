/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.jmx;

import java.util.Date;
import javax.annotation.Nullable;

public interface AlertsMXBean {
    public long getErrorCount();

    public long getInfoCount();

    @Nullable
    public Date getLatestAlertTimestamp();

    public long getTotalCount();

    public long getWarningCount();

    public void reset();
}

