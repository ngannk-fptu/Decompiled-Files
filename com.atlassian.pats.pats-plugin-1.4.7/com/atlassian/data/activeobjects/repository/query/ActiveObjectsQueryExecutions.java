/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.EntityStreamCallback
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQueryExecution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.support.PageableExecutionUtils;

public final class ActiveObjectsQueryExecutions {
    private ActiveObjectsQueryExecutions() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static class StreamExecution<T extends RawEntity<T>>
    extends AbstractActiveObjectsQueryExecution {
        StreamExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            Query createQuery = (Query)query.createQuery(values).getQuery();
            ArrayList results = new ArrayList();
            EntityStreamCallback streamCallback = results::add;
            query.getActiveObjectsEntityManager().stream(query.getQueryMethod().getDomainClass(), createQuery, streamCallback);
            return results.stream();
        }
    }

    static class SingleEntityExecution<T extends RawEntity<T>>
    extends AbstractActiveObjectsQueryExecution {
        SingleEntityExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            Query aoQuery = (Query)query.createQuery(values).getQuery();
            aoQuery.limit(1);
            Object[] results = query.getActiveObjectsEntityManager().find(query.getQueryMethod().getDomainClass(), aoQuery);
            return ArrayUtils.isEmpty((Object[])results) ? null : results[0];
        }
    }

    static class PagedExecution<T extends RawEntity<T>>
    extends AbstractActiveObjectsQueryExecution {
        private final Parameters<?, ?> parameters;

        public PagedExecution(Parameters<?, ?> parameters) {
            this.parameters = parameters;
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, values);
            Query createQuery = (Query)query.createQuery(values).getQuery();
            List<RawEntity> resultList = Arrays.asList(query.getActiveObjectsEntityManager().find(query.getQueryMethod().getDomainClass(), createQuery));
            return PageableExecutionUtils.getPage(resultList, accessor.getPageable(), () -> this.count(query, values));
        }

        private long count(AbstractActiveObjectsQuery repositoryQuery, Object[] values) {
            Query createCountQuery = (Query)repositoryQuery.createCountQuery(values).getQuery();
            return repositoryQuery.getActiveObjectsEntityManager().count(repositoryQuery.getQueryMethod().getDomainClass(), createCountQuery);
        }
    }

    static class SlicedExecution<T extends RawEntity<T>>
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
            Query aoQuery = (Query)query.createQuery(values).getQuery();
            aoQuery.setLimit(limit);
            List<RawEntity> resultList = Arrays.asList(query.getActiveObjectsEntityManager().find(query.getQueryMethod().getDomainClass(), aoQuery));
            boolean hasNext = pageable.isPaged() && resultList.size() > pageSize;
            return new SliceImpl<RawEntity>(hasNext ? resultList.subList(0, pageSize) : resultList, pageable, hasNext);
        }
    }

    static class CollectionExecution<T extends RawEntity<T>>
    extends AbstractActiveObjectsQueryExecution {
        CollectionExecution() {
        }

        @Override
        protected Object doExecute(AbstractActiveObjectsQuery query, Object[] values) {
            Query createQuery = (Query)query.createQuery(values).getQuery();
            return query.getActiveObjectsEntityManager().find(query.getQueryMethod().getDomainClass(), createQuery);
        }
    }
}

