/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.codehaus.jackson.annotate.JsonValue
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import kotlin.Metadata;
import org.codehaus.jackson.annotate.JsonValue;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\n\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0007R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "", "date", "Ljava/time/Instant;", "(Ljava/time/Instant;)V", "getDate", "()Ljava/time/Instant;", "toDateString", "", "analytics"})
public final class ApiDateTime {
    @Nullable
    private final Instant date;

    public ApiDateTime(@Nullable Instant date) {
        this.date = date;
    }

    @Nullable
    public final Instant getDate() {
        return this.date;
    }

    @JsonValue
    @Nullable
    public final String toDateString() {
        Object object = this.date;
        if (object == null || (object = ((Instant)object).atOffset(ZoneOffset.UTC)) == null || (object = ((OffsetDateTime)object).format(DateTimeFormatter.ISO_INSTANT)) == null) {
            object = null;
        }
        return object;
    }
}

