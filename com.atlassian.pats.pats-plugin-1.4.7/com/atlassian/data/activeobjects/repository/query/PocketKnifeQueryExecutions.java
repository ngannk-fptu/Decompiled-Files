/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQueryExecution;
import com.atlassian.data.activeobjects.repository.query.QueryDSLQuery;
import com.atlassian.data.activeobjects.repository.support.QuerydslPocketKnifeCrudPredicateExecutor;
import com.atlassian.data.activeobjects.repository.support.QuerydslUtils;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.SQLQuery;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.support.PageableExecutionUtils;

public final class PocketKnifeQueryExecutions {
    private static final Logger log = LoggerFactory.getLogger(PocketKnifeQueryExecutions.class);

    private PocketKnifeQueryExecutions() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static class StreamExecution
    extends AbstractActiveObjectsQueryExecution {
        StreamExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            return query.getDatabaseAccessor().runInTransaction(t -> {
                SQLQuery sqlQuery = (SQLQuery)query.createQuery(values).getQuery();
                return sqlQuery.clone(t.getJdbcConnection()).fetch();
            }, OnRollback.NOOP).stream();
        }
    }

    static class ExistsExecution
    extends AbstractActiveObjectsQueryExecution {
        ExistsExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            SQLQuery sqlQuery = (SQLQuery)query.createQuery(values).getQuery();
            return query.getDatabaseAccessor().runInTransaction(t -> 0L != sqlQuery.clone(t.getJdbcConnection()).fetchCount(), OnRollback.NOOP);
        }
    }

    static class DeleteExecution
    extends AbstractActiveObjectsQueryExecution {
        DeleteExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            QueryDSLQuery activeObjectsQuery = (QueryDSLQuery)query.createQuery(values);
            SQLQuery sqlQuery = (SQLQuery)activeObjectsQuery.getQuery();
            return query.getDatabaseAccessor().runInTransaction(t -> {
                AbstractSQLQuery clone = sqlQuery.clone(t.getJdbcConnection());
                List resultList = clone.fetch();
                log.debug("Got results to delete: [{}]", resultList);
                if (!resultList.isEmpty()) {
                    Path pkPath = QuerydslUtils.getPrimaryKeyForEntity(activeObjectsQuery.getQdslType());
                    SimplePath pkSimplePath = QuerydslUtils.getSimplePathForPrimaryKey(activeObjectsQuery.getQdslType(), pkPath);
                    for (Object entity : resultList) {
                        log.debug("Deleting: [{}]", entity);
                        BooleanExpression pkValuePredicate = QuerydslPocketKnifeCrudPredicateExecutor.createPkIdPredicate(entity, activeObjectsQuery.getQdslType(), pkPath, pkSimplePath);
                        long deletedRows = t.delete(activeObjectsQuery.getQdslType()).where((Predicate)pkValuePredicate).execute();
                        log.trace("Deleted predicate: [{}] with result: [{}]", (Object)pkValuePredicate, (Object)deletedRows);
                    }
                }
                return query.getQueryMethod().isCollectionQuery() ? resultList : Integer.valueOf(resultList.size());
            }, OnRollback.NOOP);
        }
    }

    static class SingleEntityExecution
    extends AbstractActiveObjectsQueryExecution {
        SingleEntityExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            return query.getDatabaseAccessor().runInTransaction(t -> {
                SQLQuery sqlQuery = (SQLQuery)query.createQuery(values).getQuery();
                sqlQuery.limit(1L);
                return sqlQuery.clone(t.getJdbcConnection()).fetchOne();
            }, OnRollback.NOOP);
        }
    }

    static class PagedExecution
    extends AbstractActiveObjectsQueryExecution {
        private final Parameters<?, ?> parameters;

        public PagedExecution(Parameters<?, ?> parameters) {
            this.parameters = parameters;
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, values);
            SQLQuery sqlQuery = (SQLQuery)query.createQuery(values).getQuery();
            return query.getDatabaseAccessor().runInTransaction(t -> {
                List resultList = sqlQuery.clone(t.getJdbcConnection()).fetch();
                return PageableExecutionUtils.getPage(resultList, accessor.getPageable(), () -> this.count(query, values));
            }, OnRollback.NOOP);
        }

        private long count(AbstractActiveObjectsQuery repositoryQuery, Object[] values) {
            SQLQuery sqlQuery = (SQLQuery)repositoryQuery.createCountQuery(values).getQuery();
            long count = repositoryQuery.getDatabaseAccessor().runInTransaction(t -> sqlQuery.clone(t.getJdbcConnection()).fetchCount(), OnRollback.NOOP);
            log.debug("Got count: [{}]", (Object)count);
            return count;
        }
    }

    static class SlicedExecution
    extends AbstractActiveObjectsQueryExecution {
        private final Parameters<?, ?> parameters;

        public SlicedExecution(Parameters<?, ?> parameters) {
            this.parameters = parameters;
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, values);
            Pageable pageable = accessor.getPageable();
            int pageSize = pageable.isPaged() ? pageable.getPageSize() : 0;
            int limit = pageable.isPaged() ? pageSize + 1 : Integer.MAX_VALUE;
            List resultList = query.getDatabaseAccessor().runInTransaction(t -> {
                SQLQuery sqlQuery = (SQLQuery)query.createQuery(values).getQuery();
                sqlQuery.limit(limit);
                return sqlQuery.clone(t.getJdbcConnection()).fetch();
            }, OnRollback.NOOP);
            boolean hasNext = pageable.isPaged() && resultList.size() > pageSize;
            return new SliceImpl(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
        }
    }

    static class CollectionExecution
    extends AbstractActiveObjectsQueryExecution {
        CollectionExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            SQLQuery createQuery = (SQLQuery)query.createQuery(values).getQuery();
            return query.getDatabaseAccessor().runInTransaction(t -> createQuery.clone(t.getJdbcConnection()).fetch(), OnRollback.NOOP);
        }
    }
}

