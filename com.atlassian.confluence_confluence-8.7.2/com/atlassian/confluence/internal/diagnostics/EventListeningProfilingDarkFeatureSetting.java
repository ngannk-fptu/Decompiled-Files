/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.MetricsFilter
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.diagnostics.EventListeningDarkFeatureSetting;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.MetricsFilter;

@Internal
public class EventListeningProfilingDarkFeatureSetting
extends EventListeningDarkFeatureSetting {
    public static final String ITA_DENY_DARK_FEATURE = "com.atlassian.profiling.ita.metrics.deny";
    @VisibleForTesting
    static final MetricsFilter DENY_ITA_METRICS = MetricsFilter.deny((String[])new String[]{"macro.render", "cache.removeAll", "cacheManager.flushAll", "cachedReference.reset", "cluster.lock.held.duration", "cluster.lock.waited.duration", "db.ao.entityManager.count", "db.ao.entityManager.create", "db.ao.entityManager.delete", "db.ao.entityManager.deleteWithSQL", "db.ao.entityManager.find", "db.ao.entityManager.get", "db.ao.entityManager.stream", "db.ao.executeInTransaction", "db.ao.upgradeTask", "db.sal.transactionalExecutor", "http.rest.request", "http.sal.request", "longRunningTask", "plugin.disabled.counter", "plugin.enabled.counter", "index.reindex", "task", "web.fragment.condition", "web.resource.condition", "web.resource.transform", "webTemplateRenderer", "search.manager", "noisy-neighbor.LargeNumberOfTimersOperation"});

    public EventListeningProfilingDarkFeatureSetting(EventPublisher eventPublisher, DarkFeaturesManager darkFeaturesManager) {
        super(eventPublisher, darkFeaturesManager, ITA_DENY_DARK_FEATURE);
    }

    @Override
    protected void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Metrics.getConfiguration().setFilter(enabled ? DENY_ITA_METRICS : MetricsFilter.ACCEPT_ALL);
    }
}

