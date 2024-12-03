/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.query;

import com.querydsl.core.types.ParamExpression;

public interface Queryable {
    public void setOffset(Integer var1);

    public Integer getOffset();

    public Object getQuery();

    public void setLimit(Integer var1);

    public Integer getLimit();

    public void setWhereQueryParams(Object[] var1);

    public Object[] getWhereQueryParams();

    public void setParameter(Integer var1, Object var2);

    public void setParameter(ParamExpression<?> var1, Object var2);
}

