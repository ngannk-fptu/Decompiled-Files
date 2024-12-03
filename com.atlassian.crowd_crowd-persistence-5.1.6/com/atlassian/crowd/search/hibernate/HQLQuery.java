/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.search.hibernate;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HQLQuery {
    protected final StringBuilder select = new StringBuilder("");
    protected final StringBuilder from = new StringBuilder(" FROM ");
    protected final StringBuilder where = new StringBuilder(" WHERE ");
    protected final StringBuilder orderBy = new StringBuilder(" ORDER BY ");
    protected int aliasCounter = 0;
    protected String cacheRegion;
    protected boolean distinctRequired = false;
    protected boolean whereRequired = false;
    protected boolean orderByRequired = false;
    protected boolean readOnly = false;
    protected final Map<String, Object> parameterValues = new LinkedHashMap<String, Object>();
    protected int maxResults;
    protected int startIndex;
    protected Integer fetchSize;
    protected String batchedParam;
    protected Comparator comparatorForBatch;
    protected Function<List<Object[]>, List<?>> resultTransform;

    public StringBuilder appendSelect(CharSequence hql) {
        this.select.append(hql);
        return this.select;
    }

    public StringBuilder appendFrom(CharSequence hql) {
        this.from.append(hql);
        return this.from;
    }

    @Deprecated
    public StringBuilder appendWhere(CharSequence hql) {
        this.whereRequired = true;
        this.where.append(hql);
        return this.where;
    }

    public HQLQuery safeAppendWhere(CharSequence hql) {
        if (hql.toString().trim().length() > 0) {
            this.whereRequired = true;
            this.where.append(hql);
        }
        return this;
    }

    public StringBuilder appendOrderBy(CharSequence hql) {
        this.orderByRequired = true;
        this.orderBy.append(hql);
        return this.orderBy;
    }

    public int getNextAlias() {
        ++this.aliasCounter;
        return this.aliasCounter;
    }

    public String addParameterPlaceholder(@Nullable Object value) {
        return ":" + this.addParam(value);
    }

    public String addParameterPlaceholderForBatchedParam(@Nullable Collection values) {
        Preconditions.checkState((this.batchedParam == null ? 1 : 0) != 0, (Object)"Only one batched param allowed");
        this.batchedParam = this.addParam(values);
        return ":" + this.batchedParam;
    }

    public Comparator getComparatorForBatch() {
        return this.comparatorForBatch;
    }

    public void setComparatorForBatch(Comparator comparatorForBatch) {
        this.comparatorForBatch = comparatorForBatch;
    }

    private String addParam(@Nullable Object value) {
        String name = "param" + (this.parameterValues.size() + 1);
        this.parameterValues.put(name, value);
        return name;
    }

    public Map<String, Object> getParameterMap() {
        return Collections.unmodifiableMap(this.parameterValues);
    }

    public Collection<Object> getParameterValues() {
        return Collections.unmodifiableCollection(this.parameterValues.values());
    }

    public void setFetchSize(@Nullable Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void requireDistinct() {
        this.distinctRequired = true;
    }

    public void limitResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void offsetResults(int startIndex) {
        this.startIndex = startIndex;
    }

    @Nonnull
    public Optional<Integer> getFetchSize() {
        return Optional.ofNullable(this.fetchSize);
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getBatchedParamName() {
        return this.batchedParam;
    }

    public Collection getBatchedParamValues() {
        return this.batchedParam == null ? null : (Collection)this.parameterValues.get(this.batchedParam);
    }

    public void setResultTransform(Function<List<Object[]>, List<?>> resultTransform) {
        Preconditions.checkState((this.resultTransform == null ? 1 : 0) != 0, (Object)"Result transform already set");
        this.resultTransform = resultTransform;
    }

    public Function<List<Object[]>, List<?>> getResultTransform() {
        return this.resultTransform;
    }

    public String getCacheRegion() {
        return this.cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public String toString() {
        StringBuilder hql = new StringBuilder("SELECT ");
        if (this.distinctRequired) {
            hql.append("DISTINCT ");
        }
        hql.append((CharSequence)this.select);
        hql.append((CharSequence)this.from);
        if (this.whereRequired) {
            hql.append((CharSequence)this.where);
        }
        if (this.orderByRequired) {
            hql.append((CharSequence)this.orderBy);
        }
        return hql.toString();
    }
}

