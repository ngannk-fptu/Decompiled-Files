/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.Operator
 *  com.querydsl.core.types.Ops$DateTimeOps
 *  com.querydsl.sql.OracleTemplates
 *  com.querydsl.sql.SQLTemplates
 *  com.querydsl.sql.SQLTemplates$Builder
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl.template;

import com.addonengine.addons.analytics.store.server.querydsl.operator.FastStringHash;
import com.addonengine.addons.analytics.store.server.querydsl.operator.UnixSecondToUtcDatetime;
import com.addonengine.addons.analytics.store.server.querydsl.operator.UtcDatetimeToUnixSecond;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedOracleTemplate;", "Lcom/querydsl/sql/OracleTemplates;", "escape", "", "quote", "", "(CZ)V", "Companion", "analytics"})
public final class ExtendedOracleTemplate
extends OracleTemplates {
    @NotNull
    public static final Companion Companion = new Companion(null);

    public ExtendedOracleTemplate(char escape, boolean quote) {
        super(escape, quote);
        this.add(UnixSecondToUtcDatetime.INSTANCE, "timestamp '1970-01-01 00:00:00' + numtodsinterval({0} + {1},'SECOND')");
        this.add(UtcDatetimeToUnixSecond.INSTANCE, "(extract(day from (cast({0} as timestamp) - timestamp '1970-01-01 00:00:00')) * 86400 +extract(hour from cast({0} as timestamp)) * 3600 +extract(minute from cast({0} as timestamp)) * 60 +extract(second from cast({0} as timestamp)) - {1})");
        this.add((Operator)Ops.DateTimeOps.TRUNC_WEEK, "trunc({0}, 'iw')");
        this.add(FastStringHash.INSTANCE, "{0}");
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedOracleTemplate$Companion;", "", "()V", "builder", "Lcom/querydsl/sql/SQLTemplates$Builder;", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final SQLTemplates.Builder builder() {
            return new SQLTemplates.Builder(){

                @NotNull
                protected SQLTemplates build(char escape, boolean quote) {
                    return (SQLTemplates)new ExtendedOracleTemplate(escape, quote);
                }
            };
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

