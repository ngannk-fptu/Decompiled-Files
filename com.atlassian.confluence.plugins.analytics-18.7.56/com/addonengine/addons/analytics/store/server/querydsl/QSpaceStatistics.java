/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.dsl.NumberPath
 *  com.querydsl.core.types.dsl.StringPath
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001f\u0010\u000b\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001f\u0010\u0011\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\nR\u0011\u0010\u0013\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u001f\u0010\u0015\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\nR\u001f\u0010\u0017\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\n\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QSpaceStatistics;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "createdCount", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getCreatedCount", "()Lcom/querydsl/core/types/dsl/NumberPath;", "maxEventAt", "getMaxEventAt", "spaceKey", "Lcom/querydsl/core/types/dsl/StringPath;", "getSpaceKey", "()Lcom/querydsl/core/types/dsl/StringPath;", "updatedCount", "getUpdatedCount", "userKey", "getUserKey", "viewedCount", "getViewedCount", "viewedUsers", "getViewedUsers", "analytics"})
public final class QSpaceStatistics
extends EnhancedRelationalPathBase<QSpaceStatistics> {
    @NotNull
    private final StringPath spaceKey;
    @NotNull
    private final StringPath userKey;
    @NotNull
    private final NumberPath<Long> maxEventAt;
    @NotNull
    private final NumberPath<Long> viewedCount;
    @NotNull
    private final NumberPath<Long> createdCount;
    @NotNull
    private final NumberPath<Long> updatedCount;
    @NotNull
    private final NumberPath<Long> viewedUsers;

    public QSpaceStatistics(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QSpaceStatistics.class, tableName);
        StringPath stringPath = this.createStringCol("spaceKey").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.spaceKey = stringPath;
        StringPath stringPath2 = this.createStringCol("userKey").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath2);
        this.userKey = stringPath2;
        NumberPath<Long> numberPath = this.createLongCol("maxEventAt").notNull().build();
        Intrinsics.checkNotNull(numberPath);
        this.maxEventAt = numberPath;
        NumberPath<Long> numberPath2 = this.createLongCol("viewedCount").notNull().build();
        Intrinsics.checkNotNull(numberPath2);
        this.viewedCount = numberPath2;
        NumberPath<Long> numberPath3 = this.createLongCol("createdCount").notNull().build();
        Intrinsics.checkNotNull(numberPath3);
        this.createdCount = numberPath3;
        NumberPath<Long> numberPath4 = this.createLongCol("updatedCount").notNull().build();
        Intrinsics.checkNotNull(numberPath4);
        this.updatedCount = numberPath4;
        NumberPath<Long> numberPath5 = this.createLongCol("viewedUsers").notNull().build();
        Intrinsics.checkNotNull(numberPath5);
        this.viewedUsers = numberPath5;
    }

    @NotNull
    public final StringPath getSpaceKey() {
        return this.spaceKey;
    }

    @NotNull
    public final StringPath getUserKey() {
        return this.userKey;
    }

    @NotNull
    public final NumberPath<Long> getMaxEventAt() {
        return this.maxEventAt;
    }

    @NotNull
    public final NumberPath<Long> getViewedCount() {
        return this.viewedCount;
    }

    @NotNull
    public final NumberPath<Long> getCreatedCount() {
        return this.createdCount;
    }

    @NotNull
    public final NumberPath<Long> getUpdatedCount() {
        return this.updatedCount;
    }

    @NotNull
    public final NumberPath<Long> getViewedUsers() {
        return this.viewedUsers;
    }
}

