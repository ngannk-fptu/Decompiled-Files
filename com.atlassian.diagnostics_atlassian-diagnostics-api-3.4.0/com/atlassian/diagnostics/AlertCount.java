/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.PluginDetails;
import java.util.Map;
import javax.annotation.Nonnull;

public interface AlertCount {
    @Nonnull
    public Issue getIssue();

    @Nonnull
    public Map<String, Long> getCountsByNodeName();

    @Nonnull
    public PluginDetails getPlugin();

    public long getTotalCount();
}

