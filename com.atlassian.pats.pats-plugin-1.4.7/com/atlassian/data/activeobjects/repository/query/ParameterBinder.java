/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsParameters;
import com.atlassian.data.activeobjects.repository.query.QueryParameterSetter;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.util.Assert;

public class ParameterBinder {
    private static final Logger log = LoggerFactory.getLogger(ParameterBinder.class);
    static final String PARAMETER_NEEDS_TO_BE_NAMED = "For queries with named parameters you need to use provide names for method parameters. Use @Param for query method parameters, or when on Java 8+ use the javac flag -parameters.";
    private final ActiveObjectsParameters parameters;
    private final Iterable<QueryParameterSetter> parameterSetters;
    private final boolean useAOForPaging;

    ParameterBinder(ActiveObjectsParameters parameters, Iterable<QueryParameterSetter> parameterSetters) {
        this(parameters, parameterSetters, true);
    }

    public ParameterBinder(ActiveObjectsParameters parameters, Iterable<QueryParameterSetter> parameterSetters, boolean useAOForPaging) {
        Assert.notNull((Object)parameters, (String)"ActiveObjectsParameters must not be null!");
        Assert.notNull(parameterSetters, (String)"Parameter setters must not be null!");
        this.parameters = parameters;
        this.parameterSetters = parameterSetters;
        this.useAOForPaging = useAOForPaging;
        log.debug("Got params: [{}], paramSetters: [{}], useAOForPaging: [{}]", new Object[]{parameters, parameterSetters, useAOForPaging});
    }

    public Queryable bind(Queryable aoQuery, Object[] values) {
        return this.bind(aoQuery, values, QueryParameterSetter.ErrorHandling.STRICT);
    }

    public Queryable bind(Queryable aoQuery, Object[] values, QueryParameterSetter.ErrorHandling errorHandling) {
        aoQuery.setWhereQueryParams(new Object[Iterables.size(this.parameterSetters)]);
        this.parameterSetters.forEach(it -> it.setParameter(aoQuery, values, errorHandling));
        return aoQuery;
    }

    Queryable bindAndPrepare(Queryable query, Object[] values) {
        Assert.notNull((Object)query, (String)"Query must not be null!");
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, values);
        Queryable result = this.bind(query, values);
        if (!this.useAOForPaging || !this.parameters.hasPageableParameter() || accessor.getPageable().isUnpaged()) {
            return result;
        }
        result.setOffset((int)accessor.getPageable().getOffset());
        result.setLimit(accessor.getPageable().getPageSize());
        return result;
    }
}

