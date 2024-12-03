/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.jobs;

import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.NotificationState;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.events.TokenEventPublisher;
import com.atlassian.pats.jobs.AbstractJob;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpiryDateTokenCheckEventJob
extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(ExpiryDateTokenCheckEventJob.class);
    private final TokenEventPublisher tokenEventPublisher;
    private final Clock utcClock;
    private final TokenRepository tokenRepository;

    public ExpiryDateTokenCheckEventJob(SchedulerService schedulerService, Clock utcClock, TokenEventPublisher tokenEventPublisher, TokenRepository tokenRepository) {
        super(schedulerService);
        this.utcClock = utcClock;
        this.tokenEventPublisher = tokenEventPublisher;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doJob() {
        logger.info("Checking tokens expiry date");
        this.publishEventsAboutExpiredTokens();
        this.publishEventsAboutTokensThatWillExpire();
    }

    private void publishEventsAboutExpiredTokens() {
        OnRollback rollback;
        Function<DatabaseConnection, List<TokenDTO>> clause = this.findExpiringTokensClause(Timestamp.from(this.utcClock.instant()), (Collection<NotificationState>)ImmutableList.of((Object)((Object)NotificationState.NOT_SENT), (Object)((Object)NotificationState.EXPIRE_SOON_SENT)));
        List<TokenDTO> expiredTokens = this.tokenRepository.executeQuery(PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION, clause, rollback = () -> logger.error("Caught error fetching expired tokens"));
        if (!expiredTokens.isEmpty()) {
            this.updateTokensState(expiredTokens, NotificationState.EXPIRED_SENT);
            expiredTokens.forEach(token -> this.tokenEventPublisher.tokenExpiredEvent((TokenDTO)token, null));
        }
    }

    private void publishEventsAboutTokensThatWillExpire() {
        OnRollback rollback;
        Function<DatabaseConnection, List<TokenDTO>> clause = this.findExpiringTokensClause(Timestamp.from(this.utcClock.instant().plus(Duration.ofDays(SystemProperty.EXPIRY_WARNING_DAYS.getValue().intValue()))), Collections.singletonList(NotificationState.NOT_SENT));
        List<TokenDTO> expireSoonTokens = this.tokenRepository.executeQuery(PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION, clause, rollback = () -> logger.error("Caught error fetching expired tokens"));
        if (!expireSoonTokens.isEmpty()) {
            this.updateTokensState(expireSoonTokens, NotificationState.EXPIRE_SOON_SENT);
            expireSoonTokens.forEach(token -> this.tokenEventPublisher.tokenExpireSoonEvent((TokenDTO)token, null));
        }
    }

    private void updateTokensState(List<TokenDTO> tokens, NotificationState notificationState) {
        Function<DatabaseConnection, Long> updateClause = this.updateTokensStateClause(tokens, notificationState);
        OnRollback rollback = () -> logger.error("Caught error updating notification state for tokens: [{}]", (Object)tokens);
        this.tokenRepository.executeQuery(PocketKnifeQuerydslPredicateExecutor.TransactionType.IN_TRANSACTION, updateClause, rollback);
    }

    private Function<DatabaseConnection, List<TokenDTO>> findExpiringTokensClause(Timestamp expiryDate, Collection<NotificationState> notificationStates) {
        return connection -> ((SQLQuery)((SQLQuery)connection.from(Tables.TOKEN).where(Tables.TOKEN.expiringAt.before(expiryDate))).where(Tables.TOKEN.notificationState.in(notificationStates))).fetch();
    }

    private Function<DatabaseConnection, Long> updateTokensStateClause(List<TokenDTO> tokens, NotificationState newNotificationState) {
        return connection -> Lists.partition((List)tokens, (int)100).stream().mapToLong(tokensBatch -> ((SQLUpdateClause)connection.update(Tables.TOKEN).set((Path)Tables.TOKEN.notificationState, (Object)newNotificationState)).where((Predicate)Tables.TOKEN.id.in(Lists.transform((List)tokensBatch, TokenDTO::getId))).execute()).sum();
    }

    @Override
    protected RunMode getRunMode() {
        return RunMode.RUN_ONCE_PER_CLUSTER;
    }

    @Override
    protected Schedule getSchedule() {
        return Schedule.forCronExpression((String)SystemProperty.EXPIRY_CHECK_SCHEDULE_CRON.getValue());
    }
}

