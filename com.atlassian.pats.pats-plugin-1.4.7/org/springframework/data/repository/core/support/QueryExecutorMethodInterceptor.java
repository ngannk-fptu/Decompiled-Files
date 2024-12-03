/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.ResolvableType;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.QueryCreationListener;
import org.springframework.data.repository.core.support.QueryExecutionResultHandler;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryInvocationMulticaster;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.data.repository.core.support.RepositoryMethodInvoker;
import org.springframework.data.repository.query.QueryCreationException;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

class QueryExecutorMethodInterceptor
implements MethodInterceptor {
    private final RepositoryInformation repositoryInformation;
    private final Map<Method, RepositoryQuery> queries;
    private final Map<Method, RepositoryMethodInvoker> invocationMetadataCache = new ConcurrentReferenceHashMap();
    private final QueryExecutionResultHandler resultHandler;
    private final NamedQueries namedQueries;
    private final List<QueryCreationListener<?>> queryPostProcessors;
    private final RepositoryInvocationMulticaster invocationMulticaster;

    public QueryExecutorMethodInterceptor(RepositoryInformation repositoryInformation, ProjectionFactory projectionFactory, Optional<QueryLookupStrategy> queryLookupStrategy, NamedQueries namedQueries, List<QueryCreationListener<?>> queryPostProcessors, List<RepositoryMethodInvocationListener> methodInvocationListeners) {
        this.repositoryInformation = repositoryInformation;
        this.namedQueries = namedQueries;
        this.queryPostProcessors = queryPostProcessors;
        this.invocationMulticaster = methodInvocationListeners.isEmpty() ? RepositoryInvocationMulticaster.NoOpRepositoryInvocationMulticaster.INSTANCE : new RepositoryInvocationMulticaster.DefaultRepositoryInvocationMulticaster(methodInvocationListeners);
        this.resultHandler = new QueryExecutionResultHandler(RepositoryFactorySupport.CONVERSION_SERVICE);
        if (!queryLookupStrategy.isPresent() && repositoryInformation.hasQueryMethods()) {
            throw new IllegalStateException("You have defined query methods in the repository but do not have any query lookup strategy defined. The infrastructure apparently does not support query methods!");
        }
        this.queries = queryLookupStrategy.map(it -> this.mapMethodsToQuery(repositoryInformation, (QueryLookupStrategy)it, projectionFactory)).orElse(Collections.emptyMap());
    }

    private Map<Method, RepositoryQuery> mapMethodsToQuery(RepositoryInformation repositoryInformation, QueryLookupStrategy lookupStrategy, ProjectionFactory projectionFactory) {
        return repositoryInformation.getQueryMethods().stream().map(method -> this.lookupQuery((Method)method, repositoryInformation, lookupStrategy, projectionFactory)).peek(pair -> this.invokeListeners((RepositoryQuery)pair.getSecond())).collect(Pair.toMap());
    }

    private Pair<Method, RepositoryQuery> lookupQuery(Method method, RepositoryInformation information, QueryLookupStrategy strategy, ProjectionFactory projectionFactory) {
        try {
            return Pair.of(method, strategy.resolveQuery(method, information, projectionFactory, this.namedQueries));
        }
        catch (QueryCreationException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw QueryCreationException.create(e.getMessage(), e, information.getRepositoryInterface(), method);
        }
    }

    private void invokeListeners(RepositoryQuery query) {
        for (QueryCreationListener<?> listener : this.queryPostProcessors) {
            ResolvableType typeArgument = ResolvableType.forClass(QueryCreationListener.class, listener.getClass()).getGeneric(new int[]{0});
            if (typeArgument == null || !typeArgument.isAssignableFrom(ResolvableType.forClass(query.getClass()))) continue;
            listener.onCreation(query);
        }
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        QueryExecutionConverters.ExecutionAdapter executionAdapter = QueryExecutionConverters.getExecutionAdapter(method.getReturnType());
        if (executionAdapter == null) {
            return this.resultHandler.postProcessInvocationResult(this.doInvoke(invocation), method);
        }
        return executionAdapter.apply(() -> this.resultHandler.postProcessInvocationResult(this.doInvoke(invocation), method));
    }

    @Nullable
    private Object doInvoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (this.hasQueryFor(method)) {
            RepositoryMethodInvoker invocationMetadata = this.invocationMetadataCache.get(method);
            if (invocationMetadata == null) {
                invocationMetadata = RepositoryMethodInvoker.forRepositoryQuery(method, this.queries.get(method));
                this.invocationMetadataCache.put(method, invocationMetadata);
            }
            return invocationMetadata.invoke(this.repositoryInformation.getRepositoryInterface(), this.invocationMulticaster, invocation.getArguments());
        }
        return invocation.proceed();
    }

    private boolean hasQueryFor(Method method) {
        return this.queries.containsKey(method);
    }
}

