/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QUserRateLimitSettings
extends EnhancedRelationalPathBase<QUserRateLimitSettings> {
    private static final long serialVersionUID = -8133980326787264484L;
    public final StringPath USER_ID = this.createStringCol("USER_ID").asPrimaryKey().notNull().build();
    public final NumberPath<Integer> CAPACITY = this.createIntegerCol("CAPACITY").notNull().build();
    public final NumberPath<Integer> FILL_RATE = this.createIntegerCol("FILL_RATE").notNull().build();
    public final NumberPath<Integer> INTERVAL_FREQUENCY = this.createIntegerCol("INTERVAL_FREQUENCY").notNull().build();
    public final StringPath INTERVAL_TIME_UNIT = this.createStringCol("INTERVAL_TIME_UNIT").notNull().build();
    public final BooleanPath WHITELISTED = this.createBooleanCol("WHITELISTED").notNull().build();

    public QUserRateLimitSettings(String aoPrefix) {
        super(QUserRateLimitSettings.class, aoPrefix + "_USER_RL_SETTINGS");
    }
}

