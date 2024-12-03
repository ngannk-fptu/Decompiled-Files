/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryFactory;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethodFactory;
import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.atlassian.data.activeobjects.repository.query.PartTreeActiveObjectsQuery;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public final class ActiveObjectsQueryLookupStrategy {
    private static final Logger log = LoggerFactory.getLogger(ActiveObjectsQueryLookupStrategy.class);

    private ActiveObjectsQueryLookupStrategy() {
    }

    public static QueryLookupStrategy create(ActiveObjects em, ActiveObjectsQueryMethodFactory queryMethodFactory, @Nullable QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider, DatabaseAccessor databaseAccessor, EntityPathResolver entityPathResolver, EscapeCharacter escape) {
        Assert.notNull((Object)em, (String)"ActiveObjects must not be null!");
        Assert.notNull((Object)evaluationContextProvider, (String)"EvaluationContextProvider must not be null!");
        switch (key != null ? key : QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND) {
            case CREATE: {
                return new CreateQueryLookupStrategy(em, queryMethodFactory, entityPathResolver, databaseAccessor, escape);
            }
            case USE_DECLARED_QUERY: {
                return new DeclaredQueryLookupStrategy(em, databaseAccessor, queryMethodFactory, evaluationContextProvider);
            }
            case CREATE_IF_NOT_FOUND: {
                return new CreateIfNotFoundQueryLookupStrategy(em, databaseAccessor, queryMethodFactory, new CreateQueryLookupStrategy(em, queryMethodFactory, entityPathResolver, databaseAccessor, escape), new DeclaredQueryLookupStrategy(em, databaseAccessor, queryMethodFactory, evaluationContextProvider));
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", new Object[]{key}));
    }

    private static class CreateIfNotFoundQueryLookupStrategy
    extends AbstractQueryLookupStrategy {
        private final DeclaredQueryLookupStrategy lookupStrategy;
        private final CreateQueryLookupStrategy createStrategy;

        public CreateIfNotFoundQueryLookupStrategy(ActiveObjects em, DatabaseAccessor databaseAccessor, ActiveObjectsQueryMethodFactory queryMethodFactory, CreateQueryLookupStrategy createStrategy, DeclaredQueryLookupStrategy lookupStrategy) {
            super(em, databaseAccessor, queryMethodFactory);
            Assert.notNull((Object)databaseAccessor, (String)"To create derived queries from method names you must have PocketKnife configured!");
            Assert.notNull((Object)createStrategy, (String)"CreateQueryLookupStrategy must not be null!");
            Assert.notNull((Object)lookupStrategy, (String)"DeclaredQueryLookupStrategy must not be null!");
            this.createStrategy = createStrategy;
            this.lookupStrategy = lookupStrategy;
        }

        @Override
        protected RepositoryQuery resolveQuery(ActiveObjectsQueryMethod method, ActiveObjects em, DatabaseAccessor databaseAccessor, NamedQueries namedQueries) {
            try {
                return this.lookupStrategy.resolveQuery(method, em, databaseAccessor, namedQueries);
            }
            catch (IllegalStateException e) {
                log.debug("Got resolveQuery error for method: [{}]: [{}]", (Object)method, (Object)e.getMessage());
                return this.createStrategy.resolveQuery(method, em, databaseAccessor, namedQueries);
            }
        }
    }

    private static class DeclaredQueryLookupStrategy
    extends AbstractQueryLookupStrategy {
        private final QueryMethodEvaluationContextProvider evaluationContextProvider;

        public DeclaredQueryLookupStrategy(ActiveObjects em, DatabaseAccessor databaseAccessor, ActiveObjectsQueryMethodFactory queryMethodFactory, QueryMethodEvaluationContextProvider evaluationContextProvider) {
            super(em, databaseAccessor, queryMethodFactory);
            this.evaluationContextProvider = evaluationContextProvider;
        }

        @Override
        protected RepositoryQuery resolveQuery(ActiveObjectsQueryMethod method, ActiveObjects em, DatabaseAccessor databaseAccessor, NamedQueries namedQueries) {
            AbstractActiveObjectsQuery query = ActiveObjectsQueryFactory.INSTANCE.fromQueryAnnotation(method, em, databaseAccessor, this.evaluationContextProvider);
            if (null != query) {
                return query;
            }
            throw new IllegalStateException(String.format("Did neither find a NamedQuery nor an annotated query for method %s!", method));
        }
    }

    private static class CreateQueryLookupStrategy
    extends AbstractQueryLookupStrategy {
        private final EntityPathResolver entityPathResolver;
        private final EscapeCharacter escape;

        public CreateQueryLookupStrategy(ActiveObjects em, ActiveObjectsQueryMethodFactory queryMethodFactory, EntityPathResolver entityPathResolver, DatabaseAccessor databaseAccessor, EscapeCharacter escape) {
            super(em, databaseAccessor, queryMethodFactory);
            Assert.notNull((Object)databaseAccessor, (String)"To create derived queries from method names you must have PocketKnife configured!");
            this.entityPathResolver = entityPathResolver;
            this.escape = escape;
        }

        @Override
        protected RepositoryQuery resolveQuery(ActiveObjectsQueryMethod method, ActiveObjects em, DatabaseAccessor databaseAccessor, NamedQueries namedQueries) {
            return new PartTreeActiveObjectsQuery(method, em, databaseAccessor, this.entityPathResolver, this.escape);
        }
    }

    private static abstract class AbstractQueryLookupStrategy
    implements QueryLookupStrategy {
        private final ActiveObjects em;
        private final ActiveObjectsQueryMethodFactory queryMethodFactory;
        private final DatabaseAccessor databaseAccessor;

        public AbstractQueryLookupStrategy(ActiveObjects em, DatabaseAccessor databaseAccessor, ActiveObjectsQueryMethodFactory queryMethodFactory) {
            Assert.notNull((Object)em, (String)"EntityManager must not be null!");
            Assert.notNull((Object)queryMethodFactory, (String)"ActiveObjectsQueryMethodFactory must not be null!");
            this.em = em;
            this.queryMethodFactory = queryMethodFactory;
            this.databaseAccessor = databaseAccessor;
        }

        @Override
        public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
            return this.resolveQuery(this.queryMethodFactory.build(method, metadata, factory), this.em, this.databaseAccessor, namedQueries);
        }

        protected abstract RepositoryQuery resolveQuery(ActiveObjectsQueryMethod var1, ActiveObjects var2, DatabaseAccessor var3, NamedQueries var4);
    }
}

