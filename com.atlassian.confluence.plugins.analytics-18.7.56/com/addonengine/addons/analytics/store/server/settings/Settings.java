/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.types.Expression
 *  com.querydsl.core.types.Path
 *  com.querydsl.core.types.Predicate
 *  com.querydsl.sql.RelationalPath
 *  com.querydsl.sql.SQLQuery
 *  com.querydsl.sql.SQLQueryFactory
 *  com.querydsl.sql.dml.SQLDeleteClause
 *  com.querydsl.sql.dml.SQLInsertClause
 *  com.querydsl.sql.dml.SQLUpdateClause
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.JvmClassMappingKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.reflect.KClass
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.store.server.settings;

import com.addonengine.addons.analytics.store.server.querydsl.QueryDslDbConnectionManager;
import com.addonengine.addons.analytics.store.server.querydsl.Tables;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ$\u0010\u000b\u001a\u0004\u0018\u0001H\f\"\n\b\u0000\u0010\f\u0018\u0001*\u00020\u00012\u0006\u0010\t\u001a\u00020\nH\u0086\b\u00a2\u0006\u0002\u0010\rJ-\u0010\u000b\u001a\u0004\u0018\u0001H\f\"\b\b\u0000\u0010\f*\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\f0\u000f\u00a2\u0006\u0002\u0010\u0010J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\n2\u0006\u0010\t\u001a\u00020\nH\u0002J!\u0010\u0012\u001a\u00020\u0013\"\u0004\b\u0000\u0010\f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u0002H\f\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/store/server/settings/Settings;", "", "db", "Lcom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager;", "(Lcom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager;)V", "mapper", "Lorg/codehaus/jackson/map/ObjectMapper;", "delete", "", "key", "", "get", "T", "(Ljava/lang/String;)Ljava/lang/Object;", "klass", "Lkotlin/reflect/KClass;", "(Ljava/lang/String;Lkotlin/reflect/KClass;)Ljava/lang/Object;", "getValueAsString", "set", "", "value", "(Ljava/lang/String;Ljava/lang/Object;)V", "analytics"})
public final class Settings {
    @NotNull
    private final QueryDslDbConnectionManager db;
    @NotNull
    private final ObjectMapper mapper;

    @Inject
    public Settings(@NotNull QueryDslDbConnectionManager db) {
        Intrinsics.checkNotNullParameter((Object)db, (String)"db");
        this.db = db;
        this.mapper = new ObjectMapper();
    }

    public final /* synthetic */ <T> T get(String key) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        boolean $i$f$get = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return this.get(key, Reflection.getOrCreateKotlinClass(Object.class));
    }

    @Nullable
    public final <T> T get(@NotNull String key, @NotNull KClass<T> klass) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter(klass, (String)"klass");
        String value = this.getValueAsString(key);
        return (T)(value == null ? null : this.mapper.readValue(value, JvmClassMappingKt.getJavaClass(klass)));
    }

    public final <T> void set(@NotNull String key, T value) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        String valueString = this.mapper.writeValueAsString(value);
        this.db.execute(false, (Function1)new Function1<SQLQueryFactory, Long>(this, key, valueString){
            final /* synthetic */ Settings this$0;
            final /* synthetic */ String $key;
            final /* synthetic */ String $valueString;
            {
                this.this$0 = $receiver;
                this.$key = $key;
                this.$valueString = $valueString;
                super(1);
            }

            @NotNull
            public final Long invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                boolean exists = Settings.access$getValueAsString(this.this$0, this.$key) != null;
                long now = Instant.now().toEpochMilli();
                return exists ? ((SQLUpdateClause)((SQLUpdateClause)((SQLUpdateClause)sqlQueryFactory.update((RelationalPath)Tables.INSTANCE.getSettings()).set((Path)Tables.INSTANCE.getSettings().getValue(), (Object)this.$valueString)).set((Path)Tables.INSTANCE.getSettings().getUpdatedAt(), (Object)now)).where((Predicate)Tables.INSTANCE.getSettings().getKey().eq((Object)this.$key))).execute() : ((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)sqlQueryFactory.insert((RelationalPath)Tables.INSTANCE.getSettings()).set((Path)Tables.INSTANCE.getSettings().getKey(), (Object)this.$key)).set((Path)Tables.INSTANCE.getSettings().getValue(), (Object)this.$valueString)).set((Path)Tables.INSTANCE.getSettings().getCreatedAt(), (Object)now)).set((Path)Tables.INSTANCE.getSettings().getUpdatedAt(), (Object)now)).execute();
            }
        });
    }

    public final boolean delete(@NotNull String key) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        long rowCount2 = ((Number)this.db.execute(false, (Function1)new Function1<SQLQueryFactory, Long>(key){
            final /* synthetic */ String $key;
            {
                this.$key = $key;
                super(1);
            }

            @NotNull
            public final Long invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                return ((SQLDeleteClause)sqlQueryFactory.delete((RelationalPath)Tables.INSTANCE.getSettings()).where((Predicate)Tables.INSTANCE.getSettings().getKey().eq((Object)this.$key))).execute();
            }
        })).longValue();
        return rowCount2 > 0L;
    }

    private final String getValueAsString(String key) {
        return (String)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, String>(key){
            final /* synthetic */ String $key;
            {
                this.$key = $key;
                super(1);
            }

            public final String invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                return (String)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Tables.INSTANCE.getSettings().getValue()).from((Expression)Tables.INSTANCE.getSettings())).where((Predicate)Tables.INSTANCE.getSettings().getKey().eq((Object)this.$key))).fetchOne();
            }
        }, 1, null);
    }

    public static final /* synthetic */ String access$getValueAsString(Settings $this, String key) {
        return $this.getValueAsString(key);
    }
}

