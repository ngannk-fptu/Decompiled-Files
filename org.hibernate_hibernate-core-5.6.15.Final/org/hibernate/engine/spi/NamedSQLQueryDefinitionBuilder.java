/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;

public class NamedSQLQueryDefinitionBuilder
extends NamedQueryDefinitionBuilder {
    private NativeSQLQueryReturn[] queryReturns;
    private Collection<String> querySpaces;
    private boolean callable;
    private String resultSetRef;

    public NamedSQLQueryDefinitionBuilder() {
    }

    public NamedSQLQueryDefinitionBuilder(String name) {
        super(name);
    }

    public NamedSQLQueryDefinitionBuilder setQueryReturns(NativeSQLQueryReturn[] queryReturns) {
        this.queryReturns = queryReturns;
        return this;
    }

    public NamedSQLQueryDefinitionBuilder setQueryReturns(List<NativeSQLQueryReturn> queryReturns) {
        this.queryReturns = queryReturns != null ? queryReturns.toArray(new NativeSQLQueryReturn[queryReturns.size()]) : null;
        return this;
    }

    public NamedSQLQueryDefinitionBuilder setQuerySpaces(List<String> querySpaces) {
        this.querySpaces = querySpaces;
        return this;
    }

    public NamedSQLQueryDefinitionBuilder setQuerySpaces(Collection<String> synchronizedQuerySpaces) {
        this.querySpaces = synchronizedQuerySpaces;
        return this;
    }

    public NamedSQLQueryDefinitionBuilder addSynchronizedQuerySpace(String table) {
        if (this.querySpaces == null) {
            this.querySpaces = new ArrayList<String>();
        }
        this.querySpaces.add(table);
        return this;
    }

    public NamedSQLQueryDefinitionBuilder setResultSetRef(String resultSetRef) {
        this.resultSetRef = resultSetRef;
        return this;
    }

    public NamedSQLQueryDefinitionBuilder setCallable(boolean callable) {
        this.callable = callable;
        return this;
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setName(String name) {
        return (NamedSQLQueryDefinitionBuilder)super.setName(name);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setQuery(String query) {
        return (NamedSQLQueryDefinitionBuilder)super.setQuery(query);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setCacheable(boolean cacheable) {
        return (NamedSQLQueryDefinitionBuilder)super.setCacheable(cacheable);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setCacheRegion(String cacheRegion) {
        return (NamedSQLQueryDefinitionBuilder)super.setCacheRegion(cacheRegion);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setTimeout(Integer timeout) {
        return (NamedSQLQueryDefinitionBuilder)super.setTimeout(timeout);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setFetchSize(Integer fetchSize) {
        return (NamedSQLQueryDefinitionBuilder)super.setFetchSize(fetchSize);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setFlushMode(FlushMode flushMode) {
        return (NamedSQLQueryDefinitionBuilder)super.setFlushMode(flushMode);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setCacheMode(CacheMode cacheMode) {
        return (NamedSQLQueryDefinitionBuilder)super.setCacheMode(cacheMode);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setReadOnly(boolean readOnly) {
        return (NamedSQLQueryDefinitionBuilder)super.setReadOnly(readOnly);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setComment(String comment) {
        return (NamedSQLQueryDefinitionBuilder)super.setComment(comment);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder addParameterType(String name, String typeName) {
        return (NamedSQLQueryDefinitionBuilder)super.addParameterType(name, typeName);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setParameterTypes(Map parameterTypes) {
        return (NamedSQLQueryDefinitionBuilder)super.setParameterTypes(parameterTypes);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setLockOptions(LockOptions lockOptions) {
        return (NamedSQLQueryDefinitionBuilder)super.setLockOptions(lockOptions);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setFirstResult(Integer firstResult) {
        return (NamedSQLQueryDefinitionBuilder)super.setFirstResult(firstResult);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setMaxResults(Integer maxResults) {
        return (NamedSQLQueryDefinitionBuilder)super.setMaxResults(maxResults);
    }

    @Override
    public NamedSQLQueryDefinitionBuilder setPassDistinctThrough(Boolean passDistinctThrough) {
        return (NamedSQLQueryDefinitionBuilder)super.setPassDistinctThrough(passDistinctThrough);
    }

    @Override
    public NamedSQLQueryDefinition createNamedQueryDefinition() {
        return new NamedSQLQueryDefinition(this.name, this.query, this.cacheable, this.cacheRegion, this.timeout, this.fetchSize, this.flushMode, this.cacheMode, this.readOnly, this.comment, this.parameterTypes, this.firstResult, this.maxResults, this.resultSetRef, this.querySpacesCopy(), this.callable, this.queryReturns, this.passDistinctThrough);
    }

    private List<String> querySpacesCopy() {
        return this.querySpaces == null ? null : new ArrayList<String>(this.querySpaces);
    }
}

