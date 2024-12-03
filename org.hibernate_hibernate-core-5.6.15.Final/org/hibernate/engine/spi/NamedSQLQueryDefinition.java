/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.util.List;
import java.util.Map;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.NamedQueryDefinition;

public class NamedSQLQueryDefinition
extends NamedQueryDefinition {
    private NativeSQLQueryReturn[] queryReturns;
    private final List<String> querySpaces;
    private final boolean callable;
    private String resultSetRef;

    @Deprecated
    public NamedSQLQueryDefinition(String name, String query, NativeSQLQueryReturn[] queryReturns, List<String> querySpaces, boolean cacheable, String cacheRegion, Integer timeout, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes, boolean callable) {
        this(name, query, cacheable, cacheRegion, timeout, fetchSize, flushMode, cacheMode, readOnly, comment, parameterTypes, null, null, null, querySpaces, callable, queryReturns, null);
    }

    @Deprecated
    public NamedSQLQueryDefinition(String name, String query, String resultSetRef, List<String> querySpaces, boolean cacheable, String cacheRegion, Integer timeout, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes, boolean callable) {
        this(name, query, cacheable, cacheRegion, timeout, fetchSize, flushMode, cacheMode, readOnly, comment, parameterTypes, null, null, resultSetRef, querySpaces, callable, null, null);
    }

    NamedSQLQueryDefinition(String name, String query, boolean cacheable, String cacheRegion, Integer timeout, Integer fetchSize, FlushMode flushMode, CacheMode cacheMode, boolean readOnly, String comment, Map parameterTypes, Integer firstResult, Integer maxResults, String resultSetRef, List<String> querySpaces, boolean callable, NativeSQLQueryReturn[] queryReturns, Boolean passDistinctThrough) {
        super(name, query.trim(), cacheable, cacheRegion, timeout, null, fetchSize, flushMode, cacheMode, readOnly, comment, parameterTypes, firstResult, maxResults, passDistinctThrough);
        this.resultSetRef = resultSetRef;
        this.querySpaces = querySpaces;
        this.callable = callable;
        this.queryReturns = queryReturns;
    }

    public NativeSQLQueryReturn[] getQueryReturns() {
        return this.queryReturns;
    }

    public List<String> getQuerySpaces() {
        return this.querySpaces;
    }

    public boolean isCallable() {
        return this.callable;
    }

    public String getResultSetRef() {
        return this.resultSetRef;
    }

    @Override
    public NamedSQLQueryDefinition makeCopy(String name) {
        return new NamedSQLQueryDefinition(name, this.getQuery(), this.isCacheable(), this.getCacheRegion(), this.getTimeout(), this.getFetchSize(), this.getFlushMode(), this.getCacheMode(), this.isReadOnly(), this.getComment(), this.getParameterTypes(), this.getFirstResult(), this.getMaxResults(), this.getResultSetRef(), this.getQuerySpaces(), this.isCallable(), this.getQueryReturns(), this.getPassDistinctThrough());
    }

    public void addQueryReturns(NativeSQLQueryReturn[] queryReturnsToAdd) {
        if (queryReturnsToAdd != null && queryReturnsToAdd.length > 0) {
            int initialQueryReturnsLength = 0;
            if (this.queryReturns != null) {
                initialQueryReturnsLength = this.queryReturns.length;
            }
            NativeSQLQueryReturn[] allQueryReturns = new NativeSQLQueryReturn[initialQueryReturnsLength + queryReturnsToAdd.length];
            int i = 0;
            for (i = 0; i < initialQueryReturnsLength; ++i) {
                allQueryReturns[i] = this.queryReturns[i];
            }
            NativeSQLQueryReturn[] nativeSQLQueryReturnArray = queryReturnsToAdd;
            int n = nativeSQLQueryReturnArray.length;
            for (int j = 0; j < n; ++j) {
                NativeSQLQueryReturn queryReturnsToAdd1;
                allQueryReturns[i] = queryReturnsToAdd1 = nativeSQLQueryReturnArray[j];
                ++i;
            }
            this.queryReturns = allQueryReturns;
        }
    }
}

