/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.Severity;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface IssueSupplier {
    @Nonnull
    public Issue getIssue(@Nonnull String var1, @Nonnull Severity var2);
}

