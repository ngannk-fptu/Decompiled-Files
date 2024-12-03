/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.dsl.StringPath
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.addonengine.addons.analytics.store.server.querydsl.EnhancedRelationalPathBase;
import com.querydsl.core.types.dsl.StringPath;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\bR\u0011\u0010\r\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\b\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/QSpaces;", "Lcom/addonengine/addons/analytics/store/server/querydsl/EnhancedRelationalPathBase;", "tableName", "", "(Ljava/lang/String;)V", "spaceKey", "Lcom/querydsl/core/types/dsl/StringPath;", "getSpaceKey", "()Lcom/querydsl/core/types/dsl/StringPath;", "spaceName", "getSpaceName", "spaceStatus", "getSpaceStatus", "spaceType", "getSpaceType", "analytics"})
public final class QSpaces
extends EnhancedRelationalPathBase<QSpaces> {
    @NotNull
    private final StringPath spaceKey;
    @NotNull
    private final StringPath spaceName;
    @NotNull
    private final StringPath spaceType;
    @NotNull
    private final StringPath spaceStatus;

    public QSpaces(@NotNull String tableName) {
        Intrinsics.checkNotNullParameter((Object)tableName, (String)"tableName");
        super(QSpaces.class, tableName);
        StringPath stringPath = this.createStringCol("SPACEKEY").notNull().build();
        Intrinsics.checkNotNull((Object)stringPath);
        this.spaceKey = stringPath;
        StringPath stringPath2 = this.createStringCol("SPACENAME").build();
        Intrinsics.checkNotNull((Object)stringPath2);
        this.spaceName = stringPath2;
        StringPath stringPath3 = this.createStringCol("SPACETYPE").build();
        Intrinsics.checkNotNull((Object)stringPath3);
        this.spaceType = stringPath3;
        StringPath stringPath4 = this.createStringCol("SPACESTATUS").build();
        Intrinsics.checkNotNull((Object)stringPath4);
        this.spaceStatus = stringPath4;
    }

    @NotNull
    public final StringPath getSpaceKey() {
        return this.spaceKey;
    }

    @NotNull
    public final StringPath getSpaceName() {
        return this.spaceName;
    }

    @NotNull
    public final StringPath getSpaceType() {
        return this.spaceType;
    }

    @NotNull
    public final StringPath getSpaceStatus() {
        return this.spaceStatus;
    }
}

