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

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001f\u0010\u0005\u001a\u0010\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QUsersByTimeBucket;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "bucketStartTimestamp", "Lcom/querydsl/core/types/dsl/NumberPath;", "", "kotlin.jvm.PlatformType", "getBucketStartTimestamp", "()Lcom/querydsl/core/types/dsl/NumberPath;", "userKeyHash", "Lcom/querydsl/core/types/dsl/StringPath;", "getUserKeyHash", "()Lcom/querydsl/core/types/dsl/StringPath;", "analytics"})
public final class QUsersByTimeBucket
extends EnhancedRelationalPathBase<QUsersByTimeBucket> {
    @NotNull
    private final NumberPath<Long> bucketStartTimestamp;
    @NotNull
    private final StringPath userKeyHash;

    public QUsersByTimeBucket(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QUsersByTimeBucket.class, tableName);
        NumberPath<Long> numberPath = this.createLongCol("bucketStartTimestamp").notNull().build();
        Intrinsics.checkNotNull(numberPath);
        this.bucketStartTimestamp = numberPath;
        StringPath stringPath = this.createStringCol("userKeyHash").build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.userKeyHash = stringPath;
    }

    @NotNull
    public final NumberPath<Long> getBucketStartTimestamp() {
        return this.bucketStartTimestamp;
    }

    @NotNull
    public final StringPath getUserKeyHash() {
        return this.userKeyHash;
    }
}

