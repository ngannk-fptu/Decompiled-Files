/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Query
 *  org.apache.commons.lang3.NotImplementedException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.querydsl.core.types.ParamExpression;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import net.java.ao.Query;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ActiveObjectsQuery
implements Queryable {
    private static final Logger log = LoggerFactory.getLogger(ActiveObjectsQuery.class);
    private Integer offset;
    private Integer limit;
    private String whereClauseQueryString;
    private Object[] whereQueryParams;

    public ActiveObjectsQuery(String queryString) {
        this.whereClauseQueryString = queryString;
    }

    @Override
    public void setParameter(Integer position, Object value) {
        if (value instanceof Collection) {
            Collection coll = (Collection)value;
            value = CollectionUtils.isEmpty((Collection)coll) ? null : StringUtils.collectionToDelimitedString((Collection)coll, (String)",");
        }
        log.debug("Setting queryWhereParams: [{}] to: [{}] of type: [{}]", new Object[]{position - 1, value, Objects.nonNull(value) ? value.getClass() : "N/A"});
        this.whereQueryParams[position.intValue() - 1] = value;
    }

    public Query getQuery() {
        Query query = Query.select().where(this.whereClauseQueryString, this.getWhereQueryParams());
        log.debug("Assigning offset: [{}] + limit: [{}]", (Object)this.offset, (Object)this.limit);
        if (Objects.nonNull(this.offset)) {
            query.setOffset(this.offset.intValue());
        }
        if (Objects.nonNull(this.limit)) {
            query.limit(this.limit.intValue());
        }
        return query;
    }

    @Override
    public void setParameter(ParamExpression<?> paramExpression, Object value) {
        throw new NotImplementedException("setParameter");
    }

    @Override
    public Integer getOffset() {
        return this.offset;
    }

    @Override
    public Integer getLimit() {
        return this.limit;
    }

    public String getWhereClauseQueryString() {
        return this.whereClauseQueryString;
    }

    @Override
    public Object[] getWhereQueryParams() {
        return this.whereQueryParams;
    }

    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setWhereClauseQueryString(String whereClauseQueryString) {
        this.whereClauseQueryString = whereClauseQueryString;
    }

    @Override
    public void setWhereQueryParams(Object[] whereQueryParams) {
        this.whereQueryParams = whereQueryParams;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ActiveObjectsQuery)) {
            return false;
        }
        ActiveObjectsQuery other = (ActiveObjectsQuery)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$offset = this.getOffset();
        Integer other$offset = other.getOffset();
        if (this$offset == null ? other$offset != null : !((Object)this$offset).equals(other$offset)) {
            return false;
        }
        Integer this$limit = this.getLimit();
        Integer other$limit = other.getLimit();
        if (this$limit == null ? other$limit != null : !((Object)this$limit).equals(other$limit)) {
            return false;
        }
        String this$whereClauseQueryString = this.getWhereClauseQueryString();
        String other$whereClauseQueryString = other.getWhereClauseQueryString();
        if (this$whereClauseQueryString == null ? other$whereClauseQueryString != null : !this$whereClauseQueryString.equals(other$whereClauseQueryString)) {
            return false;
        }
        return Arrays.deepEquals(this.getWhereQueryParams(), other.getWhereQueryParams());
    }

    protected boolean canEqual(Object other) {
        return other instanceof ActiveObjectsQuery;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $offset = this.getOffset();
        result = result * 59 + ($offset == null ? 43 : ((Object)$offset).hashCode());
        Integer $limit = this.getLimit();
        result = result * 59 + ($limit == null ? 43 : ((Object)$limit).hashCode());
        String $whereClauseQueryString = this.getWhereClauseQueryString();
        result = result * 59 + ($whereClauseQueryString == null ? 43 : $whereClauseQueryString.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getWhereQueryParams());
        return result;
    }

    public String toString() {
        return "ActiveObjectsQuery(offset=" + this.getOffset() + ", limit=" + this.getLimit() + ", whereClauseQueryString=" + this.getWhereClauseQueryString() + ", whereQueryParams=" + Arrays.deepToString(this.getWhereQueryParams()) + ")";
    }

    public ActiveObjectsQuery() {
    }
}

