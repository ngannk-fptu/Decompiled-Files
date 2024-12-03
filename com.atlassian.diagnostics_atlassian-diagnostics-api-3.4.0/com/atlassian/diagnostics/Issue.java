/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.Severity;
import javax.annotation.Nonnull;

public interface Issue {
    @Nonnull
    public Component getComponent();

    @Nonnull
    public String getDescription();

    @Nonnull
    public String getId();

    @Nonnull
    public <T> JsonMapper<T> getJsonMapper();

    @Nonnull
    public Severity getSeverity();

    @Nonnull
    public String getSummary();
}

