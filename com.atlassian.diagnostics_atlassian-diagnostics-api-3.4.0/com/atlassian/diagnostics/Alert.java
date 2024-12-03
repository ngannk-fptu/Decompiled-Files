/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface Alert {
    @Nonnull
    public Optional<Object> getDetails();

    public long getId();

    @Nonnull
    public Issue getIssue();

    @Nonnull
    public String getNodeName();

    @Nonnull
    public Instant getTimestamp();

    @Nonnull
    public AlertTrigger getTrigger();
}

