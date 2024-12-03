/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.ratelimiting.dao.RateLimitingSettingsVersionDao;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsDao;
import com.atlassian.ratelimiting.db.internal.dao.Tables;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.google.common.annotations.VisibleForTesting;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QDSLSystemRateLimitingSettingsDao
implements SystemRateLimitingSettingsDao {
    private static final String NAME_SYSTEM = "SYSTEM";
    private static final Logger logger = LoggerFactory.getLogger(QDSLSystemRateLimitingSettingsDao.class);
    private final DatabaseAccessor databaseAccessor;
    private final RateLimitingSettingsVersionDao settingsVersionDao;

    public QDSLSystemRateLimitingSettingsDao(DatabaseAccessor databaseAccessor, RateLimitingSettingsVersionDao settingsVersionDao) {
        this.databaseAccessor = databaseAccessor;
        this.settingsVersionDao = settingsVersionDao;
    }

    @Override
    public void initializeDbIfNeeded(SystemRateLimitingSettings initialSystemRLSettings) {
        logger.debug("Checking if system rate limiting DB table needs to be initialised with default settings: [{}]", (Object)initialSystemRLSettings);
        this.databaseAccessor.runInTransaction(databaseConnection -> {
            SystemRateLimitingSettings existing = this.getSystemSettings();
            if (Objects.isNull(existing)) {
                logger.warn("Initializing system rate limiting settings DB with default settings: [{}]", (Object)initialSystemRLSettings);
                this.create(initialSystemRLSettings);
            }
            return null;
        }, () -> logger.error("Caught error initializing system rate limiting settings: [{}] in DB - rolling back transaction", (Object)initialSystemRLSettings));
    }

    @Override
    public SystemRateLimitingSettings saveOrUpdate(SystemRateLimitingSettings newSystemSettings) {
        logger.info("Saving system rate limiting settings: [{}]", (Object)newSystemSettings);
        return this.databaseAccessor.runInTransaction(databaseConnection -> {
            SystemRateLimitingSettings existing = this.getSystemSettings();
            SystemRateLimitingSettings saved = Objects.isNull(existing) ? this.create(newSystemSettings) : this.update(existing, newSystemSettings);
            logger.debug("Returning created entity: [{}]", (Object)saved);
            return saved;
        }, () -> logger.error("Caught error updating system rate limiting settings: [{}] in DB - rolling back transaction", (Object)newSystemSettings));
    }

    SystemRateLimitingSettings create(SystemRateLimitingSettings newSystemSettings) {
        logger.debug("Creating new system rate limiting settings: [{}]", (Object)newSystemSettings);
        String createdKey = this.databaseAccessor.runInTransaction(databaseConnection -> {
            String newKey = ((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)databaseConnection.insert(Tables.RL_SYSTEM_SETTINGS).set((Path)Tables.RL_SYSTEM_SETTINGS.NAME, NAME_SYSTEM)).set((Path)Tables.RL_SYSTEM_SETTINGS.MODE, newSystemSettings.getMode().name())).set((Path)Tables.RL_SYSTEM_SETTINGS.CAPACITY, (Object)newSystemSettings.getBucketSettings().getCapacity())).set((Path)Tables.RL_SYSTEM_SETTINGS.FILL_RATE, (Object)newSystemSettings.getBucketSettings().getFillRate())).set((Path)Tables.RL_SYSTEM_SETTINGS.INTERVAL_FREQUENCY, (Object)newSystemSettings.getBucketSettings().getIntervalFrequency())).set((Path)Tables.RL_SYSTEM_SETTINGS.INTERVAL_TIME_UNIT, newSystemSettings.getBucketSettings().getIntervalTimeUnit().name())).set((Path)Tables.RL_SYSTEM_SETTINGS.FLUSH_JOB_DURATION, newSystemSettings.getJobControlSettings().getBucketCollectionJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.CLEAN_JOB_DURATION, newSystemSettings.getJobControlSettings().getReportingDbArchivingJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.RETENTION_PERIOD_DURATION, newSystemSettings.getJobControlSettings().getReportingDbRetentionPeriodDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.SETTINGS_RELOAD_JOB_DURATION, newSystemSettings.getJobControlSettings().getSettingsReloadJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.REAPER_JOB_DURATION, newSystemSettings.getJobControlSettings().getBucketCleanupJobFrequencyDuration().toString())).executeWithKey(Tables.RL_SYSTEM_SETTINGS.NAME);
            this.settingsVersionDao.incrementDefaultSettingsVersion();
            return newKey;
        }, () -> logger.error("Caught error inserting system rate limiting settings: [{}] into DB - rolling back transaction", (Object)newSystemSettings));
        logger.debug("Returning created entity: [{}]", (Object)createdKey);
        return newSystemSettings;
    }

    private SystemRateLimitingSettings update(SystemRateLimitingSettings existingSystemSettings, SystemRateLimitingSettings newSystemSettings) {
        logger.debug("Updating system rate limiting settings: [{}]", (Object)newSystemSettings);
        SystemJobControlSettings existingJobSettings = existingSystemSettings.getJobControlSettings();
        SystemJobControlSettings updatedJobSettings = newSystemSettings.getJobControlSettings();
        Long updatedEntityCount = this.databaseAccessor.runInTransaction(databaseConnection -> {
            long count = ((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)databaseConnection.update(Tables.RL_SYSTEM_SETTINGS).set((Path)Tables.RL_SYSTEM_SETTINGS.MODE, newSystemSettings.getMode().name())).set((Path)Tables.RL_SYSTEM_SETTINGS.CAPACITY, (Object)newSystemSettings.getBucketSettings().getCapacity())).set((Path)Tables.RL_SYSTEM_SETTINGS.FILL_RATE, (Object)newSystemSettings.getBucketSettings().getFillRate())).set((Path)Tables.RL_SYSTEM_SETTINGS.INTERVAL_FREQUENCY, (Object)newSystemSettings.getBucketSettings().getIntervalFrequency())).set((Path)Tables.RL_SYSTEM_SETTINGS.INTERVAL_TIME_UNIT, newSystemSettings.getBucketSettings().getIntervalTimeUnit().name())).set((Path)Tables.RL_SYSTEM_SETTINGS.FLUSH_JOB_DURATION, Objects.nonNull(updatedJobSettings.getBucketCollectionJobFrequencyDuration()) ? updatedJobSettings.getBucketCollectionJobFrequencyDuration().toString() : existingJobSettings.getBucketCollectionJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.CLEAN_JOB_DURATION, Objects.nonNull(updatedJobSettings.getReportingDbArchivingJobFrequencyDuration()) ? updatedJobSettings.getReportingDbArchivingJobFrequencyDuration().toString() : existingJobSettings.getReportingDbArchivingJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.RETENTION_PERIOD_DURATION, Objects.nonNull(updatedJobSettings.getReportingDbRetentionPeriodDuration()) ? updatedJobSettings.getReportingDbRetentionPeriodDuration().toString() : existingJobSettings.getReportingDbRetentionPeriodDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.SETTINGS_RELOAD_JOB_DURATION, Objects.nonNull(updatedJobSettings.getSettingsReloadJobFrequencyDuration()) ? updatedJobSettings.getSettingsReloadJobFrequencyDuration().toString() : existingJobSettings.getSettingsReloadJobFrequencyDuration().toString())).set((Path)Tables.RL_SYSTEM_SETTINGS.REAPER_JOB_DURATION, Objects.nonNull(updatedJobSettings.getBucketCleanupJobFrequencyDuration()) ? updatedJobSettings.getBucketCleanupJobFrequencyDuration().toString() : existingJobSettings.getBucketCleanupJobFrequencyDuration().toString())).where((Predicate)Tables.RL_SYSTEM_SETTINGS.NAME.eq(NAME_SYSTEM)).execute();
            this.settingsVersionDao.incrementDefaultSettingsVersion();
            return count;
        }, () -> logger.error("Caught error updating system rate limiting settings: [{}] in DB - rolling back transaction", (Object)newSystemSettings));
        logger.debug("Updated num entities: [{}]", (Object)updatedEntityCount);
        return this.getSystemSettings();
    }

    @Override
    public SystemRateLimitingSettings getSystemSettings() {
        Tuple tuple = this.databaseAccessor.runInTransaction(databaseConnection -> (Tuple)((SQLQuery)((SQLQuery)databaseConnection.select(Tables.RL_SYSTEM_SETTINGS.all()).from((Expression<?>)Tables.RL_SYSTEM_SETTINGS)).where(Tables.RL_SYSTEM_SETTINGS.NAME.eq(NAME_SYSTEM))).fetchFirst(), OnRollback.NOOP);
        return Objects.isNull(tuple) ? null : this.getSystemSettings(tuple);
    }

    private SystemRateLimitingSettings getSystemSettings(Tuple tuple) {
        TokenBucketSettings bucketSettings = new TokenBucketSettings(tuple.get(Tables.RL_SYSTEM_SETTINGS.CAPACITY), tuple.get(Tables.RL_SYSTEM_SETTINGS.FILL_RATE), tuple.get(Tables.RL_SYSTEM_SETTINGS.INTERVAL_FREQUENCY), ChronoUnit.valueOf(tuple.get(Tables.RL_SYSTEM_SETTINGS.INTERVAL_TIME_UNIT)));
        return new SystemRateLimitingSettings.Builder().mode(RateLimitingMode.valueOf(tuple.get(Tables.RL_SYSTEM_SETTINGS.MODE))).bucketSettings(bucketSettings).bucketCollectionJobFrequencyDuration(Duration.parse(tuple.get(Tables.RL_SYSTEM_SETTINGS.FLUSH_JOB_DURATION))).reportingDbArchivingJobFrequencyDuration(Duration.parse(tuple.get(Tables.RL_SYSTEM_SETTINGS.CLEAN_JOB_DURATION))).reportingDbRetentionPeriodDuration(Duration.parse(tuple.get(Tables.RL_SYSTEM_SETTINGS.RETENTION_PERIOD_DURATION))).bucketCleanupJobFrequencyDuration(Duration.parse(tuple.get(Tables.RL_SYSTEM_SETTINGS.REAPER_JOB_DURATION))).settingsReloadPeriodDuration(Duration.parse(tuple.get(Tables.RL_SYSTEM_SETTINGS.SETTINGS_RELOAD_JOB_DURATION))).build();
    }

    @Override
    public Optional<Long> getLatestSystemSettingsVersion() {
        return this.settingsVersionDao.getLatestSystemSettingsVersion();
    }

    @VisibleForTesting
    long count() {
        return this.databaseAccessor.runInTransaction(databaseConnection -> ((SQLQuery)databaseConnection.select(Tables.RL_SYSTEM_SETTINGS.NAME).from((Expression<?>)Tables.RL_SYSTEM_SETTINGS)).fetchCount(), OnRollback.NOOP);
    }

    @VisibleForTesting
    void delete() {
        Long deletedCount = this.databaseAccessor.runInTransaction(databaseConnection -> {
            long count = databaseConnection.delete(Tables.RL_SYSTEM_SETTINGS).where((Predicate)Tables.RL_SYSTEM_SETTINGS.NAME.eq(NAME_SYSTEM)).execute();
            this.settingsVersionDao.incrementDefaultSettingsVersion();
            return count;
        }, () -> logger.error("Caught error deleting system rate limiting settings from DB - rolling back transaction"));
        logger.debug("Deleted num entities: [{}]", (Object)deletedCount);
    }
}

