/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.jobs;

import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.jobs.AbstractJob;
import com.atlassian.pats.web.filter.LastAccessedTimeBatcher;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastAccessedTimeBatcherJob
extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(LastAccessedTimeBatcherJob.class);
    private final LastAccessedTimeBatcher accessedTimeBatcher;
    private final TokenRepository tokenRepository;

    public LastAccessedTimeBatcherJob(SchedulerService schedulerService, LastAccessedTimeBatcher accessedTimeBatcher, TokenRepository tokenRepository) {
        super(schedulerService);
        this.accessedTimeBatcher = accessedTimeBatcher;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doJob() {
        Map<Long, Instant> batch = this.accessedTimeBatcher.collect();
        if (!batch.isEmpty()) {
            logger.trace("Stamping tokens with last accessed times: [{}]", batch);
            Function<DatabaseConnection, Long> updateClause = this.createUpdateFromBatch(batch);
            OnRollback rollback = () -> logger.error("Caught error updating last update time for token batch: [{}]", (Object)batch);
            long results = this.tokenRepository.executeQuery(PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION, updateClause, rollback);
            logger.debug("Updated number tokens: [{}]", (Object)results);
        }
    }

    private Function<DatabaseConnection, Long> createUpdateFromBatch(Map<Long, Instant> batch) {
        return connection -> {
            SQLUpdateClause sqlUpdateClause = connection.update(Tables.TOKEN);
            batch.forEach((tokenId, lastAccessedTime) -> ((SQLUpdateClause)sqlUpdateClause.set((Path)Tables.TOKEN.lastAccessedAt, Timestamp.from(lastAccessedTime))).where((Predicate)Tables.TOKEN.id.eq((Long)tokenId)).addBatch());
            return sqlUpdateClause.execute();
        };
    }

    @Override
    protected Schedule getSchedule() {
        long intervalInMillis = (long)SystemProperty.LAST_USED_UPDATE_INTERVAL_MINS.getValue().intValue() * 60L * 1000L;
        long firstRunTime = System.currentTimeMillis() + intervalInMillis;
        return Schedule.forInterval((long)intervalInMillis, (Date)new Date(firstRunTime));
    }

    @Override
    protected RunMode getRunMode() {
        return RunMode.RUN_LOCALLY;
    }
}

