/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.lang.Nullable
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQuery;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.SimpleActiveObjectsQuery;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

enum ActiveObjectsQueryFactory {
    INSTANCE;

    private static final SpelExpressionParser PARSER;
    private static final Logger LOG;

    @Nullable
    AbstractActiveObjectsQuery fromQueryAnnotation(ActiveObjectsQueryMethod method, ActiveObjects em, DatabaseAccessor databaseAccessor, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        LOG.debug("Looking up query for method {}", (Object)method.getName());
        return this.fromMethodWithQueryString(method, em, databaseAccessor, method.getAnnotatedQuery(), evaluationContextProvider);
    }

    @Nullable
    AbstractActiveObjectsQuery fromMethodWithQueryString(ActiveObjectsQueryMethod method, ActiveObjects em, DatabaseAccessor databaseAccessor, @Nullable String queryString, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        if (queryString == null) {
            return null;
        }
        return new SimpleActiveObjectsQuery(method, em, databaseAccessor, queryString, evaluationContextProvider, PARSER);
    }

    static {
        PARSER = new SpelExpressionParser();
        LOG = LoggerFactory.getLogger(ActiveObjectsQueryFactory.class);
    }
}

