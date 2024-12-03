/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00018\u0000\u00a2\u0006\u0002\u0010\u0006R\u0015\u0010\u0005\u001a\u0004\u0018\u00018\u0000\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/service/PageRequest;", "C", "", "limit", "", "cursor", "(ILjava/lang/Object;)V", "getCursor", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getLimit", "()I", "analytics"})
public final class PageRequest<C> {
    private final int limit;
    @Nullable
    private final C cursor;

    public PageRequest(int limit, @Nullable C cursor) {
        this.limit = limit;
        this.cursor = cursor;
    }

    public final int getLimit() {
        return this.limit;
    }

    @Nullable
    public final C getCursor() {
        return this.cursor;
    }
}

