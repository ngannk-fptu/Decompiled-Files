/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.Operator
 *  com.querydsl.core.types.Ops$DateTimeOps
 *  com.querydsl.sql.SQLServer2012Templates
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
import com.querydsl.sql.SQLServer2012Templates;
import com.querydsl.sql.SQLTemplates;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedSqlServerTemplate;", "Lcom/querydsl/sql/SQLServer2012Templates;", "escape", "", "quote", "", "(CZ)V", "Companion", "analytics"})
public final class ExtendedSqlServerTemplate
extends SQLServer2012Templates {
    @NotNull
    public static final Companion Companion = new Companion(null);

    public ExtendedSqlServerTemplate(char escape, boolean quote) {
        super(escape, quote);
        this.add(UnixSecondToUtcDatetime.INSTANCE, "dateadd(S, {0} + {1}, '1970-01-01')");
        this.add(UtcDatetimeToUnixSecond.INSTANCE, "(cast(datediff(S, '1970-01-01', {0}) as bigint) - {1})");
        this.add((Operator)Ops.DateTimeOps.TRUNC_WEEK, "DATEADD(WEEK, DATEDIFF(WEEK, 0, {0} - 1), 0)");
        if (Boolean.getBoolean("confluence.afc.hashing.disable")) {
            this.add(FastStringHash.INSTANCE, "{0}");
        } else {
            this.add(FastStringHash.INSTANCE, "cast(hashbytes('md5', {0}) as bigint)");
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedSqlServerTemplate$Companion;", "", "()V", "builder", "Lcom/querydsl/sql/SQLTemplates$Builder;", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final SQLTemplates.Builder builder() {
            return new SQLTemplates.Builder(){

                @NotNull
                protected SQLTemplates build(char escape, boolean quote) {
                    return (SQLTemplates)new ExtendedSqlServerTemplate(escape, quote);
                }
            };
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

