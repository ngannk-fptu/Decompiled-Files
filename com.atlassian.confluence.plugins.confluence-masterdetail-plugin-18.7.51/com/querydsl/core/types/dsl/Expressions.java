/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.core.types.dsl;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.PredicateTemplate;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.CollectionOperation;
import com.querydsl.core.types.dsl.CollectionPath;
import com.querydsl.core.types.dsl.ComparableEntityPath;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableOperation;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.ComparableTemplate;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateOperation;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.DateTimeOperation;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.DslOperation;
import com.querydsl.core.types.dsl.DslPath;
import com.querydsl.core.types.dsl.DslTemplate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.EnumOperation;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.EnumTemplate;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapPath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberOperation;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringOperation;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.core.types.dsl.TimeExpression;
import com.querydsl.core.types.dsl.TimeOperation;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.core.types.dsl.TimeTemplate;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class Expressions {
    public static final NumberExpression<Integer> ONE = Expressions.numberTemplate(Integer.class, "1", new Object[0]);
    public static final NumberExpression<Integer> TWO = Expressions.numberTemplate(Integer.class, "2", new Object[0]);
    public static final NumberExpression<Integer> THREE = Expressions.numberTemplate(Integer.class, "3", new Object[0]);
    public static final NumberExpression<Integer> FOUR = Expressions.numberTemplate(Integer.class, "4", new Object[0]);
    public static final NumberExpression<Integer> ZERO = Expressions.numberTemplate(Integer.class, "0", new Object[0]);
    public static final BooleanExpression TRUE = Expressions.booleanTemplate("true", new Object[0]);
    public static final BooleanExpression FALSE = Expressions.booleanTemplate("false", new Object[0]);

    private Expressions() {
    }

    public static <D> SimpleExpression<D> as(Expression<D> source, Path<D> alias) {
        if (source == null) {
            return Expressions.as(Expressions.nullExpression(), alias);
        }
        return Expressions.operation(alias.getType(), Ops.ALIAS, source, alias);
    }

    public static DateExpression<Date> currentDate() {
        return DateExpression.currentDate();
    }

    public static DateTimeExpression<Date> currentTimestamp() {
        return DateTimeExpression.currentTimestamp();
    }

    public static TimeExpression<Time> currentTime() {
        return TimeExpression.currentTime();
    }

    public static <D> SimpleExpression<D> as(Expression<D> source, String alias) {
        return Expressions.as(source, ExpressionUtils.path(source.getType(), alias));
    }

    public static BooleanExpression allOf(BooleanExpression ... exprs) {
        BooleanExpression rv = null;
        for (BooleanExpression b : exprs) {
            rv = rv == null ? b : rv.and(b);
        }
        return rv;
    }

    public static BooleanExpression anyOf(BooleanExpression ... exprs) {
        BooleanExpression rv = null;
        for (BooleanExpression b : exprs) {
            rv = rv == null ? b : rv.or(b);
        }
        return rv;
    }

    public static <T> Expression<T> constant(T value) {
        return ConstantImpl.create(value);
    }

    public static <D> SimpleExpression<D> constantAs(D source, Path<D> alias) {
        if (source == null) {
            return Expressions.as(Expressions.nullExpression(), alias);
        }
        return Expressions.as(ConstantImpl.create(source), alias);
    }

    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    @Deprecated
    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    @Deprecated
    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    public static <T> SimpleTemplate<T> template(Class<? extends T> cl, Template template, List<?> args) {
        return Expressions.simpleTemplate(cl, template, args);
    }

    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.simpleTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.simpleTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.simpleTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.simpleTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new SimpleTemplate<T>(cl, template, args);
    }

    public static <T> SimpleTemplate<T> simpleTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new SimpleTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.dslTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.dslTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.dslTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.dslTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new DslTemplate<T>(cl, template, args);
    }

    public static <T> DslTemplate<T> dslTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new DslTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.comparableTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.comparableTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.comparableTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.comparableTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new ComparableTemplate<T>(cl, template, args);
    }

    public static <T extends Comparable<?>> ComparableTemplate<T> comparableTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new ComparableTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.dateTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.dateTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.dateTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.dateTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new DateTemplate<T>(cl, template, args);
    }

    public static <T extends Comparable<?>> DateTemplate<T> dateTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new DateTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.dateTimeTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.dateTimeTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.dateTimeTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.dateTimeTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new DateTimeTemplate<T>(cl, template, args);
    }

    public static <T extends Comparable<?>> DateTimeTemplate<T> dateTimeTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new DateTimeTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.timeTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.timeTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.timeTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.timeTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new TimeTemplate<T>(cl, template, args);
    }

    public static <T extends Comparable<?>> TimeTemplate<T> timeTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new TimeTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.enumTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.enumTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.enumTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.enumTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new EnumTemplate<T>(cl, template, args);
    }

    public static <T extends Enum<T>> EnumTemplate<T> enumTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new EnumTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, String template, Object ... args) {
        return Expressions.numberTemplate(cl, Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return Expressions.numberTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, String template, List<?> args) {
        return Expressions.numberTemplate(cl, Expressions.createTemplate(template), args);
    }

    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, Template template, Object ... args) {
        return Expressions.numberTemplate(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        return new NumberTemplate<T>(cl, template, args);
    }

    public static <T extends Number> NumberTemplate<T> numberTemplate(Class<? extends T> cl, Template template, List<?> args) {
        return new NumberTemplate<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static StringTemplate stringTemplate(String template, Object ... args) {
        return Expressions.stringTemplate(Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static StringTemplate stringTemplate(String template, ImmutableList<?> args) {
        return Expressions.stringTemplate(Expressions.createTemplate(template), args);
    }

    public static StringTemplate stringTemplate(String template, List<?> args) {
        return Expressions.stringTemplate(Expressions.createTemplate(template), args);
    }

    public static StringTemplate stringTemplate(Template template, Object ... args) {
        return Expressions.stringTemplate(template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static StringTemplate stringTemplate(Template template, ImmutableList<?> args) {
        return new StringTemplate(template, args);
    }

    public static StringTemplate stringTemplate(Template template, List<?> args) {
        return new StringTemplate(template, ImmutableList.copyOf(args));
    }

    public static BooleanTemplate booleanTemplate(String template, Object ... args) {
        return Expressions.booleanTemplate(Expressions.createTemplate(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static BooleanTemplate booleanTemplate(String template, ImmutableList<?> args) {
        return Expressions.booleanTemplate(Expressions.createTemplate(template), args);
    }

    public static BooleanTemplate booleanTemplate(String template, List<?> args) {
        return Expressions.booleanTemplate(Expressions.createTemplate(template), args);
    }

    public static BooleanTemplate booleanTemplate(Template template, Object ... args) {
        return Expressions.booleanTemplate(template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static BooleanTemplate booleanTemplate(Template template, ImmutableList<?> args) {
        return new BooleanTemplate(template, args);
    }

    public static BooleanTemplate booleanTemplate(Template template, List<?> args) {
        return new BooleanTemplate(template, ImmutableList.copyOf(args));
    }

    public static BooleanOperation predicate(Operator operator, Expression<?> ... args) {
        return new BooleanOperation(operator, args);
    }

    public static <T> SimpleOperation<T> operation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return Expressions.simpleOperation(type, operator, args);
    }

    public static <T> SimpleOperation<T> simpleOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new SimpleOperation<T>(type, operator, args);
    }

    public static <T> DslOperation<T> dslOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new DslOperation<T>(type, operator, args);
    }

    public static BooleanOperation booleanOperation(Operator operator, Expression<?> ... args) {
        return Expressions.predicate(operator, args);
    }

    public static <T extends Comparable<?>> ComparableOperation<T> comparableOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new ComparableOperation<T>(type, operator, args);
    }

    public static <T extends Comparable<?>> DateOperation<T> dateOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new DateOperation<T>(type, operator, args);
    }

    public static <T extends Comparable<?>> DateTimeOperation<T> dateTimeOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new DateTimeOperation<T>(type, operator, args);
    }

    public static <T extends Comparable<?>> TimeOperation<T> timeOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new TimeOperation<T>(type, operator, args);
    }

    public static <T extends Number> NumberOperation<T> numberOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new NumberOperation<T>(type, operator, args);
    }

    public static StringOperation stringOperation(Operator operator, Expression<?> ... args) {
        return new StringOperation(operator, args);
    }

    public static <T> SimplePath<T> path(Class<? extends T> type, String variable) {
        return Expressions.simplePath(type, variable);
    }

    public static <T> SimplePath<T> path(Class<? extends T> type, Path<?> parent, String property) {
        return Expressions.simplePath(type, parent, property);
    }

    public static <T> SimplePath<T> path(Class<? extends T> type, PathMetadata metadata) {
        return Expressions.simplePath(type, metadata);
    }

    public static <T> SimplePath<T> simplePath(Class<? extends T> type, String variable) {
        return new SimplePath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T> SimplePath<T> simplePath(Class<? extends T> type, Path<?> parent, String property) {
        return new SimplePath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T> SimplePath<T> simplePath(Class<? extends T> type, PathMetadata metadata) {
        return new SimplePath<T>(type, metadata);
    }

    public static <T> DslPath<T> dslPath(Class<? extends T> type, String variable) {
        return new DslPath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T> DslPath<T> dslPath(Class<? extends T> type, Path<?> parent, String property) {
        return new DslPath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T> DslPath<T> dslPath(Class<? extends T> type, PathMetadata metadata) {
        return new DslPath<T>(type, metadata);
    }

    public static <T extends Comparable<?>> ComparablePath<T> comparablePath(Class<? extends T> type, String variable) {
        return new ComparablePath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Comparable<?>> ComparablePath<T> comparablePath(Class<? extends T> type, Path<?> parent, String property) {
        return new ComparablePath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Comparable<?>> ComparablePath<T> comparablePath(Class<? extends T> type, PathMetadata metadata) {
        return new ComparablePath<T>(type, metadata);
    }

    public static <T extends Comparable<?>> ComparableEntityPath<T> comparableEntityPath(Class<? extends T> type, String variable) {
        return new ComparableEntityPath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Comparable<?>> ComparableEntityPath<T> comparableEntityPath(Class<? extends T> type, Path<?> parent, String property) {
        return new ComparableEntityPath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Comparable<?>> ComparableEntityPath<T> comparableEntityPath(Class<? extends T> type, PathMetadata metadata) {
        return new ComparableEntityPath<T>(type, metadata);
    }

    public static <T extends Comparable<?>> DatePath<T> datePath(Class<? extends T> type, String variable) {
        return new DatePath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Comparable<?>> DatePath<T> datePath(Class<? extends T> type, Path<?> parent, String property) {
        return new DatePath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Comparable<?>> DatePath<T> datePath(Class<? extends T> type, PathMetadata metadata) {
        return new DatePath<T>(type, metadata);
    }

    public static <T extends Comparable<?>> DateTimePath<T> dateTimePath(Class<? extends T> type, String variable) {
        return new DateTimePath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Comparable<?>> DateTimePath<T> dateTimePath(Class<? extends T> type, Path<?> parent, String property) {
        return new DateTimePath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Comparable<?>> DateTimePath<T> dateTimePath(Class<? extends T> type, PathMetadata metadata) {
        return new DateTimePath<T>(type, metadata);
    }

    public static <T extends Comparable<?>> TimePath<T> timePath(Class<? extends T> type, String variable) {
        return new TimePath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Comparable<?>> TimePath<T> timePath(Class<? extends T> type, Path<?> parent, String property) {
        return new TimePath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Comparable<?>> TimePath<T> timePath(Class<? extends T> type, PathMetadata metadata) {
        return new TimePath<T>(type, metadata);
    }

    public static <T extends Number> NumberPath<T> numberPath(Class<? extends T> type, String variable) {
        return new NumberPath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Number> NumberPath<T> numberPath(Class<? extends T> type, Path<?> parent, String property) {
        return new NumberPath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Number> NumberPath<T> numberPath(Class<? extends T> type, PathMetadata metadata) {
        return new NumberPath<T>(type, metadata);
    }

    public static StringPath stringPath(String variable) {
        return new StringPath(PathMetadataFactory.forVariable(variable));
    }

    public static StringPath stringPath(Path<?> parent, String property) {
        return new StringPath(PathMetadataFactory.forProperty(parent, property));
    }

    public static StringPath stringPath(PathMetadata metadata) {
        return new StringPath(metadata);
    }

    public static BooleanPath booleanPath(String variable) {
        return new BooleanPath(PathMetadataFactory.forVariable(variable));
    }

    public static BooleanPath booleanPath(Path<?> parent, String property) {
        return new BooleanPath(PathMetadataFactory.forProperty(parent, property));
    }

    public static BooleanPath booleanPath(PathMetadata metadata) {
        return new BooleanPath(metadata);
    }

    public static CaseBuilder cases() {
        return new CaseBuilder();
    }

    public static SimpleExpression<Tuple> list(SimpleExpression<?> ... exprs) {
        return Expressions.list(Tuple.class, exprs);
    }

    public static <T> SimpleExpression<T> list(Class<T> clazz, SimpleExpression<?> ... exprs) {
        SimpleExpression<?> rv = exprs[0];
        for (int i = 1; i < exprs.length; ++i) {
            rv = new SimpleOperation(clazz, (Operator)Ops.LIST, rv, exprs[i]);
        }
        return rv;
    }

    public static <T> Expression<T> list(Class<T> clazz, Expression<?> ... exprs) {
        Expression<?> rv = exprs[0];
        for (int i = 1; i < exprs.length; ++i) {
            rv = new SimpleOperation(clazz, (Operator)Ops.LIST, rv, exprs[i]);
        }
        return rv;
    }

    public static <T> SimpleExpression<T> set(Class<T> clazz, SimpleExpression<?> ... exprs) {
        SimpleExpression<?> rv = exprs[0];
        for (int i = 1; i < exprs.length; ++i) {
            rv = new SimpleOperation(clazz, (Operator)Ops.SET, rv, exprs[i]);
        }
        return rv;
    }

    public static <T> Expression<T> set(Class<T> clazz, Expression<?> ... exprs) {
        Expression<?> rv = exprs[0];
        for (int i = 1; i < exprs.length; ++i) {
            rv = new SimpleOperation(clazz, (Operator)Ops.SET, rv, exprs[i]);
        }
        return rv;
    }

    public static Expression<Tuple> list(Expression<?> ... exprs) {
        return Expressions.list(Tuple.class, exprs);
    }

    public static Expression<Tuple> set(Expression<?> ... exprs) {
        return Expressions.set(Tuple.class, exprs);
    }

    public static <T> NullExpression<T> nullExpression() {
        return NullExpression.DEFAULT;
    }

    public static <T> NullExpression<T> nullExpression(Class<T> type) {
        return Expressions.nullExpression();
    }

    public static <T> NullExpression<T> nullExpression(Path<T> path) {
        return Expressions.nullExpression();
    }

    public static <T extends Enum<T>> EnumOperation<T> enumOperation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return new EnumOperation<T>(type, operator, args);
    }

    public static <T extends Enum<T>> EnumPath<T> enumPath(Class<? extends T> type, String variable) {
        return new EnumPath<T>(type, PathMetadataFactory.forVariable(variable));
    }

    public static <T extends Enum<T>> EnumPath<T> enumPath(Class<? extends T> type, Path<?> parent, String property) {
        return new EnumPath<T>(type, PathMetadataFactory.forProperty(parent, property));
    }

    public static <T extends Enum<T>> EnumPath<T> enumPath(Class<? extends T> type, PathMetadata metadata) {
        return new EnumPath<T>(type, metadata);
    }

    public static <T> CollectionExpression<Collection<T>, T> collectionOperation(Class<T> elementType, Operator operator, Expression<?> ... args) {
        return new CollectionOperation<T>(elementType, operator, args);
    }

    public static <E, Q extends SimpleExpression<? super E>> CollectionPath<E, Q> collectionPath(Class<E> type, Class<Q> queryType, PathMetadata metadata) {
        return new CollectionPath<E, Q>(type, queryType, metadata);
    }

    public static <E, Q extends SimpleExpression<? super E>> ListPath<E, Q> listPath(Class<E> type, Class<Q> queryType, PathMetadata metadata) {
        return new ListPath<E, Q>(type, queryType, metadata);
    }

    public static <E, Q extends SimpleExpression<? super E>> SetPath<E, Q> setPath(Class<E> type, Class<Q> queryType, PathMetadata metadata) {
        return new SetPath<E, Q>(type, queryType, metadata);
    }

    public static <K, V, E extends SimpleExpression<? super V>> MapPath<K, V, E> mapPath(Class<? super K> keyType, Class<? super V> valueType, Class<E> queryType, PathMetadata metadata) {
        return new MapPath<K, V, E>(keyType, valueType, queryType, metadata);
    }

    public static <A, E> ArrayPath<A, E> arrayPath(Class<A> arrayType, String variable) {
        return new ArrayPath(arrayType, variable);
    }

    public static <A, E> ArrayPath<A, E> arrayPath(Class<A> arrayType, Path<?> parent, String property) {
        return new ArrayPath(arrayType, parent, property);
    }

    public static <A, E> ArrayPath<A, E> arrayPath(Class<A> arrayType, PathMetadata metadata) {
        return new ArrayPath(arrayType, metadata);
    }

    private static Template createTemplate(String template) {
        return TemplateFactory.DEFAULT.create(template);
    }

    public static BooleanExpression asBoolean(Expression<Boolean> expr) {
        Expression<Boolean> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new BooleanPath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof PredicateOperation) {
            return new BooleanOperation((PredicateOperation)underlyingMixin);
        }
        if (underlyingMixin instanceof PredicateTemplate) {
            return new BooleanTemplate((PredicateTemplate)underlyingMixin);
        }
        return new BooleanExpression((Expression)underlyingMixin){
            private static final long serialVersionUID = -8712299418891960222L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static BooleanExpression asBoolean(boolean value) {
        return Expressions.asBoolean(Expressions.constant(value));
    }

    public static <T extends Comparable<?>> ComparableExpression<T> asComparable(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new ComparablePath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new ComparableOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new ComparableTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new ComparableExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = 389920618099394430L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Comparable<?>> ComparableExpression<T> asComparable(T value) {
        return Expressions.asComparable(Expressions.constant(value));
    }

    public static <T extends Comparable<?>> DateExpression<T> asDate(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new DatePath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new DateOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new DateTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new DateExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = 389920618099394430L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Comparable<?>> DateExpression<T> asDate(T value) {
        return Expressions.asDate(Expressions.constant(value));
    }

    public static <T extends Comparable<?>> DateTimeExpression<T> asDateTime(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new DateTimePath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new DateTimeOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new DateTimeTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new DateTimeExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = 8007203530480765244L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Comparable<?>> DateTimeExpression<T> asDateTime(T value) {
        return Expressions.asDateTime(Expressions.constant(value));
    }

    public static <T extends Comparable<?>> TimeExpression<T> asTime(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new TimePath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new TimeOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new TimeTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new TimeExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = -2402288239000668173L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Comparable<?>> TimeExpression<T> asTime(T value) {
        return Expressions.asTime(Expressions.constant(value));
    }

    public static <T extends Enum<T>> EnumExpression<T> asEnum(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new EnumPath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new EnumOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new EnumTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new EnumExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = 949681836002045152L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Enum<T>> EnumExpression<T> asEnum(T value) {
        return Expressions.asEnum(Expressions.constant(value));
    }

    public static <T extends Number> NumberExpression<T> asNumber(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new NumberPath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new NumberOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new NumberTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new NumberExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = -8712299418891960222L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T extends Number> NumberExpression<T> asNumber(T value) {
        return Expressions.asNumber(Expressions.constant(value));
    }

    public static StringExpression asString(Expression<String> expr) {
        Expression<String> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new StringPath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new StringOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new StringTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new StringExpression((Expression)underlyingMixin){
            private static final long serialVersionUID = 8007203530480765244L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static StringExpression asString(String value) {
        return Expressions.asString(Expressions.constant(value));
    }

    public static <T> SimpleExpression<T> asSimple(Expression<T> expr) {
        Expression<T> underlyingMixin = ExpressionUtils.extract(expr);
        if (underlyingMixin instanceof PathImpl) {
            return new SimplePath((PathImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof OperationImpl) {
            return new SimpleOperation((OperationImpl)underlyingMixin);
        }
        if (underlyingMixin instanceof TemplateExpressionImpl) {
            return new SimpleTemplate((TemplateExpressionImpl)underlyingMixin);
        }
        return new SimpleExpression<T>((Expression)underlyingMixin){
            private static final long serialVersionUID = -8712299418891960222L;

            @Override
            public <R, C> R accept(Visitor<R, C> v, C context) {
                return this.mixin.accept(v, context);
            }
        };
    }

    public static <T> SimpleExpression<T> asSimple(T value) {
        return Expressions.asSimple(Expressions.constant(value));
    }
}

