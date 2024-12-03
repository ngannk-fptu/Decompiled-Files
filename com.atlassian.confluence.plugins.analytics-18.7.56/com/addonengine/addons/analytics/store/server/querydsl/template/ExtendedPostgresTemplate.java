/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.sql.PostgreSQLTemplates
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
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLTemplates;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedPostgresTemplate;", "Lcom/querydsl/sql/PostgreSQLTemplates;", "escape", "", "quote", "", "(CZ)V", "Companion", "analytics"})
public final class ExtendedPostgresTemplate
extends PostgreSQLTemplates {
    @NotNull
    public static final Companion Companion = new Companion(null);

    public ExtendedPostgresTemplate(char escape, boolean quote) {
        super(escape, quote);
        this.add(UnixSecondToUtcDatetime.INSTANCE, "to_timestamp({0} + {1}) at time zone 'UTC'");
        this.add(UtcDatetimeToUnixSecond.INSTANCE, "(extract(epoch from {0}) - {1})");
        if (Boolean.getBoolean("confluence.afc.hashing.disable")) {
            this.add(FastStringHash.INSTANCE, "{0}");
        } else {
            this.add(FastStringHash.INSTANCE, "('x'||substr(md5({0}),1,16))::bit(64)::bigint");
        }
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2={"Lcom/addonengine/addons/analytics/store/server/querydsl/template/ExtendedPostgresTemplate$Companion;", "", "()V", "builder", "Lcom/querydsl/sql/SQLTemplates$Builder;", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final SQLTemplates.Builder builder() {
            return new SQLTemplates.Builder(){

                @NotNull
                protected SQLTemplates build(char escape, boolean quote) {
                    return (SQLTemplates)new ExtendedPostgresTemplate(escape, quote);
                }
            };
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

