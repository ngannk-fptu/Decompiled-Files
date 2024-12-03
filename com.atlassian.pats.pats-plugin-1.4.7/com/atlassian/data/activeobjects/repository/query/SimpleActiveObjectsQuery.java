/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQueryExecution;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryExecutions;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.DeclaredQuery;
import com.atlassian.data.activeobjects.repository.query.ExpressionBasedStringQuery;
import com.atlassian.data.activeobjects.repository.query.ParameterBinder;
import com.atlassian.data.activeobjects.repository.query.ParameterBinderFactory;
import com.atlassian.data.activeobjects.repository.query.QueryParameterSetter;
import com.atlassian.data.activeobjects.repository.query.QueryUtils;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityMetadata;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

public final class SimpleActiveObjectsQuery
extends AbstractActiveObjectsQuery {
    private final DeclaredQuery query;
    private final DeclaredQuery countQuery;
    private final QueryMethodEvaluationContextProvider evaluationContextProvider;
    private final SpelExpressionParser parser;

    public SimpleActiveObjectsQuery(ActiveObjectsQueryMethod<?> method, ActiveObjects em, DatabaseAccessor databaseAccessor, String queryString, QueryMethodEvaluationContextProvider evaluationContextProvider, SpelExpressionParser parser) {
        super(method, em, databaseAccessor);
        Assert.hasText((String)queryString, (String)"Query string must not be null or empty!");
        Assert.notNull((Object)evaluationContextProvider, (String)"ExpressionEvaluationContextProvider must not be null!");
        Assert.notNull((Object)parser, (String)"Parser must not be null!");
        this.evaluationContextProvider = evaluationContextProvider;
        this.query = new ExpressionBasedStringQuery(queryString, (ActiveObjectsEntityMetadata<?>)method.getEntityInformation(), parser);
        DeclaredQuery derivedCountQuery = this.query.deriveCountQuery(method.getCountQuery(), method.getCountQueryProjection());
        this.countQuery = ExpressionBasedStringQuery.from(derivedCountQuery, (ActiveObjectsEntityMetadata)method.getEntityInformation(), parser);
        this.parser = parser;
    }

    @Override
    public Queryable createQuery(Object[] values) {
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.getQueryMethod().getParameters(), values);
        String sortedQueryString = QueryUtils.applySorting(this.query.getQueryString(), accessor.getSort(), this.query.getAlias());
        Queryable createQuery = this.createActiveObjectsQuery(sortedQueryString);
        return ((ParameterBinder)this.parameterBinder.get()).bindAndPrepare(createQuery, values);
    }

    @Override
    protected ParameterBinder createBinder() {
        return ParameterBinderFactory.createQueryAwareBinder(this.getQueryMethod().getParameters(), this.query, this.parser, this.evaluationContextProvider);
    }

    @Override
    protected Queryable createCountQuery(Object[] values) {
        String queryString = this.countQuery.getQueryString();
        ActiveObjectsQuery select = new ActiveObjectsQuery(queryString);
        return ((ParameterBinder)this.parameterBinder.get()).bind(select, values, QueryParameterSetter.ErrorHandling.LENIENT);
    }

    public DeclaredQuery getQuery() {
        return this.query;
    }

    public DeclaredQuery getCountQuery() {
        return this.countQuery;
    }

    protected Queryable createActiveObjectsQuery(String queryString) {
        return new ActiveObjectsQuery(queryString);
    }

    @Override
    protected AbstractActiveObjectsQueryExecution getExecution() {
        if (this.getQueryMethod().isStreamQuery()) {
            return new ActiveObjectsQueryExecutions.StreamExecution();
        }
        if (this.getQueryMethod().isCollectionQuery()) {
            return new ActiveObjectsQueryExecutions.CollectionExecution();
        }
        if (this.getQueryMethod().isSliceQuery()) {
            return new ActiveObjectsQueryExecutions.SlicedExecution(this.getQueryMethod().getParameters());
        }
        if (this.getQueryMethod().isPageQuery()) {
            return new ActiveObjectsQueryExecutions.PagedExecution(this.getQueryMethod().getParameters());
        }
        return new ActiveObjectsQueryExecutions.SingleEntityExecution();
    }
}

