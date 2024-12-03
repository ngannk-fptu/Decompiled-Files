/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.Severity;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AlertEntity {
    @Nullable
    public String getDetailsJson();

    public long getId();

    @Nonnull
    public String getIssueId();

    @Nonnull
    public String getIssueComponentId();

    @Nonnull
    public Severity getIssueSeverity();

    @Nonnull
    public String getNodeName();

    @Nullable
    public String getTriggerModule();

    @Nonnull
    public String getTriggerPluginKey();

    @Nullable
    public String getTriggerPluginVersion();

    @Nonnull
    public Instant getTimestamp();
}

