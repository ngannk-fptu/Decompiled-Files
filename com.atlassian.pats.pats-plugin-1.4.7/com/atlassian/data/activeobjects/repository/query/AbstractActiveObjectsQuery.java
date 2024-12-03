/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.data.activeobjects.repository.query.AbstractActiveObjectsQueryExecution;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.ParameterBinder;
import com.atlassian.data.activeobjects.repository.query.ParameterBinderFactory;
import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.querydsl.core.types.dsl.SimplePath;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractActiveObjectsQuery
implements RepositoryQuery {
    private static final Logger logger = LoggerFactory.getLogger(AbstractActiveObjectsQuery.class);
    private final ActiveObjectsQueryMethod<?> method;
    private final ActiveObjects activeObjects;
    @Nullable
    private final DatabaseAccessor databaseAccessor;
    final Lazy<ParameterBinder> parameterBinder = new Lazy<ParameterBinder>(this::createBinder);

    public AbstractActiveObjectsQuery(ActiveObjectsQueryMethod<?> method, ActiveObjects activeObjects, DatabaseAccessor databaseAccessor) {
        Assert.notNull(method, (String)"ActiveObjectsQueryMethod must not be null!");
        Assert.notNull((Object)activeObjects, (String)"ActiveObjects must not be null!");
        this.method = method;
        this.activeObjects = activeObjects;
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public ActiveObjectsQueryMethod getQueryMethod() {
        return this.method;
    }

    protected ActiveObjects getActiveObjectsEntityManager() {
        return this.activeObjects;
    }

    protected DatabaseAccessor getDatabaseAccessor() {
        return this.databaseAccessor;
    }

    @Override
    @Nullable
    public Object execute(Object[] values) {
        return this.doExecute(this.getExecution(), values);
    }

    protected abstract AbstractActiveObjectsQueryExecution getExecution();

    @Nullable
    private Object doExecute(AbstractActiveObjectsQueryExecution execution, Object[] values) {
        Object result = execution.execute(this, values);
        ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.method.getParameters(), values);
        ResultProcessor withDynamicProjection = this.method.getResultProcessor().withDynamicProjection(accessor);
        return withDynamicProjection.processResult(result, new TupleConverter(withDynamicProjection.getReturnedType()));
    }

    protected ParameterBinder createBinder() {
        return ParameterBinderFactory.createBinder(this.getQueryMethod().getParameters());
    }

    protected abstract Queryable createQuery(Object[] var1);

    protected abstract Queryable createCountQuery(Object[] var1);

    static class TupleConverter
    implements Converter<Object, Object> {
        private final ReturnedType type;

        public TupleConverter(ReturnedType type) {
            Assert.notNull((Object)type, (String)"Returned type must not be null!");
            this.type = type;
        }

        public Object convert(Object source) {
            if (!(source instanceof Map)) {
                return source;
            }
            Map tuple = (Map)source;
            if (this.type.isInstance(source)) {
                return source;
            }
            Set keySet = tuple.keySet();
            for (Object key : keySet) {
                if (!(key instanceof SimplePath)) continue;
                SimplePath keyStringValue = (SimplePath)key;
                String name = keyStringValue.getMetadata().getName();
                tuple.put(name, tuple.get(key));
            }
            logger.debug("Enriched map with property names: [{}]", source);
            return source;
        }
    }
}

