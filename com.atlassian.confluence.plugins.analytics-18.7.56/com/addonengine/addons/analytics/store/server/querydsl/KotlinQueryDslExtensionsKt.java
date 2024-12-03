/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.querydsl.core.ResultTransformer
 *  com.querydsl.core.group.GroupByBuilder
 *  com.querydsl.core.types.CollectionExpression
 *  com.querydsl.core.types.ConstructorExpression
 *  com.querydsl.core.types.Expression
 *  com.querydsl.core.types.Path
 *  com.querydsl.core.types.Projections
 *  com.querydsl.core.types.SubQueryExpression
 *  com.querydsl.core.types.dsl.BooleanExpression
 *  com.querydsl.core.types.dsl.SimpleExpression
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server.querydsl;

import com.querydsl.core.ResultTransformer;
import com.querydsl.core.group.GroupByBuilder;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000T\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0000\u001aV\u0010\u0000\u001a&\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002 \u0003*\u0012\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002\u0018\u00010\u00010\u0001\"\u0006\b\u0000\u0010\u0002\u0018\u00012\u001a\u0010\u0004\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00060\u0005\"\u0006\u0012\u0002\b\u00030\u0006H\u0086\b\u00a2\u0006\u0002\u0010\u0007\u001a\u00c2\u0001\u0010\b\u001a\u0096\u0001\u0012D\u0012B\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u000bH\u000b\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\fH\f \u0003* \u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u000bH\u000b\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\fH\f\u0018\u00010\r0\n \u0003*J\u0012D\u0012B\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u000bH\u000b\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\fH\f \u0003* \u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u000bH\u000b\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\fH\f\u0018\u00010\r0\n\u0018\u00010\t0\t\"\u0004\b\u0000\u0010\f\"\u0004\b\u0001\u0010\u000b*\b\u0012\u0004\u0012\u0002H\u000b0\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\f0\u0006H\u0086\b\u001aK\u0010\b\u001a&\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002 \u0003*\u0012\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002\u0018\u00010\u00100\u0010\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0012H\u0086\b\u001aE\u0010\b\u001a&\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002 \u0003*\u0012\u0012\f\u0012\n \u0003*\u0004\u0018\u0001H\u0002H\u0002\u0018\u00010\u00100\u0010\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0013H\u0086\b\u001a5\u0010\u0014\u001a\n \u0003*\u0004\u0018\u00010\u00150\u0015\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010\u0016\u001a\u000e\u0012\u0002\b\u0003\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0017H\u0086\b\u001a1\u0010\u0014\u001a\n \u0003*\u0004\u0018\u00010\u00150\u0015\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u000e\u0010\u0016\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0018H\u0086\b\u001a:\u0010\u0014\u001a\n \u0003*\u0004\u0018\u00010\u00150\u0015\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010\u0016\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0005\"\u0002H\u0002H\u0086\b\u00a2\u0006\u0002\u0010\u0019\u001aJ\u0010\u0014\u001a\n \u0003*\u0004\u0018\u00010\u00150\u0015\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\"\u0010\u0016\u001a\u0012\u0012\u000e\b\u0001\u0012\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00060\u0005\"\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0006H\u0086\b\u00a2\u0006\u0002\u0010\u001a\u001a1\u0010\u0014\u001a\n \u0003*\u0004\u0018\u00010\u00150\u0015\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u000e\u0010\u0016\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u001bH\u0086\b\u00a8\u0006\u001c"}, d2={"projection", "Lcom/querydsl/core/types/ConstructorExpression;", "T", "kotlin.jvm.PlatformType", "exprs", "", "Lcom/querydsl/core/types/Expression;", "([Lcom/querydsl/core/types/Expression;)Lcom/querydsl/core/types/ConstructorExpression;", "_as", "Lcom/querydsl/core/ResultTransformer;", "", "K", "V", "", "Lcom/querydsl/core/group/GroupByBuilder;", "expression", "Lcom/querydsl/core/types/dsl/SimpleExpression;", "alias", "Lcom/querydsl/core/types/Path;", "", "isIn", "Lcom/querydsl/core/types/dsl/BooleanExpression;", "right", "Lcom/querydsl/core/types/CollectionExpression;", "Lcom/querydsl/core/types/SubQueryExpression;", "(Lcom/querydsl/core/types/dsl/SimpleExpression;[Ljava/lang/Object;)Lcom/querydsl/core/types/dsl/BooleanExpression;", "(Lcom/querydsl/core/types/dsl/SimpleExpression;[Lcom/querydsl/core/types/Expression;)Lcom/querydsl/core/types/dsl/BooleanExpression;", "", "analytics"})
public final class KotlinQueryDslExtensionsKt {
    public static final <T> BooleanExpression isIn(@NotNull SimpleExpression<T> $this$isIn, @NotNull Collection<? extends T> right) {
        Intrinsics.checkNotNullParameter($this$isIn, (String)"<this>");
        Intrinsics.checkNotNullParameter(right, (String)"right");
        boolean $i$f$isIn = false;
        return $this$isIn.in(right);
    }

