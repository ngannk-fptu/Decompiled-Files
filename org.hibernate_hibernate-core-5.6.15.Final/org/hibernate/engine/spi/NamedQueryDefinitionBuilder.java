/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.NamedQueryDefinition;

public class NamedQueryDefinitionBuilder {
    protected String name;
    protected String query;
    protected boolean cacheable;
    protected String cacheRegion;
    protected Integer timeout;
    protected Integer fetchSize;
    protected FlushMode flushMode;
    protected CacheMode cacheMode;
    protected boolean readOnly;
    protected String comment;
    protected Map parameterTypes;
    protected LockOptions lockOptions;
    protected Integer firstResult;
    protected Integer maxResults;
    protected Boolean passDistinctThrough;

    public NamedQueryDefinitionBuilder() {
    }

    public NamedQueryDefinitionBuilder(String name) {
        this.name = name;
    }

    public NamedQueryDefinitionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public NamedQueryDefinitionBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public NamedQueryDefinitionBuilder setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }

    public NamedQueryDefinitionBuilder setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
        return this;
    }

    public NamedQueryDefinitionBuilder setTimeout(Integer timeout) {
        this.timeout = timeout;
        return this;
    }

    public NamedQueryDefinitionBuilder setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public NamedQueryDefinitionBuilder setFlushMode(FlushMode flushMode) {
        this.flushMode = flushMode;
        return this;
    }

    public NamedQueryDefinitionBuilder setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    public NamedQueryDefinitionBuilder setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public NamedQueryDefinitionBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public NamedQueryDefinitionBuilder addParameterType(String name, String typeName) {
        if (this.parameterTypes == null) {
            this.parameterTypes = new HashMap();
        }
        this.parameterTypes.put(name, typeName);
        return this;
    }

    public NamedQueryDefinitionBuilder setParameterTypes(Map parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public NamedQueryDefinitionBuilder setLockOptions(LockOptions lockOptions) {
        this.lockOptions = lockOptions;
        return this;
    }

    public NamedQueryDefinitionBuilder setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public NamedQueryDefinitionBuilder setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public NamedQueryDefinitionBuilder setPassDistinctThrough(Boolean passDistinctThrough) {
        this.passDistinctThrough = passDistinctThrough;
        return this;
    }

    public NamedQueryDefinition createNamedQueryDefinition() {
        return new NamedQueryDefinition(this.name, this.query, this.cacheable, this.cacheRegion, this.timeout, this.lockOptions, this.fetchSize, this.flushMode, this.cacheMode, this.readOnly, this.comment, this.parameterTypes, this.firstResult, this.maxResults, this.passDistinctThrough);
    }
}

