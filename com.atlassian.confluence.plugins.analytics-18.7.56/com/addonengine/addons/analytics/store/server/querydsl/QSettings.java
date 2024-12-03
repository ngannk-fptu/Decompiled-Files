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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001f\u0010\u000b\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001f\u0010\u0011\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\nR\u0011\u0010\u0013\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QSettings;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "createdAt", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getCreatedAt", "()Lcom/querydsl/core/types/dsl/NumberPath;", "id", "getId", "key", "Lcom/querydsl/core/types/dsl/StringPath;", "getKey", "()Lcom/querydsl/core/types/dsl/StringPath;", "updatedAt", "getUpdatedAt", "value", "getValue", "analytics"})
public final class QSettings
extends EnhancedRelationalPathBase<QSettings> {
    @NotNull
    private final NumberPath<Long> id;
    @NotNull
    private final StringPath key;
    @NotNull
    private final StringPath value;
    @NotNull
    private final NumberPath<Long> createdAt;
    @NotNull
    private final NumberPath<Long> updatedAt;

    public QSettings(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QSettings.class, tableName);
        NumberPath<Long> numberPath = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("id")).asPrimaryKey().build();
        Intrinsics.checkNotNull(numberPath);
        this.id = numberPath;
        StringPath stringPath = this.createStringCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("key")).notNull().build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.key = stringPath;
        StringPath stringPath2 = this.createStringCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("value")).notNull().build();
        Intrinsics.checkNotNull((Object)stringPath2);
        this.value = stringPath2;
        NumberPath<Long> numberPath2 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("createdAt")).notNull().build();
        Intrinsics.checkNotNull(numberPath2);
        this.createdAt = numberPath2;
        NumberPath<Long> numberPath3 = this.createLongCol(KotlinActiveObjectExtensionsKt.toDBParamFieldName("updatedAt")).notNull().build();
        Intrinsics.checkNotNull(numberPath3);
        this.updatedAt = numberPath3;
    }

    @NotNull
    public final NumberPath<Long> getId() {
        return this.id;
    }

    @NotNull
    public final StringPath getKey() {
        return this.key;
    }

    @NotNull
    public final StringPath getValue() {
        return this.value;
    }

    @NotNull
    public final NumberPath<Long> getCreatedAt() {
        return this.createdAt;
    }

    @NotNull
    public final NumberPath<Long> getUpdatedAt() {
        return this.updatedAt;
    }
}

