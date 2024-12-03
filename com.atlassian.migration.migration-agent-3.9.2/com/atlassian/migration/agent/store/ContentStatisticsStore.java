/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import java.util.Collection;

public interface ContentStatisticsStore {
    public ContentSummary loadContentSummary();

    public Collection<SpaceStats> loadSpaceStatistics(Collection<String> var1);

    public SpaceStats loadSpaceStatistics(String var1);
}

