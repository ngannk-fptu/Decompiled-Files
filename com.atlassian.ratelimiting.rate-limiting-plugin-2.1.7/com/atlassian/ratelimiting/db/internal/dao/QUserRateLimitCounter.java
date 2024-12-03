/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Date;

public class QUserRateLimitCounter
extends EnhancedRelationalPathBase<QUserRateLimitCounter> {
    private static final long serialVersionUID = 2212735288379494479L;
    public final NumberPath<Long> ID = this.createLongCol("ID").asPrimaryKey().notNull().build();
    public final DateTimePath<Date> INTERVAL_START = this.createDateTimeCol("INTERVAL_START", Date.class).build();
    public final StringPath NODE_ID = this.createStringCol("NODE_ID").notNull().build();
    public final NumberPath<Long> REJECT_COUNT = this.createLongCol("REJECT_COUNT").notNull().build();
    public final StringPath USER_ID = this.createStringCol("USER_ID").notNull().build();

    public QUserRateLimitCounter(String aoPrefix) {
        super(QUserRateLimitCounter.class, aoPrefix + "_RL_USER_COUNTER");
    }
}

