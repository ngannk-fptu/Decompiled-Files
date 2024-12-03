/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.SpaceStatistic;
import com.atlassian.migration.agent.store.SpaceStatisticStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatisticStoreImpl
implements SpaceStatisticStore {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticStoreImpl.class);
    @VisibleForTesting
    static final String JPQL_SPACE_STATISTIC_COUNT_QUERY = "select count(spaceStatistic.spaceId) from SpaceStatistic spaceStatistic";
    @VisibleForTesting
    static final String JPQL_SPACE_STATISTIC_WITH_NULL_TEAM_CALENDAR_COUNT = "select count(spaceStatistic.spaceId) from SpaceStatistic spaceStatistic where spaceStatistic.teamCalendarCount IS NULL";
    private final EntityManagerTemplate entityManagerTemplate;

    public SpaceStatisticStoreImpl(EntityManagerTemplate entityManagerTemplate) {
        this.entityManagerTemplate = entityManagerTemplate;
    }

    @Override
    public boolean isSpaceStatisticEmpty() {
        return this.entityManagerTemplate.query(Long.class, JPQL_SPACE_STATISTIC_COUNT_QUERY).single() == 0L;
    }

    @Override
    public long countSpaceEntriesInSpaceStatistic() {
        return this.entityManagerTemplate.query(Long.class, JPQL_SPACE_STATISTIC_COUNT_QUERY).single();
    }

    @Override
    public Optional<SpaceStatistic> getSpaceStatisticFor(long spaceId) {
        return this.entityManagerTemplate.query(SpaceStatistic.class, "select spaceStatistic from SpaceStatistic spaceStatistic where spaceStatistic.spaceId = :spaceId").param("spaceId", (Object)spaceId).first();
    }

    @Override
    public void upsert(SpaceStatistic spaceStatistic) {
        if (this.getSpaceStatisticFor(spaceStatistic.getSpaceId()).isPresent()) {
            this.entityManagerTemplate.merge(spaceStatistic);
        } else {
            this.entityManagerTemplate.persist(spaceStatistic);
        }
    }

    @Override
    public long countSpaceStatisticWithEmptyTeamCalendarEntry() {
        return this.entityManagerTemplate.query(Long.class, JPQL_SPACE_STATISTIC_WITH_NULL_TEAM_CALENDAR_COUNT).single();
    }

    @Override
    public List<Long> getDeletedSpacesWithStatistics() {
        return this.entityManagerTemplate.query(Long.class, "select spaceStatistic.spaceId from SpaceStatistic spaceStatistic left join Space space on space.id = spaceStatistic.spaceId where space IS NULL").list();
    }

    @Override
    public void deleteAllSpaceStatisticsRecords() {
        this.entityManagerTemplate.query("delete from SpaceStatistic").update();
    }

    @Override
    public void deleteSpaceStatisticsForSpacesWithIds(List<Long> spaceIds) {
        this.entityManagerTemplate.query("delete from SpaceStatistic where spaceId in :spaceIds").param("spaceIds", spaceIds).update();
    }
}

