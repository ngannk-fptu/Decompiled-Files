/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.Severity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IssueBuilder {
    @Nonnull
    public Issue build();

    @Nonnull
    public IssueBuilder descriptionI18nKey(@Nonnull String var1);

    @Nonnull
    public IssueBuilder jsonMapper(@Nullable JsonMapper var1);

    @Nonnull
    public IssueBuilder severity(@Nonnull Severity var1);

    @Nonnull
    public IssueBuilder summaryI18nKey(@Nonnull String var1);
}

