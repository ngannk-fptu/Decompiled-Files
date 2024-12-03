/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.SpaceStatistic;
import java.util.List;
import java.util.Optional;

public interface SpaceStatisticStore {
    public boolean isSpaceStatisticEmpty();

    public long countSpaceEntriesInSpaceStatistic();

    public Optional<SpaceStatistic> getSpaceStatisticFor(long var1);

    public void upsert(SpaceStatistic var1);

    public long countSpaceStatisticWithEmptyTeamCalendarEntry();

    public List<Long> getDeletedSpacesWithStatistics();

    public void deleteAllSpaceStatisticsRecords();

    public void deleteSpaceStatisticsForSpacesWithIds(List<Long> var1);
}