    public static final <T> BooleanExpression isIn(@NotNull SimpleExpression<T> $this$isIn, T ... right) {
        Intrinsics.checkNotNullParameter($this$isIn, (String)"<this>");
        Intrinsics.checkNotNullParameter(right, (String)"right");
        boolean $i$f$isIn = false;
        return $this$isIn.in((Object[])Arrays.copyOf(right, right.length));
    }

    public static final <T> BooleanExpression isIn(@NotNull SimpleExpression<T> $this$isIn, @NotNull CollectionExpression<?, ? extends T> right) {
        Intrinsics.checkNotNullParameter($this$isIn, (String)"<this>");
        Intrinsics.checkNotNullParameter(right, (String)"right");
        boolean $i$f$isIn = false;
        return $this$isIn.in(right);
    }

    public static final <T> BooleanExpression isIn(@NotNull SimpleExpression<T> $this$isIn, @NotNull SubQueryExpression<? extends T> right) {
        Intrinsics.checkNotNullParameter($this$isIn, (String)"<this>");
        Intrinsics.checkNotNullParameter(right, (String)"right");
        boolean $i$f$isIn = false;
        return $this$isIn.in(right);
    }

    public static final <T> BooleanExpression isIn(@NotNull SimpleExpression<T> $this$isIn, Expression<? extends T> ... right) {
        Intrinsics.checkNotNullParameter($this$isIn, (String)"<this>");
        Intrinsics.checkNotNullParameter(right, (String)"right");
        boolean $i$f$isIn = false;
        return $this$isIn.in(Arrays.copyOf(right, right.length));
    }

    public static final <T> SimpleExpression<T> _as(@NotNull SimpleExpression<T> $this$_as, @NotNull Path<T> alias) {
        Intrinsics.checkNotNullParameter($this$_as, (String)"<this>");
        Intrinsics.checkNotNullParameter(alias, (String)"alias");
        boolean $i$f$_as = false;
        return $this$_as.as(alias);
    }

    public static final <T> SimpleExpression<T> _as(@NotNull SimpleExpression<T> $this$_as, @NotNull String alias) {
        Intrinsics.checkNotNullParameter($this$_as, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)alias, (String)"alias");
        boolean $i$f$_as = false;
        return $this$_as.as(alias);
    }

    public static final /* synthetic */ <T> ConstructorExpression<T> projection(Expression<?> ... exprs) {
        Intrinsics.checkNotNullParameter(exprs, (String)"exprs");
        boolean $i$f$projection = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return Projections.constructor(Object.class, Arrays.copyOf(exprs, exprs.length));
    }

    public static final <V, K> ResultTransformer<Map<K, V>> _as(@NotNull GroupByBuilder<K> $this$_as, @NotNull Expression<V> expression) {
        Intrinsics.checkNotNullParameter($this$_as, (String)"<this>");
        Intrinsics.checkNotNullParameter(expression, (String)"expression");
        boolean $i$f$_as = false;
        return $this$_as.as(expression);
    }
}

