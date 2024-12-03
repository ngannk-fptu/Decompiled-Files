/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.jmx;

import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IssueAlertsMXBean {
    @Nonnull
    public String getComponent();

    public long getCount();

    @Nonnull
    public String getDescription();

    @Nullable
    public Date getLatestAlertTimestamp();

    @Nonnull
    public String getSeverity();

    public void reset();
}

