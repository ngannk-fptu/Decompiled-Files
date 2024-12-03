/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.JoinFlag;
import com.querydsl.core.Query;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalFunctionCall;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.WithBuilder;

public interface SQLCommonQuery<Q extends SQLCommonQuery<Q>>
extends Query<Q> {
    public Q addFlag(QueryFlag.Position var1, Expression<?> var2);

    public Q addFlag(QueryFlag.Position var1, String var2);

    public Q addFlag(QueryFlag.Position var1, String var2, Expression<?> var3);

    public Q addJoinFlag(String var1);

    public Q addJoinFlag(String var1, JoinFlag.Position var2);

    public Q from(Expression<?> ... var1);

    public Q from(SubQueryExpression<?> var1, Path<?> var2);

    public Q fullJoin(EntityPath<?> var1);

    public <E> Q fullJoin(EntityPath<E> var1, Path<E> var2);

    public <E> Q fullJoin(RelationalFunctionCall<E> var1, Path<E> var2);

    public <E> Q fullJoin(ForeignKey<E> var1, RelationalPath<E> var2);

    public Q fullJoin(SubQueryExpression<?> var1, Path<?> var2);

    public Q innerJoin(EntityPath<?> var1);

    public <E> Q innerJoin(EntityPath<E> var1, Path<E> var2);

    public <E> Q innerJoin(RelationalFunctionCall<E> var1, Path<E> var2);

    public <E> Q innerJoin(ForeignKey<E> var1, RelationalPath<E> var2);

    public Q innerJoin(SubQueryExpression<?> var1, Path<?> var2);

    public Q join(EntityPath<?> var1);

    public <E> Q join(EntityPath<E> var1, Path<E> var2);

    public <E> Q join(RelationalFunctionCall<E> var1, Path<E> var2);

    public <E> Q join(ForeignKey<E> var1, RelationalPath<E> var2);

    public Q join(SubQueryExpression<?> var1, Path<?> var2);

    public Q leftJoin(EntityPath<?> var1);

    public <E> Q leftJoin(EntityPath<E> var1, Path<E> var2);

    public <E> Q leftJoin(RelationalFunctionCall<E> var1, Path<E> var2);

    public <E> Q leftJoin(ForeignKey<E> var1, RelationalPath<E> var2);

    public Q leftJoin(SubQueryExpression<?> var1, Path<?> var2);

    public Q on(Predicate ... var1);

    public Q rightJoin(EntityPath<?> var1);

    public <E> Q rightJoin(EntityPath<E> var1, Path<E> var2);

    public <E> Q rightJoin(RelationalFunctionCall<E> var1, Path<E> var2);

    public <E> Q rightJoin(ForeignKey<E> var1, RelationalPath<E> var2);

    public Q rightJoin(SubQueryExpression<?> var1, Path<?> var2);

    public Q with(Path<?> var1, SubQueryExpression<?> var2);

    public Q with(Path<?> var1, Expression<?> var2);

    public WithBuilder<Q> with(Path<?> var1, Path<?> ... var2);

    public Q withRecursive(Path<?> var1, SubQueryExpression<?> var2);

    public Q withRecursive(Path<?> var1, Expression<?> var2);

    public WithBuilder<Q> withRecursive(Path<?> var1, Path<?> ... var2);
}

