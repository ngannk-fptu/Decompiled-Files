/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.Operator
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl.operator;

import com.querydsl.core.types.Operator;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u0004H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/operator/UtcDatetimeToUnixSecond;", "Lcom/querydsl/core/types/Operator;", "()V", "getType", "Ljava/lang/Class;", "", "name", "", "analytics"})
public final class UtcDatetimeToUnixSecond
implements Operator {
    @NotNull
    public static final UtcDatetimeToUnixSecond INSTANCE = new UtcDatetimeToUnixSecond();

    private UtcDatetimeToUnixSecond() {
    }

    @NotNull
    public String name() {
        return "UTC_DATETIME_TO_UNIX_SECOND";
    }

    @NotNull
    public Class<Comparable<?>> getType() {
        return Comparable.class;
    }
}

