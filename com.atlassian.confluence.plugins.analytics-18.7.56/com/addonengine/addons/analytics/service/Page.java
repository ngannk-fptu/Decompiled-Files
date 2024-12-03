/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u00020\u0003B\u0017\u0012\u0006\u0010\u0004\u001a\u00028\u0000\u0012\b\u0010\u0005\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000b\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\f\u001a\u0004\u0018\u00018\u0001H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ0\u0010\r\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u00002\b\b\u0002\u0010\u0004\u001a\u00028\u00002\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00018\u0001H\u00c6\u0001\u00a2\u0006\u0002\u0010\u000eJ\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0015\u0010\u0005\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u00028\u0000\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/Page;", "T", "C", "", "data", "cursor", "(Ljava/lang/Object;Ljava/lang/Object;)V", "getCursor", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getData", "component1", "component2", "copy", "(Ljava/lang/Object;Ljava/lang/Object;)Lcom/addonengine/addons/analytics/service/Page;", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class Page<T, C> {
    private final T data;
    @Nullable
    private final C cursor;

    public Page(T data, @Nullable C cursor) {
        this.data = data;
        this.cursor = cursor;
    }

    public final T getData() {
        return this.data;
    }

    @Nullable
    public final C getCursor() {
        return this.cursor;
    }

    public final T component1() {
        return this.data;
    }

    @Nullable
    public final C component2() {
        return this.cursor;
    }

    @NotNull
    public final Page<T, C> copy(T data, @Nullable C cursor) {
        return new Page<T, C>(data, cursor);
    }

    public static /* synthetic */ Page copy$default(Page page, Object object, Object object2, int n, Object object3) {
        if ((n & 1) != 0) {
            object = page.data;
        }
        if ((n & 2) != 0) {
            object2 = page.cursor;
        }
        return page.copy(object, object2);
    }

    @NotNull
    public String toString() {
        return "Page(data=" + this.data + ", cursor=" + this.cursor + ')';
    }

    public int hashCode() {
        int result = this.data == null ? 0 : this.data.hashCode();
        result = result * 31 + (this.cursor == null ? 0 : this.cursor.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Page)) {
            return false;
        }
        Page page = (Page)other;
        if (!Intrinsics.areEqual(this.data, page.data)) {
            return false;
        }
        return Intrinsics.areEqual(this.cursor, page.cursor);
    }
}

