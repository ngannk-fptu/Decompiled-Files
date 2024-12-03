/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import com.atlassian.diagnostics.internal.dao.AlertMetric;
import com.atlassian.diagnostics.internal.dao.MinimalAlertEntity;
import com.atlassian.diagnostics.internal.dao.RowCallback;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AlertEntityDao {
    public void deleteAll(@Nonnull AlertCriteria var1);

    public Set<String> findAllComponentIds();

    public Map<String, Severity> findAllIssueIds();

    public Set<String> findAllNodeNames();

    public Set<String> findAllPluginKeys();

    @Nullable
    public AlertEntity getById(long var1);

    @Nonnull
    public AlertEntity save(@Nonnull Alert var1);

    public void streamAll(@Nonnull AlertCriteria var1, @Nonnull RowCallback<AlertEntity> var2, @Nonnull PageRequest var3);

    public void streamByIds(@Nonnull Collection<Long> var1, @Nonnull RowCallback<AlertEntity> var2);

    public void streamMetrics(@Nonnull AlertCriteria var1, @Nonnull RowCallback<AlertMetric> var2, @Nonnull PageRequest var3);

    public void streamMinimalAlerts(@Nonnull AlertCriteria var1, @Nonnull RowCallback<MinimalAlertEntity> var2, @Nonnull PageRequest var3);
}

