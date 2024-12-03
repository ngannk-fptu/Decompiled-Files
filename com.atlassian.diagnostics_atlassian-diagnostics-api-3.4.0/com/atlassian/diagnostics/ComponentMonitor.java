/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.AlertRequest;
import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.IssueBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ComponentMonitor {
    public void alert(@Nonnull AlertRequest var1);

    @Nonnull
    public IssueBuilder defineIssue(int var1);

    @Nonnull
    public Component getComponent();

    @Nonnull
    public Optional<Issue> getIssue(int var1);

    @Nonnull
    public List<Issue> getIssues();

    public boolean isEnabled();
}

