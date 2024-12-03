/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2={"Lcom/addonengine/addons/analytics/rest/LimitValidator;", "", "()V", "Companion", "analytics"})
public final class LimitValidator {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private static final Integer MAX_LIMIT = Integer.getInteger("confluence.analytics.pagination.max.limit", 100);

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0004J\u0013\u0010\f\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004\u00a2\u0006\u0002\u0010\rR\u0018\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0006\u00a8\u0006\u000e"}, d2={"Lcom/addonengine/addons/analytics/rest/LimitValidator$Companion;", "", "()V", "MAX_LIMIT", "", "kotlin.jvm.PlatformType", "Ljava/lang/Integer;", "errorMessage", "", "isInvalid", "", "limit", "maxLimit", "()Ljava/lang/Integer;", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public final boolean isInvalid(int limit) {
            boolean bl;
            if (0 <= limit) {
                Integer n = MAX_LIMIT;
                Intrinsics.checkNotNullExpressionValue((Object)n, (String)"access$getMAX_LIMIT$cp(...)");
                bl = limit <= ((Number)n).intValue();
            } else {
                bl = false;
            }
            return !bl;
        }

        public final Integer maxLimit() {
            return MAX_LIMIT;
        }

        @NotNull
        public final String errorMessage() {
            return "'limit' parameter should be between 0 and " + this.maxLimit();
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

