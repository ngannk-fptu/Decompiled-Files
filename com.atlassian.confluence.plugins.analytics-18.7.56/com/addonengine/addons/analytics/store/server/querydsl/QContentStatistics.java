/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.dsl.NumberPath
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\f\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001f\u0010\u000b\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u001f\u0010\r\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u001f\u0010\u000f\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\nR\u001f\u0010\u0011\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\n\u00a8\u0006\u0013"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QContentStatistics;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "commentsCount", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getCommentsCount", "()Lcom/querydsl/core/types/dsl/NumberPath;", "contentId", "getContentId", "maxEventAt", "getMaxEventAt", "viewedCount", "getViewedCount", "viewedUsers", "getViewedUsers", "analytics"})
public final class QContentStatistics
extends EnhancedRelationalPathBase<QContentStatistics> {
    @NotNull
    private final NumberPath<Long> contentId;
    @NotNull
    private final NumberPath<Long> maxEventAt;
    @NotNull
    private final NumberPath<Long> commentsCount;
    @NotNull
    private final NumberPath<Long> viewedCount;
    @NotNull
    private final NumberPath<Long> viewedUsers;

    public QContentStatistics(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QContentStatistics.class, tableName);
        NumberPath<Long> numberPath = this.createLongCol("contentId").notNull().build();
        Intrinsics.checkNotNull(numberPath);
        this.contentId = numberPath;
        NumberPath<Long> numberPath2 = this.createLongCol("maxEventAt").notNull().build();
        Intrinsics.checkNotNull(numberPath2);
        this.maxEventAt = numberPath2;
        NumberPath<Long> numberPath3 = this.createLongCol("commentsCount").notNull().build();
        Intrinsics.checkNotNull(numberPath3);
        this.commentsCount = numberPath3;
        NumberPath<Long> numberPath4 = this.createLongCol("viewedCount").notNull().build();
        Intrinsics.checkNotNull(numberPath4);
        this.viewedCount = numberPath4;
        NumberPath<Long> numberPath5 = this.createLongCol("viewedUsers").notNull().build();
        Intrinsics.checkNotNull(numberPath5);
        this.viewedUsers = numberPath5;
    }

    @NotNull
    public final NumberPath<Long> getContentId() {
        return this.contentId;
    }

    @NotNull
    public final NumberPath<Long> getMaxEventAt() {
        return this.maxEventAt;
    }

    @NotNull
    public final NumberPath<Long> getCommentsCount() {
        return this.commentsCount;
    }

    @NotNull
    public final NumberPath<Long> getViewedCount() {
        return this.viewedCount;
    }

    @NotNull
    public final NumberPath<Long> getViewedUsers() {
        return this.viewedUsers;
    }
}

