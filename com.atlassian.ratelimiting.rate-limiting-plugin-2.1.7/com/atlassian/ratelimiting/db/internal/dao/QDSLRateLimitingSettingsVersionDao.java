/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.ratelimiting.dao.RateLimitingSettingsVersionDao;
import com.atlassian.ratelimiting.db.internal.dao.Tables;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QDSLRateLimitingSettingsVersionDao
implements RateLimitingSettingsVersionDao {
    private static final Logger log = LoggerFactory.getLogger(QDSLRateLimitingSettingsVersionDao.class);
    public static final Long STARTING_VERSION_VALUE = 1L;
    final DatabaseAccessor databaseAccessor;

    public QDSLRateLimitingSettingsVersionDao(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public void incrementDefaultSettingsVersion() {
        this.incrementSettingsVersion("DEFAULT");
    }

    @Override
    public void incrementUserSettingsVersion() {
        this.incrementSettingsVersion("USER");
    }

    private void incrementSettingsVersion(String type) {
        Long updated = this.databaseAccessor.runInTransaction(db -> ((SQLUpdateClause)db.update(Tables.SETTINGS_VERSION).set((Path)Tables.SETTINGS_VERSION.VERSION, (Expression)Tables.SETTINGS_VERSION.VERSION.add(1))).where((Predicate)Tables.SETTINGS_VERSION.TYPE.eq(type)).execute(), () -> log.warn("Could not save {} settings version increase, no nodes will notice the current changes", (Object)type));
        if (updated < 1L) {
            log.info("No settings version record in the database for {}, inserting...", (Object)type);
            this.databaseAccessor.runInTransaction(databaseConnection -> ((SQLInsertClause)((SQLInsertClause)databaseConnection.insert(Tables.SETTINGS_VERSION).set((Path)Tables.SETTINGS_VERSION.TYPE, type)).set((Path)Tables.SETTINGS_VERSION.VERSION, STARTING_VERSION_VALUE)).executeWithKey(Tables.SETTINGS_VERSION.TYPE), () -> log.error("Error on attempt to insert initial version number"));
        }
    }

    @Override
    public Optional<Long> getLatestUserSettingsVersion() {
        return this.getLatestSettingsVersion("USER");
    }

    @Override
    public Optional<Long> getLatestSystemSettingsVersion() {
        return this.getLatestSettingsVersion("DEFAULT");
    }

    private Optional<Long> getLatestSettingsVersion(String type) {
        Optional<Long> result = Optional.ofNullable(this.databaseAccessor.runInTransaction(databaseConnection -> (Long)((SQLQuery)((SQLQuery)databaseConnection.select(Tables.SETTINGS_VERSION.VERSION).from((Expression<?>)Tables.SETTINGS_VERSION)).where(Tables.SETTINGS_VERSION.TYPE.eq(type))).fetchFirst(), OnRollback.NOOP));
        return result;
    }
}

