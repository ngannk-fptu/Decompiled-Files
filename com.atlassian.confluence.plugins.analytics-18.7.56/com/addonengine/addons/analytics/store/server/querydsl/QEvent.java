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

import com.addonengine.addons.analytics.extensions.ao.KotlinActiveObjectExtensionsKt;
import com.addonengine.addons.analytics.store.server.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001f\u0010\u000b\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u001f\u0010\r\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u001f\u0010\u000f\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\nR\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\u0017\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u001f\u0010\u0019\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\n\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QEvent;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "containerId", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getContainerId", "()Lcom/querydsl/core/types/dsl/NumberPath;", "contentId", "getContentId", "eventAt", "getEventAt", "id", "getId", "name", "Lcom/querydsl/core/types/dsl/StringPath;", "getName", "()Lcom/querydsl/core/types/dsl/StringPath;", "spaceKey", "getSpaceKey", "userKey", "getUserKey", "versionModificationDate", "getVersionModificationDate", "analytics"})
public final class QEvent
extends EnhancedRelationalPathBase<QEvent> {
    @NotNull
    private final NumberPath<Long> id;
    @NotNull
    private final StringPath name;
    @NotNull
    private final NumberPath<Long> eventAt;
    @NotNull
    private final NumberPath<Long> containerId;
    @NotNull
    private final StringPath spaceKey;
    @NotNull
    private final StringPath userKey;
    @NotNull
    private final NumberPath<Long> contentId;
    @NotNull
    private final NumberPath<Long> versionModificationDate;

    public QEvent(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QEvent.class, tableName);
        NumberPath<Long> numberPath = this.createLongCol("ID").asPrimaryKey().build();
        Intrinsics.checkNotNull(numberPath);
        this.id = numberPath;
        StringPath stringPath = this.createStringCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("name")).notNull().build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.name = stringPath;
        NumberPath<Long> numberPath2 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("eventAt")).notNull().build();
        Intrinsics.checkNotNull(numberPath2);
        this.eventAt = numberPath2;
        NumberPath<Long> numberPath3 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("containerId")).build();
        Intrinsics.checkNotNull(numberPath3);
        this.containerId = numberPath3;
        StringPath stringPath2 = this.createStringCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("spaceKey")).build();
        Intrinsics.checkNotNull((Object)stringPath2);
        this.spaceKey = stringPath2;
        StringPath stringPath3 = this.createStringCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("userKey")).build();
        Intrinsics.checkNotNull((Object)stringPath3);
        this.userKey = stringPath3;
        NumberPath<Long> numberPath4 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("contentId")).build();
        Intrinsics.checkNotNull(numberPath4);
        this.contentId = numberPath4;
        NumberPath<Long> numberPath5 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("versionModificationDate")).build();
        Intrinsics.checkNotNull(numberPath5);
        this.versionModificationDate = numberPath5;
    }

    @NotNull
    public final NumberPath<Long> getId() {
        return this.id;
    }

    @NotNull
    public final StringPath getName() {
        return this.name;
    }

    @NotNull
    public final NumberPath<Long> getEventAt() {
        return this.eventAt;
    }

    @NotNull
    public final NumberPath<Long> getContainerId() {
        return this.containerId;
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
    public final NumberPath<Long> getContentId() {
        return this.contentId;
    }

    @NotNull
    public final NumberPath<Long> getVersionModificationDate() {
        return this.versionModificationDate;
    }
}

