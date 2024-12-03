/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.dsl.DateTimePath
 *  com.querydsl.core.types.dsl.NumberPath
 *  com.querydsl.core.types.dsl.StringPath
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.sql.Timestamp;
import java.util.Date;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0011\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000eR/\u0010\u0013\u001a \u0012\u001c\b\u0001\u0012\u0018\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00160\u0016 \b*\u0004\u0018\u00010\u00170\u00150\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R/\u0010\u001a\u001a \u0012\u001c\b\u0001\u0012\u0018\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00160\u0016 \b*\u0004\u0018\u00010\u00170\u00150\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u0011\u0010\u001c\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u000eR\u001f\u0010\u001e\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\n\u00a8\u0006 "}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QContent;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "contentId", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getContentId", "()Lcom/querydsl/core/types/dsl/NumberPath;", "contentName", "Lcom/querydsl/core/types/dsl/StringPath;", "getContentName", "()Lcom/querydsl/core/types/dsl/StringPath;", "contentStatus", "getContentStatus", "contentType", "getContentType", "creationDate", "Lcom/querydsl/core/types/dsl/DateTimePath;", "", "Ljava/util/Date;", "Ljava/sql/Timestamp;", "getCreationDate", "()Lcom/querydsl/core/types/dsl/DateTimePath;", "lastModificationDate", "getLastModificationDate", "originalVersion", "getOriginalVersion", "spaceId", "getSpaceId", "analytics"})
public final class QContent
extends EnhancedRelationalPathBase<QContent> {
    @NotNull
    private final NumberPath<Long> contentId;
    @NotNull
    private final StringPath contentName;
    @NotNull
    private final StringPath contentType;
    @NotNull
    private final StringPath contentStatus;
    @NotNull
    private final StringPath originalVersion;
    @NotNull
    private final NumberPath<Long> spaceId;
    @NotNull
    private final DateTimePath<? extends Comparable<Date>> creationDate;
    @NotNull
    private final DateTimePath<? extends Comparable<Date>> lastModificationDate;

    public QContent(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QContent.class, tableName);
        NumberPath<Long> numberPath = this.createLongCol("CONTENTID").notNull().build();
        Intrinsics.checkNotNull(numberPath);
        this.contentId = numberPath;
        StringPath stringPath = this.createStringCol("TITLE").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.contentName = stringPath;
        StringPath stringPath2 = this.createStringCol("CONTENTTYPE").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath2);
        this.contentType = stringPath2;
        StringPath stringPath3 = this.createStringCol("CONTENT_STATUS").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath3);
        this.contentStatus = stringPath3;
        StringPath stringPath4 = this.createStringCol("PREVVER").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath4);
        this.originalVersion = stringPath4;
        NumberPath<Long> numberPath2 = this.createLongCol("SPACEID").notNull().build();
        Intrinsics.checkNotNull(numberPath2);
        this.spaceId = numberPath2;
        DateTimePath<Timestamp> dateTimePath = this.createDateTimeCol("CREATIONDATE", Timestamp.class).build();
        Intrinsics.checkNotNull(dateTimePath);
        this.creationDate = dateTimePath;
        DateTimePath<Timestamp> dateTimePath2 = this.createDateTimeCol("LASTMODDATE", Timestamp.class).build();
        Intrinsics.checkNotNull(dateTimePath2);
        this.lastModificationDate = dateTimePath2;
    }

    @NotNull
    public final NumberPath<Long> getContentId() {
        return this.contentId;
    }

    @NotNull
    public final StringPath getContentName() {
        return this.contentName;
    }

    @NotNull
    public final StringPath getContentType() {
        return this.contentType;
    }

    @NotNull
    public final StringPath getContentStatus() {
        return this.contentStatus;
    }

    @NotNull
    public final StringPath getOriginalVersion() {
        return this.originalVersion;
    }

    @NotNull
    public final NumberPath<Long> getSpaceId() {
        return this.spaceId;
    }

    @NotNull
    public final DateTimePath<? extends Comparable<Date>> getCreationDate() {
        return this.creationDate;
    }

    @NotNull
    public final DateTimePath<? extends Comparable<Date>> getLastModificationDate() {
        return this.lastModificationDate;
    }
}

