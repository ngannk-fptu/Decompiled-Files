/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.apache.commons.lang3.tuple.Triple
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.migration.agent.entity.SpaceStatistic;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Generated;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatisticCalculationProcessor
implements RowProcessor {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticCalculationProcessor.class);
    private final List<SpaceStatistic> result = new ArrayList<SpaceStatistic>();
    private final boolean includeTeamCalendar;
    private final Function<Triple<Long, Long, Long>, Long> computeMigrationTime;

    public SpaceStatisticCalculationProcessor(Function<Triple<Long, Long, Long>, Long> computeMigrationTime, boolean includeTeamCalendar) {
        this.computeMigrationTime = computeMigrationTime;
        this.includeTeamCalendar = includeTeamCalendar;
    }

    @Override
    public void process(ResultSet resultSet) {
        try {
            long spaceId = resultSet.getLong("spaceId");
            long sumOfPageBlogDraftCount = resultSet.getLong("sumOfPageBlogDraftCount");
            long attachmentCount = resultSet.getLong("attachmentCount");
            long teamCalendarCount = this.includeTeamCalendar ? resultSet.getLong("teamCalendarCount") : 0L;
            long attachmentSize = resultSet.getLong("attachmentSize");
            Timestamp lastUpdated = resultSet.getTimestamp("lastUpdated");
            Timestamp lastCalculated = resultSet.getTimestamp("lastCalculated");
            this.result.add(new SpaceStatistic(spaceId, sumOfPageBlogDraftCount, attachmentCount, teamCalendarCount, attachmentSize, lastUpdated == null ? null : lastUpdated.toInstant(), lastCalculated.toInstant(), this.computeMigrationTime.apply((Triple<Long, Long, Long>)Triple.of((Object)sumOfPageBlogDraftCount, (Object)attachmentSize, (Object)teamCalendarCount))));
        }
        catch (SQLException e) {
            log.error("Error in processing space statistic calculation result", (Throwable)e);
            throw new RuntimeException("Error in processing space statistic calculation result", e);
        }
    }

    @Generated
    public List<SpaceStatistic> getResult() {
        return this.result;
    }
}

