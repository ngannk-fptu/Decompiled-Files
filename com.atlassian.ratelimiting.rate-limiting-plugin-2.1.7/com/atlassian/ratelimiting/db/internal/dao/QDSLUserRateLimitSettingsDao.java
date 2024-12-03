/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.ratelimiting.dao.DefaultUserRateLimitSettings;
import com.atlassian.ratelimiting.dao.RateLimitingSettingsVersionDao;
import com.atlassian.ratelimiting.dao.UserRateLimitSettingsDao;
import com.atlassian.ratelimiting.db.internal.dao.Tables;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchRequest;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.ratelimiting.page.Pages;
import com.atlassian.sal.api.user.UserKey;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class QDSLUserRateLimitSettingsDao
implements UserRateLimitSettingsDao {
    private static final Logger logger = LoggerFactory.getLogger(QDSLUserRateLimitSettingsDao.class);
    private static final int NULL_PLACEHOLDER_VALUE = -1;
    private static final TokenBucketSettings DEFAULT_BUCKET_VALUES = new TokenBucketSettings(-1, -1, 1, ChronoUnit.SECONDS);
    private final DatabaseAccessor databaseAccessor;
    private final RateLimitingSettingsVersionDao settingsVersionDao;

    public QDSLUserRateLimitSettingsDao(DatabaseAccessor databaseAccessor, RateLimitingSettingsVersionDao settingsVersionDao) {
        this.databaseAccessor = databaseAccessor;
        this.settingsVersionDao = settingsVersionDao;
    }

    @Override
    public Optional<UserRateLimitSettings> get(UserKey userKey) {
        logger.debug("Looking for user settings for userId: [{}]", (Object)userKey.getStringValue());
        Tuple tuple = this.databaseAccessor.runInTransaction(databaseConnection -> (Tuple)((SQLQuery)((SQLQuery)databaseConnection.select(Tables.RL_USER_SETTINGS.all()).from((Expression<?>)Tables.RL_USER_SETTINGS)).where(Tables.RL_USER_SETTINGS.USER_ID.eq(userKey.getStringValue()))).fetchFirst(), OnRollback.NOOP);
        return Objects.isNull(tuple) ? Optional.empty() : Optional.of(this.getUserRateLimitSettings(tuple));
    }

    private UserRateLimitSettings getUserRateLimitSettings(Tuple tuple) {
        TokenBucketSettings bucketSettings = new TokenBucketSettings(tuple.get(Tables.RL_USER_SETTINGS.CAPACITY), tuple.get(Tables.RL_USER_SETTINGS.FILL_RATE), tuple.get(Tables.RL_USER_SETTINGS.INTERVAL_FREQUENCY), ChronoUnit.valueOf(tuple.get(Tables.RL_USER_SETTINGS.INTERVAL_TIME_UNIT)));
        DefaultUserRateLimitSettings.Builder builder = DefaultUserRateLimitSettings.builder(new UserKey(tuple.get(Tables.RL_USER_SETTINGS.USER_ID))).withSettings(bucketSettings);
        return Boolean.TRUE.equals(tuple.get(Tables.RL_USER_SETTINGS.WHITELISTED)) ? builder.whitelisted().build() : builder.build();
    }

    @Override
    public List<UserRateLimitSettings> findAll() {
        QueryResults<Tuple> results = this.findUsers(null, null);
        return results.getResults().stream().map(this::getUserRateLimitSettings).collect(Collectors.toList());
    }

    @Override
    public Page<UserRateLimitSettings> search(UserRateLimitSettingsSearchRequest searchRequest, PageRequest pageRequest) {
        Objects.requireNonNull(searchRequest, "request");
        Objects.requireNonNull(pageRequest, "pageRequest");
        if (!Objects.isNull(searchRequest.getFilter()) && !searchRequest.getFilter().isEmpty()) {
            return this.findAll(pageRequest, searchRequest.getFilter());
        }
        return this.findAll(pageRequest);
    }

    @Override
    public UserRateLimitSettings saveOrUpdate(UserRateLimitSettings userRateLimitSettings) {
        Optional<UserRateLimitSettings> found = this.get(userRateLimitSettings.getUserKey());
        return !found.isPresent() ? this.create(userRateLimitSettings) : this.update(userRateLimitSettings);
    }

    UserRateLimitSettings create(UserRateLimitSettings userRateLimitSettings) {
        logger.debug("Creating User Rate Limiting Settings: [{}]", (Object)userRateLimitSettings);
        String createdKey = this.databaseAccessor.runInTransaction(databaseConnection -> {
            String newKey = ((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)databaseConnection.insert(Tables.RL_USER_SETTINGS).set((Path)Tables.RL_USER_SETTINGS.CAPACITY, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getCapacity())).set((Path)Tables.RL_USER_SETTINGS.FILL_RATE, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getFillRate())).set((Path)Tables.RL_USER_SETTINGS.INTERVAL_FREQUENCY, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getIntervalFrequency())).set((Path)Tables.RL_USER_SETTINGS.INTERVAL_TIME_UNIT, userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getIntervalTimeUnit().name())).set((Path)Tables.RL_USER_SETTINGS.USER_ID, userRateLimitSettings.getUserKey().getStringValue())).set((Path)Tables.RL_USER_SETTINGS.WHITELISTED, (Object)userRateLimitSettings.isWhitelisted())).executeWithKey(Tables.RL_USER_SETTINGS.USER_ID);
            this.settingsVersionDao.incrementUserSettingsVersion();
            return newKey;
        }, () -> logger.error("Caught error creating user rate limiting settings: [{}] into DB - rolling back transaction!", (Object)userRateLimitSettings));
        logger.debug("Returning created entity: [{}]", (Object)createdKey);
        return userRateLimitSettings;
    }

    private UserRateLimitSettings update(UserRateLimitSettings userRateLimitSettings) {
        logger.debug("Updating user rate limiting settings: [{}]", (Object)userRateLimitSettings);
        long updatedCount = this.databaseAccessor.runInTransaction(databaseConnection -> {
            long count = ((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)databaseConnection.update(Tables.RL_USER_SETTINGS).set((Path)Tables.RL_USER_SETTINGS.CAPACITY, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getCapacity())).set((Path)Tables.RL_USER_SETTINGS.FILL_RATE, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getFillRate())).set((Path)Tables.RL_USER_SETTINGS.INTERVAL_FREQUENCY, (Object)userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getIntervalFrequency())).set((Path)Tables.RL_USER_SETTINGS.INTERVAL_TIME_UNIT, userRateLimitSettings.getSettings().orElse(DEFAULT_BUCKET_VALUES).getIntervalTimeUnit().name())).set((Path)Tables.RL_USER_SETTINGS.WHITELISTED, (Object)userRateLimitSettings.isWhitelisted())).where((Predicate)Tables.RL_USER_SETTINGS.USER_ID.eq(userRateLimitSettings.getUserKey().getStringValue())).execute();
            this.settingsVersionDao.incrementUserSettingsVersion();
            return count;
        }, () -> logger.error("Caught error updating user rate limiting settings: [{}] into DB - rolling back transaction!", (Object)userRateLimitSettings));
        logger.debug("Returning updated entities: [{}]", (Object)updatedCount);
        return userRateLimitSettings;
    }

    @Override
    public void delete(UserKey userKey) {
        logger.debug("Deleting rate limiting exemptions for userKey: [{}]", (Object)userKey);
        long deletedCount = this.databaseAccessor.runInTransaction(databaseConnection -> {
            long count = databaseConnection.delete(Tables.RL_USER_SETTINGS).where((Predicate)Tables.RL_USER_SETTINGS.USER_ID.eq(userKey.getStringValue())).execute();
            this.settingsVersionDao.incrementUserSettingsVersion();
            return count;
        }, () -> logger.error("Caught error deleting rate limiting settings for user: [{}] from DB - rolling back transaction!", (Object)userKey));
        logger.debug("Deleted num entities: [{}]", (Object)deletedCount);
    }

    private Page<UserRateLimitSettings> findAll(PageRequest pageRequest) {
        return this.findAll(pageRequest, Collections.emptyList());
    }

    private Page<UserRateLimitSettings> findAll(PageRequest pageRequest, List<String> userIds) {
        QueryResults<Tuple> results = this.findUsers(pageRequest, userIds);
        List list = results.getResults().stream().map(this::getUserRateLimitSettings).collect(Collectors.toList());
        return Pages.createPage(list, pageRequest, (int)results.getTotal());
    }

    private QueryResults<Tuple> findUsers(PageRequest pageRequest, List<String> userIds) {
        return this.databaseAccessor.runInTransaction(databaseConnection -> {
            SQLQuery sqlQuery = (SQLQuery)databaseConnection.select(Tables.RL_USER_SETTINGS.all()).from((Expression<?>)Tables.RL_USER_SETTINGS);
            if (!CollectionUtils.isEmpty((Collection)userIds)) {
                sqlQuery.where(Tables.RL_USER_SETTINGS.USER_ID.in(userIds));
            }
            if (Objects.nonNull(pageRequest)) {
                ((SQLQuery)sqlQuery.offset(pageRequest.getOffset())).limit(pageRequest.getSize());
            }
            return ((SQLQuery)sqlQuery.orderBy((OrderSpecifier<?>)Tables.RL_USER_SETTINGS.USER_ID.asc())).fetchResults();
        }, OnRollback.NOOP);
    }

    @Override
    public Optional<Long> getLatestUserSettingsVersion() {
        return this.settingsVersionDao.getLatestUserSettingsVersion();
    }

    @Override
    public long getExemptionsCount() {
        return this.databaseAccessor.runInTransaction(databaseConnection -> ((SQLQuery)databaseConnection.select(Tables.RL_USER_SETTINGS.USER_ID).from((Expression<?>)Tables.RL_USER_SETTINGS)).fetchCount(), OnRollback.NOOP);
    }
}

