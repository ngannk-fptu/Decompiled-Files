/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.model.stats.GlobalEntitiesStats;
import com.atlassian.migration.agent.model.stats.ServerStats;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.model.stats.UsersGroupsStats;
import java.util.Collection;
import javax.annotation.Nullable;

public interface StatisticsService {
    public ServerStats loadServerStatistics();

    public Collection<SpaceStats> loadSpaceStatistics(Collection<String> var1);

    public SpaceStats loadSpaceStatistics(String var1);

    public UsersGroupsStats getUsersGroupsStatistics(UserMigrationType var1, Collection<String> var2);

    public GlobalEntitiesStats getGlobalEntitiesStatistics(@Nullable String var1);
}

