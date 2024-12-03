/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.crowd.dao.membership.cache.QueryType;
import java.io.Serializable;
import java.util.Objects;

class QueryTypeCacheKey
implements Serializable {
    private final long directoryId;
    private final QueryType queryType;

    public QueryTypeCacheKey(long directoryId, QueryType queryType) {
        this.directoryId = directoryId;
        this.queryType = queryType;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public QueryType getQueryType() {
        return this.queryType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueryTypeCacheKey cacheKey = (QueryTypeCacheKey)o;
        return this.directoryId == cacheKey.directoryId && this.queryType == cacheKey.queryType;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.directoryId, this.queryType});
    }
}

