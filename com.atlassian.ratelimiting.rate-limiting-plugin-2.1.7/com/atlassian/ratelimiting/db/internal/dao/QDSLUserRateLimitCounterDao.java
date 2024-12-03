/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.ratelimiting.dao.UserRateLimitCounter;
import com.atlassian.ratelimiting.dao.UserRateLimitCounterDao;
import com.atlassian.ratelimiting.db.internal.dao.Tables;
import com.atlassian.ratelimiting.db.internal.dao.utils.DaoUtils;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import com.atlassian.ratelimiting.history.UserRateLimitingReport;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.Pages;
import com.atlassian.sal.api.user.UserKey;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLInsertClause;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QDSLUserRateLimitCounterDao
implements UserRateLimitCounterDao {
    private static final Logger logger = LoggerFactory.getLogger(QDSLUserRateLimitCounterDao.class);
    private static final Clock CLOCK = Clock.systemUTC();
    private static final String DEFAULT_NODE_ID = DaoUtils.getNodeId();
    private final DatabaseAccessor databaseAccessor;

    public QDSLUserRateLimitCounterDao(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public UserRateLimitCounter create(UserRateLimitCounter counter) {
        String nodeId = Objects.nonNull(counter.getNodeId()) ? counter.getNodeId() : DEFAULT_NODE_ID;
        Instant utcInstant = counter.getIntervalStart().atZone(CLOCK.getZone()).toInstant();
        Long newId = this.databaseAccessor.runInTransaction(databaseConnection -> ((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)databaseConnection.insert(Tables.RL_COUNTER).set((Path)Tables.RL_COUNTER.NODE_ID, nodeId)).set((Path)Tables.RL_COUNTER.USER_ID, counter.getUser().getStringValue())).set((Path)Tables.RL_COUNTER.INTERVAL_START, Date.from(utcInstant))).set((Path)Tables.RL_COUNTER.REJECT_COUNT, (Object)counter.getRejectCount())).executeWithKey(Tables.RL_COUNTER.ID), () -> logger.error("Caught error inserting counter: [{}] into DB - rolling back transaction!", (Object)counter));
        UserRateLimitCounter userRateLimitCounter = counter.copy().id(newId).nodeId(nodeId).intervalStart(LocalDateTime.ofInstant(utcInstant, CLOCK.getZone())).build();
        logger.trace("Created rate limiting Counter: [{}]", (Object)userRateLimitCounter);
        return userRateLimitCounter;
    }

    @Override
    public long deleteOlderThan(Duration duration) {
        Date timeFromWhichToDelete = DaoUtils.pastTimeDownToDurationFromNow(duration, CLOCK);
        return this.databaseAccessor.runInTransaction(databaseConnection -> databaseConnection.delete(Tables.RL_COUNTER).where((Predicate)Tables.RL_COUNTER.INTERVAL_START.lt(timeFromWhichToDelete)).execute(), () -> logger.error("Caught error deleting counters older than: [{}] from DB - rolling back transaction!", (Object)timeFromWhichToDelete));
    }

    public long count() {
        return this.databaseAccessor.runInTransaction(databaseConnection -> ((SQLQuery)databaseConnection.select(Tables.RL_COUNTER.ID).from((Expression<?>)Tables.RL_COUNTER)).fetchCount(), OnRollback.NOOP);
    }

    @Override
    public Page<UserRateLimitingReport> getAggregateCounts(RateLimitingReportSearchRequest searchRequest) {
        BooleanExpression hasExemption = (BooleanExpression)((Object)new CaseBuilder().when(Tables.RL_USER_SETTINGS.USER_ID.isNotNull()).then(true).otherwise(false));
        QueryResults results = this.databaseAccessor.runInTransaction(databaseConnection -> {
            SQLQuery sqlQuery = (SQLQuery)((SQLQuery)((SQLQuery)databaseConnection.select(Tables.RL_COUNTER.USER_ID, Tables.RL_COUNTER.REJECT_COUNT.sum(), Tables.RL_COUNTER.INTERVAL_START.max(), hasExemption).from((Expression<?>)Tables.RL_COUNTER)).leftJoin((EntityPath)Tables.RL_USER_SETTINGS)).on((Predicate)Tables.RL_COUNTER.USER_ID.eq(Tables.RL_USER_SETTINGS.USER_ID));
            if (searchRequest.isUserFilterSearchQuery()) {
                sqlQuery.where(Tables.RL_COUNTER.USER_ID.in(searchRequest.getUserFilterList()));
            }
            if (searchRequest.isDateRangeSearchQuery()) {
                sqlQuery.where(Tables.RL_COUNTER.INTERVAL_START.between(DaoUtils.getUtcStartTime(searchRequest), DaoUtils.getUtcFinishTime(searchRequest, CLOCK)));
            }
            return ((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)sqlQuery.groupBy((Expression<?>[])new Expression[]{Tables.RL_COUNTER.USER_ID, Tables.RL_USER_SETTINGS.USER_ID})).orderBy(this.applySortOrder(searchRequest))).offset(searchRequest.getPageRequest().getOffset())).limit(searchRequest.getPageRequest().getSize())).fetchResults();
        }, OnRollback.NOOP);
        List list = results.getResults().stream().map(this::mapAggregatedResults).collect(Collectors.toList());
        return Pages.createPage(list, searchRequest.getPageRequest(), (int)results.getTotal());
    }

    private OrderSpecifier<?> applySortOrder(RateLimitingReportSearchRequest searchRequest) {
        return searchRequest.isFrequencySortOrder() ? Tables.RL_COUNTER.REJECT_COUNT.sum().desc() : Tables.RL_COUNTER.INTERVAL_START.max().desc();
    }

    private UserRateLimitingReport mapAggregatedResults(Tuple tuple) {
        return new UserRateLimitingReport(new UserKey(tuple.get(0, String.class)), LocalDateTime.ofInstant(tuple.get(2, Date.class).toInstant(), CLOCK.getZone()), tuple.get(1, Long.class), tuple.get(3, Boolean.class));
    }
}

