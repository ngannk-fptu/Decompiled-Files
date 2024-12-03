/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;

public class NamedQueryDefinition
implements Serializable {
    private final String name;
    private final String query;
    private final boolean cacheable;
    private final String cacheRegion;
    private final Integer timeout;
    private final LockOptions lockOptions;
    private final Integer fetchSize;
    private final FlushMode flushMode;
    private final Map parameterTypes;
    private final CacheMode cacheMode;
    private final boolean readOnly;
    private final String comment;
    private final Boolean passDistinctThrough;
    private final Integer firstResult;
    private final Integer maxResults;

    @Deprecated
    public NamedQueryDefinition(String name, String query, boolean cacheable, String cacheRegion, Integer timeout, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes) {
        this(name, query, cacheable, cacheRegion, timeout, -1, fetchSize, flushMode, cacheMode, readOnly, comment, parameterTypes);
    }

    @Deprecated
    public NamedQueryDefinition(String name, String query, boolean cacheable, String cacheRegion, Integer timeout, Integer lockTimeout, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes) {
        this(name, query, cacheable, cacheRegion, timeout, new LockOptions().setTimeOut(lockTimeout), fetchSize, flushMode, cacheMode, readOnly, comment, parameterTypes, null, null, null);
    }

    NamedQueryDefinition(String name, String query, boolean cacheable, String cacheRegion, Integer timeout, LockOptions lockOptions, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes, Integer firstResult, Integer maxResults, Boolean passDistinctThrough) {
        this.name = name;
        this.query = query;
        this.cacheable = cacheable;
        this.cacheRegion = cacheRegion;
        this.timeout = timeout;
        this.lockOptions = lockOptions;
        this.fetchSize = fetchSize;
        this.flushMode = flushMode;
        this.parameterTypes = parameterTypes;
        this.cacheMode = cacheMode;
        this.readOnly = readOnly;
        this.comment = comment;
        this.firstResult = firstResult;
        this.maxResults = maxResults;
        this.passDistinctThrough = passDistinctThrough;
    }

    public String getName() {
        return this.name;
    }

    public String getQueryString() {
        return this.query;
    }

    public boolean isCacheable() {
        return this.cacheable;
    }

    public String getCacheRegion() {
        return this.cacheRegion;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public FlushMode getFlushMode() {
        return this.flushMode;
    }

    public Map getParameterTypes() {
        return this.parameterTypes;
    }

    public String getQuery() {
        return this.query;
    }

    public CacheMode getCacheMode() {
        return this.cacheMode;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public String getComment() {
        return this.comment;
    }

    public LockOptions getLockOptions() {
        return this.lockOptions;
    }

    public Integer getFirstResult() {
        return this.firstResult;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    public Boolean getPassDistinctThrough() {
        return this.passDistinctThrough;
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.name + " [" + this.query + "])";
    }

    public NamedQueryDefinition makeCopy(String name) {
        return new NamedQueryDefinition(name, this.getQuery(), this.isCacheable(), this.getCacheRegion(), this.getTimeout(), this.getLockOptions(), this.getFetchSize(), this.getFlushMode(), this.getCacheMode(), this.isReadOnly(), this.getComment(), this.getParameterTypes(), this.getFirstResult(), this.getMaxResults(), this.getPassDistinctThrough());
    }
}

