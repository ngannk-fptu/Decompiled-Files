/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class QSettingsVersion
extends EnhancedRelationalPathBase<QSettingsVersion> {
    public final StringPath TYPE = this.createStringCol("TYPE").asPrimaryKey().build();
    public final NumberPath<Long> VERSION = this.createLongCol("VERSION").notNull().build();

    public QSettingsVersion(String aoPrefix) {
        super(QSettingsVersion.class, aoPrefix + "_SETTINGS_VERSION");
    }
}

