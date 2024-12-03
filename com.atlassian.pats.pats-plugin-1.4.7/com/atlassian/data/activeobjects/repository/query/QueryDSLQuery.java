/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.NotImplementedException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.Queryable;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import java.util.Objects;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryDSLQuery
implements Queryable {
    private static final Logger log = LoggerFactory.getLogger(QueryDSLQuery.class);
    private Integer offset;
    private Integer limit;
    private SQLQuery<?> select;
    private RelationalPathBase<?> qdslType;

    public QueryDSLQuery(SQLQuery<?> sqlQuery, RelationalPathBase<?> qdslType) {
        this.select = sqlQuery;
        this.qdslType = qdslType;
    }

    @Override
    public Object getQuery() {
        log.debug("Assigning offset: [{}] + limit: [{}]", (Object)this.offset, (Object)this.limit);
        if (Objects.nonNull(this.offset)) {
            this.select.offset(this.offset.intValue());
        }
        if (Objects.nonNull(this.limit)) {
            this.select.limit(this.limit.intValue());
        }
        return this.select;
    }

    @Override
    public void setWhereQueryParams(Object[] objects) {
    }

    @Override
    public Object[] getWhereQueryParams() {
        throw new NotImplementedException("getQuery");
    }

    @Override
    public void setParameter(Integer position, Object value) {
        throw new NotImplementedException("setParameter");
    }

    public void setParameter(ParamExpression paramExpression, Object value) {
        log.debug("Setting param: [{}] of type: [{}] with value: [{}]", new Object[]{paramExpression, value.getClass(), value});
        this.select.set(paramExpression, value);
    }

    @Override
    public Integer getOffset() {
        return this.offset;
    }

    @Override
    public Integer getLimit() {
        return this.limit;
    }

    public SQLQuery<?> getSelect() {
        return this.select;
    }

    public RelationalPathBase<?> getQdslType() {
        return this.qdslType;
    }

    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setSelect(SQLQuery<?> select) {
        this.select = select;
    }

    public void setQdslType(RelationalPathBase<?> qdslType) {
        this.qdslType = qdslType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QueryDSLQuery)) {
            return false;
        }
        QueryDSLQuery other = (QueryDSLQuery)o;
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
        SQLQuery<?> this$select = this.getSelect();
        SQLQuery<?> other$select = other.getSelect();
        if (this$select == null ? other$select != null : !((Object)this$select).equals(other$select)) {
            return false;
        }
        RelationalPathBase<?> this$qdslType = this.getQdslType();
        RelationalPathBase<?> other$qdslType = other.getQdslType();
        return !(this$qdslType == null ? other$qdslType != null : !((Object)this$qdslType).equals(other$qdslType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof QueryDSLQuery;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $offset = this.getOffset();
        result = result * 59 + ($offset == null ? 43 : ((Object)$offset).hashCode());
        Integer $limit = this.getLimit();
        result = result * 59 + ($limit == null ? 43 : ((Object)$limit).hashCode());
        SQLQuery<?> $select = this.getSelect();
        result = result * 59 + ($select == null ? 43 : ((Object)$select).hashCode());
        RelationalPathBase<?> $qdslType = this.getQdslType();
        result = result * 59 + ($qdslType == null ? 43 : ((Object)$qdslType).hashCode());
        return result;
    }

    public String toString() {
        return "QueryDSLQuery(offset=" + this.getOffset() + ", limit=" + this.getLimit() + ", select=" + this.getSelect() + ", qdslType=" + this.getQdslType() + ")";
    }

    public QueryDSLQuery() {
    }
}

