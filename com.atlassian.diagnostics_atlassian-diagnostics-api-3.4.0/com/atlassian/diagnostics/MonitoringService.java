/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.AlertWithElisions;
import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PluginDetails;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public interface MonitoringService {
    @Deprecated
    @Nonnull
    public ComponentMonitor createMonitor(@Nonnull String var1, @Nonnull String var2);

    @Nonnull
    public ComponentMonitor createMonitor(@Nonnull String var1, @Nonnull String var2, @Nonnull MonitorConfiguration var3);

    public boolean destroyMonitor(@Nonnull String var1);

    @Nonnull
    public Set<Component> findAllComponents();

    @Nonnull
    public Set<Issue> findAllIssues();

    @Nonnull
    public Set<String> findAllNodesWithAlerts();

    @Nonnull
    public Set<PluginDetails> findAllPluginsWithAlerts();

    @Nonnull
    public Optional<ComponentMonitor> getMonitor(@Nonnull String var1);

    public boolean isEnabled();

    @Nonnull
    public String subscribe(@Nonnull AlertListener var1);

    public <T> T streamAlerts(@Nonnull AlertCriteria var1, @Nonnull PageCallback<? super Alert, T> var2, @Nonnull PageRequest var3);

    public <T> T streamAlertCounts(@Nonnull AlertCriteria var1, @Nonnull PageCallback<? super AlertCount, T> var2, @Nonnull PageRequest var3);

    public <T> T streamAlertsWithElisions(@Nonnull AlertCriteria var1, @Nonnull PageCallback<? super AlertWithElisions, T> var2, @Nonnull PageRequest var3);

    public boolean unsubscribe(@Nonnull String var1);
}

