/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.query;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.lang.Nullable;

public interface RepositoryQuery {
    @Nullable
    public Object execute(Object[] var1);

    public QueryMethod getQueryMethod();
}

