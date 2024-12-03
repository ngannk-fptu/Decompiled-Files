/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  javax.annotation.Nullable
 */
package com.querydsl.core.alias;

import com.google.common.base.CaseFormat;
import com.querydsl.core.alias.AliasFactory;
import com.querydsl.core.alias.DefaultPathFactory;
import com.querydsl.core.alias.DefaultTypeSystem;
import com.querydsl.core.alias.ManagedObject;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.CollectionPath;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class Alias {
    private static final AliasFactory aliasFactory = new AliasFactory(new DefaultPathFactory(), new DefaultTypeSystem());
    private static final SimplePath<Object> it = Expressions.path(Object.class, "it");

    public static <D extends Expression<?>> D $() {
        return (D)aliasFactory.getCurrentAndReset();
    }

    public static <D> ArrayPath<D[], D> $(D[] arg) {
        return (ArrayPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<BigDecimal> $(BigDecimal arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<BigInteger> $(BigInteger arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static BooleanPath $(Boolean arg) {
        return (BooleanPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<Byte> $(Byte arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static <T extends Enum<T>> EnumPath<T> $(T arg) {
        return (EnumPath)aliasFactory.getCurrentAndReset();
    }

    public static <D> CollectionPath<D, SimpleExpression<D>> $(Collection<D> arg) {
        return (CollectionPath)aliasFactory.getCurrentAndReset();
    }

    public static <D extends Comparable<?>> ComparablePath<D> $(D arg) {
        return (ComparablePath)Alias.getPath(arg);
    }

    public static NumberPath<Double> $(Double arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<Float> $(Float arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<Integer> $(Integer arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static DatePath<Date> $(Date arg) {
        return (DatePath)aliasFactory.getCurrentAndReset();
    }

    public static DateTimePath<java.util.Date> $(java.util.Date arg) {
        return (DateTimePath)aliasFactory.getCurrentAndReset();
    }

    public static <D> ListPath<D, SimpleExpression<D>> $(List<D> arg) {
        return (ListPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<Long> $(Long arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static <K, V> MapPath<K, V, SimpleExpression<V>> $(Map<K, V> arg) {
        return (MapPath)aliasFactory.getCurrentAndReset();
    }

    public static <D> SetPath<D, SimpleExpression<D>> $(Set<D> arg) {
        return (SetPath)aliasFactory.getCurrentAndReset();
    }

    public static NumberPath<Short> $(Short arg) {
        return (NumberPath)aliasFactory.getCurrentAndReset();
    }

    public static StringPath $(String arg) {
        return (StringPath)aliasFactory.getCurrentAndReset();
    }

    public static TimePath<Time> $(Time arg) {
        return (TimePath)aliasFactory.getCurrentAndReset();
    }

    public static DateTimePath<Timestamp> $(Timestamp arg) {
        return (DateTimePath)aliasFactory.getCurrentAndReset();
    }

    @Nullable
    public static <D> EntityPathBase<D> $(D arg) {
        EntityPathBase rv = (EntityPathBase)aliasFactory.getCurrentAndReset();
        if (rv != null) {
            return rv;
        }
        if (arg instanceof EntityPath) {
            return (EntityPathBase)arg;
        }
        if (arg instanceof ManagedObject) {
            return (EntityPathBase)((ManagedObject)arg).__mappedPath();
        }
        return null;
    }

    @Nullable
    private static <D, P extends Path<D>> P getPath(D arg) {
        Path rv = (Path)aliasFactory.getCurrentAndReset();
        if (rv != null) {
            return (P)rv;
        }
        if (arg instanceof Path) {
            return (P)((Path)arg);
        }
        if (arg instanceof ManagedObject) {
            return (P)((ManagedObject)arg).__mappedPath();
        }
        return null;
    }

    public static <A> A alias(Class<A> cl) {
        return Alias.alias(cl, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, cl.getSimpleName()));
    }

    public static <A> A alias(Class<A> cl, Expression<? extends A> expr) {
        return aliasFactory.createAliasForExpr(cl, expr);
    }

    public static <A> A alias(Class<A> cl, String var) {
        return aliasFactory.createAliasForVariable(cl, var);
    }

    public static <D> Expression<D> getAny(D arg) {
        Object current = aliasFactory.getCurrentAndReset();
        if (current != null) {
            return current;
        }
        if (arg instanceof ManagedObject) {
            return ((ManagedObject)arg).__mappedPath();
        }
        throw new IllegalArgumentException("No path mapped to " + arg);
    }

    public static void resetAlias() {
        aliasFactory.reset();
    }

    public static <D> SimplePath<D> var() {
        return it;
    }

    public static <D extends Comparable<?>> ComparablePath<D> var(D arg) {
        return Expressions.comparablePath(arg.getClass(), "var" + arg);
    }

    public static <D extends Number> NumberPath<D> var(D arg) {
        return Expressions.numberPath(arg.getClass(), "var" + arg.getClass().getSimpleName() + arg);
    }

    public static <D> EntityPathBase<D> var(D arg) {
        String var = "var" + arg.getClass().getSimpleName() + "_" + arg.toString().replace(' ', '_');
        return new PathBuilder(arg.getClass(), var);
    }

    public static StringPath var(String arg) {
        return Expressions.stringPath(arg.replace(' ', '_'));
    }

    private Alias() {
    }
}

