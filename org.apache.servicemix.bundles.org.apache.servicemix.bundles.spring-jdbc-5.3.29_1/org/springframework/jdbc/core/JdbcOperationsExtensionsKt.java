/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000B\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0015\n\u0002\b\u0007\u001aL\u0010\u0000\u001a\u0002H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006\"\u00020\u00072\u0014\b\u0004\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u0002H\u00010\tH\u0086\b\u00a2\u0006\u0002\u0010\u000b\u001a?\u0010\u0000\u001a\u00020\f*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006\"\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\f0\t\u00a2\u0006\u0002\u0010\r\u001aQ\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0004\b\u0000\u0010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006\"\u00020\u00072\u0018\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u0002H\u00010\u000f\u00a2\u0006\u0002\u0010\u0011\u001a#\u0010\u0012\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\b\u001a8\u0010\u0012\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006H\u0086\b\u00a2\u0006\u0002\u0010\u0013\u001a@\u0010\u0012\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000e\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u00062\u0006\u0010\u0014\u001a\u00020\u0015H\u0086\b\u00a2\u0006\u0002\u0010\u0016\u001a\"\u0010\u0017\u001a\u0002H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\b\u00a2\u0006\u0002\u0010\u0018\u001a4\u0010\u0017\u001a\u0004\u0018\u0001H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006H\u0086\b\u00a2\u0006\u0002\u0010\u0019\u001aR\u0010\u0017\u001a\u0002H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006\"\u00020\u00072\u001a\b\u0004\u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u0002H\u00010\u000fH\u0086\b\u00a2\u0006\u0002\u0010\u001a\u001a<\u0010\u0017\u001a\u0004\u0018\u0001H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u00062\u0006\u0010\u0014\u001a\u00020\u0015H\u0086\b\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001c"}, d2={"query", "T", "Lorg/springframework/jdbc/core/JdbcOperations;", "sql", "", "args", "", "", "function", "Lkotlin/Function1;", "Ljava/sql/ResultSet;", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)V", "", "Lkotlin/Function2;", "", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/util/List;", "queryForList", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/List;", "argTypes", "", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;[I)Ljava/util/List;", "queryForObject", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;)Ljava/lang/Object;", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "(Lorg/springframework/jdbc/core/JdbcOperations;Ljava/lang/String;[Ljava/lang/Object;[I)Ljava/lang/Object;", "spring-jdbc"})
public final class JdbcOperationsExtensionsKt {
    public static final /* synthetic */ <T> T queryForObject(JdbcOperations $this$queryForObject, String sql) {
        int $i$f$queryForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForObject, (String)"$this$queryForObject");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$queryForObject.queryForObject(sql, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> T queryForObject(JdbcOperations $this$queryForObject, String sql, Object[] args, Function2<? super ResultSet, ? super Integer, ? extends T> function) {
        int $i$f$queryForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForObject, (String)"$this$queryForObject");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        Object t = $this$queryForObject.queryForObject(sql, new RowMapper(function){
            final /* synthetic */ Function2 $function;

            public final T mapRow(@NotNull ResultSet resultSet, int i) {
                Intrinsics.checkParameterIsNotNull((Object)resultSet, (String)"resultSet");
                return (T)this.$function.invoke((Object)resultSet, (Object)i);
            }
            {
                this.$function = function2;
            }
        }, Arrays.copyOf(args, args.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return t;
    }

    public static final /* synthetic */ <T> T queryForObject(JdbcOperations $this$queryForObject, String sql, Object[] args, int[] argTypes) {
        int $i$f$queryForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForObject, (String)"$this$queryForObject");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull((Object)argTypes, (String)"argTypes");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$queryForObject.queryForObject(sql, args, argTypes, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> T queryForObject(JdbcOperations $this$queryForObject, String sql, Object[] args) {
        int $i$f$queryForObject = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForObject, (String)"$this$queryForObject");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$queryForObject.queryForObject(sql, args, Object.class);
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return (T)object;
    }

    public static final /* synthetic */ <T> List<T> queryForList(JdbcOperations $this$queryForList, String sql) {
        int $i$f$queryForList = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForList, (String)"$this$queryForList");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        List<Object> list = $this$queryForList.queryForList(sql, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(list, (String)"queryForList(sql, T::class.java)");
        return list;
    }

    public static final /* synthetic */ <T> List<T> queryForList(JdbcOperations $this$queryForList, String sql, Object[] args, int[] argTypes) {
        int $i$f$queryForList = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForList, (String)"$this$queryForList");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull((Object)argTypes, (String)"argTypes");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        List<Object> list = $this$queryForList.queryForList(sql, args, argTypes, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(list, (String)"queryForList(sql, args, argTypes, T::class.java)");
        return list;
    }

    public static final /* synthetic */ <T> List<T> queryForList(JdbcOperations $this$queryForList, String sql, Object[] args) {
        int $i$f$queryForList = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$queryForList, (String)"$this$queryForList");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        List<Object> list = $this$queryForList.queryForList(sql, args, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(list, (String)"queryForList(sql, args, T::class.java)");
        return list;
    }

    public static final /* synthetic */ <T> T query(JdbcOperations $this$query, String sql, Object[] args, Function1<? super ResultSet, ? extends T> function) {
        int $i$f$query = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$query, (String)"$this$query");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        Object t = $this$query.query(sql, new ResultSetExtractor(function){
            final /* synthetic */ Function1 $function;

            public final T extractData(@NotNull ResultSet it) {
                Intrinsics.checkParameterIsNotNull((Object)it, (String)"it");
                return (T)this.$function.invoke((Object)it);
            }
            {
                this.$function = function1;
            }
        }, Arrays.copyOf(args, args.length));
        Intrinsics.reifiedOperationMarker((int)1, (String)"T");
        return t;
    }

    public static final void query(@NotNull JdbcOperations $this$query, @NotNull String sql, @NotNull Object[] args, @NotNull Function1<? super ResultSet, Unit> function) {
        Intrinsics.checkParameterIsNotNull((Object)$this$query, (String)"$this$query");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        $this$query.query(sql, new RowCallbackHandler(function){
            final /* synthetic */ Function1 $function;

            public final void processRow(@NotNull ResultSet it) {
                Intrinsics.checkParameterIsNotNull((Object)it, (String)"it");
                this.$function.invoke((Object)it);
            }
            {
                this.$function = function1;
            }
        }, Arrays.copyOf(args, args.length));
    }

    @NotNull
    public static final <T> List<T> query(@NotNull JdbcOperations $this$query, @NotNull String sql, @NotNull Object[] args, @NotNull Function2<? super ResultSet, ? super Integer, ? extends T> function) {
        Intrinsics.checkParameterIsNotNull((Object)$this$query, (String)"$this$query");
        Intrinsics.checkParameterIsNotNull((Object)sql, (String)"sql");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        List list = $this$query.query(sql, new RowMapper(function){
            final /* synthetic */ Function2 $function;

            public final T mapRow(@NotNull ResultSet rs, int i) {
                Intrinsics.checkParameterIsNotNull((Object)rs, (String)"rs");
                return (T)this.$function.invoke((Object)rs, (Object)i);
            }
            {
                this.$function = function2;
            }
        }, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull(list, (String)"query(sql, RowMapper { r\u2026function(rs, i) }, *args)");
        return list;
    }
}

