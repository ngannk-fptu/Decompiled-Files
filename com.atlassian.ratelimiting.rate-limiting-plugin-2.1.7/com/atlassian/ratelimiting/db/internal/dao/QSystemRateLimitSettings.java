/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QSystemRateLimitSettings
extends EnhancedRelationalPathBase<QSystemRateLimitSettings> {
    private static final long serialVersionUID = -5737467895193044509L;
    public final StringPath NAME = this.createStringCol("NAME").asPrimaryKey().notNull().build();
    public final StringPath MODE = this.createStringCol("MODE").notNull().build();
    public final NumberPath<Integer> CAPACITY = this.createIntegerCol("CAPACITY").notNull().build();
    public final NumberPath<Integer> FILL_RATE = this.createIntegerCol("FILL_RATE").notNull().build();
    public final NumberPath<Integer> INTERVAL_FREQUENCY = this.createIntegerCol("INTERVAL_FREQUENCY").notNull().build();
    public final StringPath INTERVAL_TIME_UNIT = this.createStringCol("INTERVAL_TIME_UNIT").notNull().build();
    public final StringPath FLUSH_JOB_DURATION = this.createStringCol("FLUSH_JOB_DURATION").notNull().build();
    public final StringPath REAPER_JOB_DURATION = this.createStringCol("REAPER_JOB_DURATION").notNull().build();
    public final StringPath RETENTION_PERIOD_DURATION = this.createStringCol("RETENTION_PERIOD_DURATION").notNull().build();
    public final StringPath CLEAN_JOB_DURATION = this.createStringCol("CLEAN_JOB_DURATION").notNull().build();
    public final StringPath SETTINGS_RELOAD_JOB_DURATION = this.createStringCol("SETTINGS_RELOAD_JOB_DURATION").notNull().build();

    public QSystemRateLimitSettings(String aoPrefix) {
        super(QSystemRateLimitSettings.class, aoPrefix + "_SYSTEM_RL_SETTINGS");
    }
}

