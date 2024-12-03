/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.atlassian.data.activeobjects.repository.query.ParameterMetadataProvider;
import com.atlassian.data.activeobjects.repository.query.QueryDSLQuery;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.atlassian.data.activeobjects.repository.support.QuerydslUtils;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QMap;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

class ActiveObjectsQueryCreator
extends AbstractQueryCreator<Queryable, Predicate> {
    private static final Logger logger = LoggerFactory.getLogger(ActiveObjectsQueryCreator.class);
    protected final PartTree tree;
    private final ParameterMetadataProvider provider;
    private final ReturnedType returnedType;
    private final SQLQueryFactory sqlQueryFactory;
    protected final RelationalPathBase<?> qdslType;
    private final EscapeCharacter escape;
    private final PathBuilder<?> builder;

    public ActiveObjectsQueryCreator(PartTree tree, ReturnedType returnedType, SQLQueryFactory sqlQueryFactory, ParameterMetadataProvider provider, RelationalPathBase<?> qdslType) {
        super(tree);
        this.tree = tree;
        this.returnedType = returnedType;
        this.sqlQueryFactory = sqlQueryFactory;
        this.provider = provider;
        this.qdslType = qdslType;
        this.builder = new PathBuilder(qdslType.getType(), qdslType.getMetadata());
        this.escape = provider.getEscape();
    }

    @Override
    protected Predicate create(Part part, Iterator<Object> iterator) {
        return this.toPredicate(part, this.qdslType);
    }

    private Predicate toPredicate(Part part, RelationalPathBase<?> qdslType) {
        Part.Type type = part.getType();
        logger.debug("Got part: [{}] + property: [{}]", (Object)part, (Object)part.getProperty());
        switch (type) {
            case AFTER: 
            case GREATER_THAN: {
                return this.getComparablePath(part, qdslType).gt(this.provider.next(part, Comparable.class).getExpression());
            }
            case GREATER_THAN_EQUAL: {
                return this.getComparablePath(part, qdslType).goe(this.provider.next(part, Comparable.class).getExpression());
            }
            case BEFORE: 
            case LESS_THAN: {
                return this.getComparablePath(part, qdslType).lt(this.provider.next(part, Comparable.class).getExpression());
            }
            case LESS_THAN_EQUAL: {
                return this.getComparablePath(part, qdslType).loe(this.provider.next(part, Comparable.class).getExpression());
            }
            case IS_NULL: {
                return this.getTypedPath(part, qdslType).isNull();
            }
            case IS_NOT_NULL: {
                return this.getTypedPath(part, qdslType).isNotNull();
            }
            case NOT_IN: {
                throw new IllegalArgumentException("INs don't work with QueryDSL binding params but work fine when directly adding collection to predicate - see https://github.com/querydsl/querydsl/issues/1393");
            }
            case IN: {
                throw new IllegalArgumentException("INs don't work with QueryDSL binding params but work fine when directly adding collection to predicate - see https://github.com/querydsl/querydsl/issues/1393");
            }
            case STARTING_WITH: 
            case ENDING_WITH: 
            case CONTAINING: 
            case NOT_CONTAINING: {
                if (part.getProperty().getLeafProperty().isCollection()) {
                    SimplePath typedPath = this.getTypedPath(part, qdslType);
                    logger.debug("prop: [{}]", (Object)part.getProperty().getLeafProperty());
                    ParamExpression paramExpression = this.provider.next(part).getExpression();
                    return type.equals((Object)Part.Type.NOT_CONTAINING) ? typedPath.neAny((CollectionExpression)((Object)paramExpression)) : typedPath.eqAny((CollectionExpression)((Object)paramExpression));
                }
            }
            case LIKE: 
            case NOT_LIKE: {
                SimplePath typedPath = this.getTypedPath(part, qdslType);
                StringExpression stringExpression = Expressions.asString(this.upperIfIgnoreCase(typedPath, part));
                SimpleExpression<String> parameterExpression = this.upperIfIgnoreCase(this.provider.next(part, String.class).getExpression(), part);
                BooleanExpression like = stringExpression.like(parameterExpression, this.escape.getEscapeCharacter());
                return type.equals((Object)Part.Type.NOT_LIKE) || type.equals((Object)Part.Type.NOT_CONTAINING) ? like.not() : like;
            }
            case TRUE: {
                return this.getBooleanPath(part, qdslType).isTrue();
            }
            case FALSE: {
                return this.getBooleanPath(part, qdslType).isFalse();
            }
            case SIMPLE_PROPERTY: {
                ParameterMetadataProvider.ParameterMetadata expression = this.provider.next(part);
                SimplePath predicate = this.getTypedPath(part, qdslType);
                return expression.isIsNullParameter() ? predicate.isNull() : this.upperIfIgnoreCase(predicate, part).eq(this.upperIfIgnoreCase(expression.getExpression(), part));
            }
            case NEGATING_SIMPLE_PROPERTY: {
                SimplePath expressionForProperty = this.getTypedPath(part, qdslType);
                SimpleExpression right = this.upperIfIgnoreCase(this.provider.next(part).getExpression(), part);
                return this.upperIfIgnoreCase(expressionForProperty, part).ne(right);
            }
            case IS_EMPTY: 
            case IS_NOT_EMPTY: {
                if (!part.getProperty().getLeafProperty().isCollection()) {
                    throw new IllegalArgumentException("IsEmpty / IsNotEmpty can only be used on collection properties!");
                }
                throw new IllegalArgumentException("IsEmpty / IsNotEmpty currently not supported due to AO limitations around assoications/fetching!");
            }
        }
        throw new IllegalArgumentException("Unsupported keyword " + (Object)((Object)type));
    }

    private BooleanExpression getBooleanPath(Part part, RelationalPathBase<?> qdslType) {
        return Expressions.asBoolean(this.getTypedPath(part, qdslType));
    }

    private <T> ComparableExpression<Comparable<T>> getComparablePath(Part part, RelationalPathBase<?> qdslType) {
        return Expressions.asComparable(this.getTypedPath(part, qdslType));
    }

    private <T> SimplePath<T> getTypedPath(Part part, RelationalPathBase<?> qdslType) {
        return QuerydslUtils.getExpressionForProperty(part.getProperty().getSegment(), qdslType, this.builder);
    }

    private <T> SimpleExpression<T> upperIfIgnoreCase(Expression<? extends T> expression, Part part) {
        switch (part.shouldIgnoreCase()) {
            case ALWAYS: {
                Assert.state((boolean)this.canUpperCase(expression), (String)("Unable to ignore case of " + expression.getType().getName() + " types, the property '" + part.getProperty().getSegment() + "' must reference a String"));
                return Expressions.asString(expression).upper();
            }
            case WHEN_POSSIBLE: {
                if (!this.canUpperCase(expression)) break;
                return Expressions.asString(expression).upper();
            }
        }
        return (SimpleExpression)expression;
    }

    private boolean canUpperCase(Expression<?> expression) {
        return String.class.equals(expression.getType());
    }

    @Override
    protected Predicate and(Part part, Predicate base, Iterator<Object> iterator) {
        return Expressions.asBoolean(base).and(this.toPredicate(part, this.qdslType));
    }

    @Override
    protected Predicate or(Predicate base, Predicate criteria) {
        return Expressions.asBoolean(base).or(criteria);
    }

    @Override
    protected Queryable complete(Predicate predicate, Sort sort) {
        return this.complete(predicate, sort, this.sqlQueryFactory);
    }

    protected Queryable complete(Predicate predicate, Sort sort, SQLQueryFactory sqlQueryFactory) {
        SQLQuery query;
        logger.debug("Completing predicate: [{}] with sort: [{}]", (Object)predicate, (Object)sort);
        if (this.returnedType.needsCustomConstruction()) {
            Class<?> typeToRead = this.returnedType.getTypeToRead();
            boolean tupleQueryRequired = typeToRead == null || this.tree.isExistsProjection();
            logger.debug("Type to return: [{}], tuple required: [{}]", typeToRead, (Object)tupleQueryRequired);
            ArrayList exprs = new ArrayList();
            for (String property : this.returnedType.getInputProperties()) {
                SimplePath expressionForProperty = QuerydslUtils.getExpressionForProperty(property, this.qdslType, this.builder);
                exprs.add(expressionForProperty);
            }
            if (tupleQueryRequired) {
                QMap tupleQueryMap = Projections.map(exprs.toArray(new Expression[0]));
                query = (SQLQuery)sqlQueryFactory.select((Expression)tupleQueryMap).from((Expression<?>)this.qdslType);
            } else {
                query = (SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(typeToRead, exprs.toArray(new Expression[0]))).from((Expression<?>)this.qdslType);
            }
        } else {
            query = this.tree.isExistsProjection() ? (SQLQuery)sqlQueryFactory.select((Expression)this.qdslType.getPrimaryKey().getProjection()).from((Expression<?>)this.qdslType) : (SQLQuery)sqlQueryFactory.select((Expression)this.qdslType).from((Expression<?>)this.qdslType);
        }
        if (this.tree.isDistinct()) {
            query.distinct();
        }
        QuerydslUtils.applySorting(sort, query, this.qdslType, this.builder);
        query = predicate == null ? query : (SQLQuery)query.where(predicate);
        logger.debug("Created SQLQuery: [{}]", (Object)query);
        return new QueryDSLQuery(query, this.qdslType);
    }

    public List<ParameterMetadataProvider.ParameterMetadata<?>> getParameterExpressions() {
        return this.provider.getExpressions();
    }
}

