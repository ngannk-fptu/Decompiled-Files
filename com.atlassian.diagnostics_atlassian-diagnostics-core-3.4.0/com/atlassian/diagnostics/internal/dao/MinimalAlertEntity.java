/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.dao;

import java.time.Instant;
import javax.annotation.Nonnull;

public interface MinimalAlertEntity {
    public int getDetailsJsonLength();

    public long getId();

    @Nonnull
    public String getIssueId();

    @Nonnull
    public String getNodeName();

    @Nonnull
    public String getTriggerPluginKey();

    @Nonnull
    public Instant getTimestamp();
}

