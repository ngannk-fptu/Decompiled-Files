/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.Nullable
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQueryExecution;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsCountQueryCreator;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParameters;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryCreator;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.atlassian.data.activeobjects.repository.query.ParameterBinder;
import com.atlassian.data.activeobjects.repository.query.ParameterBinderFactory;
import com.atlassian.data.activeobjects.repository.query.ParameterMetadataProvider;
import com.atlassian.data.activeobjects.repository.query.PocketKnifeQueryExecutions;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.pocketknife.spi.querydsl.EnhancedRelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.Nullable;

public class PartTreeActiveObjectsQuery
extends AbstractActiveObjectsQuery {
    private static final Logger logger = LoggerFactory.getLogger(PartTreeActiveObjectsQuery.class);
    private final PartTree tree;
    private final ActiveObjectsParameters parameters;
    private final QueryPreparer query;
    private final QueryPreparer countQuery;
    private final EscapeCharacter escapeCharacter;
    private final EntityPathResolver entityPathResolver;

    PartTreeActiveObjectsQuery(ActiveObjectsQueryMethod<?> method, ActiveObjects activeObjects, DatabaseAccessor databaseAccessor, EntityPathResolver entityPathResolver, EscapeCharacter escapeCharacter) {
        super(method, activeObjects, databaseAccessor);
        this.entityPathResolver = entityPathResolver;
        this.escapeCharacter = escapeCharacter;
        Class domainClass = method.getEntityInformation().getJavaType();
        this.parameters = method.getParameters();
        boolean recreationRequired = this.parameters.hasDynamicProjection() || this.parameters.potentiallySortsDynamically();
        try {
            this.tree = new PartTree(method.getName(), domainClass);
            this.countQuery = new CountQueryPreparer(recreationRequired);
            this.query = this.tree.isCountProjection() ? this.countQuery : new QueryPreparer(recreationRequired);
        }
        catch (Exception e) {
            String error = String.format("Failed to create query for method %s! %s", method, e.getMessage());
            throw new IllegalArgumentException(error, e);
        }
    }

    @Override
    public Queryable createQuery(Object[] values) {
        return this.query.createQuery(values);
    }

    @Override
    public Queryable createCountQuery(Object[] values) {
        return this.countQuery.createQuery(values);
    }

    @Override
    protected AbstractActiveObjectsQueryExecution getExecution() {
        if (this.tree.isDelete()) {
            return new PocketKnifeQueryExecutions.DeleteExecution();
        }
        if (this.tree.isExistsProjection()) {
            return new PocketKnifeQueryExecutions.ExistsExecution();
        }
        if (this.getQueryMethod().isStreamQuery()) {
            return new PocketKnifeQueryExecutions.StreamExecution();
        }
        if (this.getQueryMethod().isCollectionQuery()) {
            return new PocketKnifeQueryExecutions.CollectionExecution();
        }
        if (this.getQueryMethod().isSliceQuery()) {
            return new PocketKnifeQueryExecutions.SlicedExecution(this.getQueryMethod().getParameters());
        }
        if (this.getQueryMethod().isPageQuery()) {
            return new PocketKnifeQueryExecutions.PagedExecution(this.getQueryMethod().getParameters());
        }
        return new PocketKnifeQueryExecutions.SingleEntityExecution();
    }

    private class CountQueryPreparer
    extends QueryPreparer {
        CountQueryPreparer(boolean recreateQueries) {
            super(recreateQueries);
        }

        @Override
        protected ActiveObjectsQueryCreator createCreator(Optional<ParametersParameterAccessor> accessor) {
            SQLQueryFactory sqlQueryFactory = PartTreeActiveObjectsQuery.this.getDatabaseAccessor().runInTransaction(DatabaseConnection::query, OnRollback.NOOP);
            ParameterMetadataProvider provider = new ParameterMetadataProvider(PartTreeActiveObjectsQuery.this.parameters, PartTreeActiveObjectsQuery.this.escapeCharacter);
            EnhancedRelationalPathBase qdslType = (EnhancedRelationalPathBase)PartTreeActiveObjectsQuery.this.entityPathResolver.createPath(PartTreeActiveObjectsQuery.this.getQueryMethod().getEntityInformation().getJavaType());
            return new ActiveObjectsCountQueryCreator(PartTreeActiveObjectsQuery.this.tree, PartTreeActiveObjectsQuery.this.getQueryMethod().getResultProcessor().getReturnedType(), sqlQueryFactory, provider, qdslType);
        }

        @Override
        protected Queryable invokeBinding(ParameterBinder binder, Queryable query, Object[] values) {
            return binder.bind(query, values);
        }
    }

    private class QueryPreparer {
        @Nullable
        private final Queryable cachedCriteriaQuery;
        @Nullable
        private final ParameterBinder cachedParameterBinder;
        @Nullable
        private final List<ParameterMetadataProvider.ParameterMetadata<?>> expressions;

        QueryPreparer(boolean recreateQueries) {
            ActiveObjectsQueryCreator creator = this.createCreator(Optional.empty());
            if (recreateQueries) {
                this.cachedCriteriaQuery = null;
                this.expressions = null;
                this.cachedParameterBinder = null;
            } else {
                this.cachedCriteriaQuery = (Queryable)creator.createQuery();
                this.expressions = creator.getParameterExpressions();
                this.cachedParameterBinder = this.getBinder(this.expressions);
            }
        }

        public Queryable createQuery(Object[] values) {
            Queryable criteriaQuery = this.cachedCriteriaQuery;
            ParameterBinder parameterBinder = this.cachedParameterBinder;
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(PartTreeActiveObjectsQuery.this.parameters, values);
            if (this.cachedCriteriaQuery == null || accessor.hasBindableNullValue()) {
                ActiveObjectsQueryCreator creator = this.createCreator(Optional.of(accessor));
                criteriaQuery = (Queryable)creator.createQuery(this.getDynamicSort(values));
                List<ParameterMetadataProvider.ParameterMetadata<?>> paramExpressions = creator.getParameterExpressions();
                parameterBinder = this.getBinder(paramExpressions);
            }
            if (parameterBinder == null) {
                throw new IllegalStateException("ParameterBinder is null!");
            }
            return this.restrictMaxResultsIfNecessary(this.invokeBinding(parameterBinder, criteriaQuery, values));
        }

        private Queryable restrictMaxResultsIfNecessary(Queryable query) {
            if (PartTreeActiveObjectsQuery.this.tree.isLimiting()) {
                if (Objects.nonNull(query.getLimit()) && this.areLimitsRequired(query)) {
                    int offset = query.getOffset() - (query.getLimit() - PartTreeActiveObjectsQuery.this.tree.getMaxResults());
                    logger.debug("Setting first result: [{}]", (Object)offset);
                    query.setOffset(offset);
                }
                logger.debug("Setting MaxResults: [{}]", (Object)PartTreeActiveObjectsQuery.this.tree.getMaxResults());
                query.setLimit(PartTreeActiveObjectsQuery.this.tree.getMaxResults());
            }
            if (PartTreeActiveObjectsQuery.this.tree.isExistsProjection()) {
                logger.debug("Restricting results for query: [{}] to [{}]", (Object)query, (Object)1);
                query.setLimit(1);
            }
            return query;
        }

        private boolean areLimitsRequired(Queryable query) {
            return query.getLimit() > PartTreeActiveObjectsQuery.this.tree.getMaxResults() && query.getOffset() > 0;
        }

        protected ActiveObjectsQueryCreator createCreator(Optional<ParametersParameterAccessor> accessor) {
            SQLQueryFactory sqlQueryFactory = PartTreeActiveObjectsQuery.this.getDatabaseAccessor().runInTransaction(DatabaseConnection::query, OnRollback.NOOP);
            ResultProcessor processor = PartTreeActiveObjectsQuery.this.getQueryMethod().getResultProcessor();
            ReturnedType returnedType = accessor.map(processor::withDynamicProjection).orElse(processor).getReturnedType();
            EnhancedRelationalPathBase path = (EnhancedRelationalPathBase)PartTreeActiveObjectsQuery.this.entityPathResolver.createPath(PartTreeActiveObjectsQuery.this.getQueryMethod().getEntityInformation().getJavaType());
            ParameterMetadataProvider provider = accessor.map(it -> new ParameterMetadataProvider((ParametersParameterAccessor)it, PartTreeActiveObjectsQuery.this.escapeCharacter)).orElseGet(() -> new ParameterMetadataProvider(PartTreeActiveObjectsQuery.this.parameters, PartTreeActiveObjectsQuery.this.escapeCharacter));
            return new ActiveObjectsQueryCreator(PartTreeActiveObjectsQuery.this.tree, returnedType, sqlQueryFactory, provider, path);
        }

        protected Queryable invokeBinding(ParameterBinder binder, Queryable query, Object[] values) {
            return binder.bindAndPrepare(query, values);
        }

        private ParameterBinder getBinder(List<ParameterMetadataProvider.ParameterMetadata<?>> expressions) {
            return ParameterBinderFactory.createCriteriaBinder(PartTreeActiveObjectsQuery.this.parameters, expressions);
        }

        private Sort getDynamicSort(Object[] values) {
            return PartTreeActiveObjectsQuery.this.parameters.potentiallySortsDynamically() ? new ParametersParameterAccessor(PartTreeActiveObjectsQuery.this.parameters, values).getSort() : Sort.unsorted();
        }
    }
}

