/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.crowd.dao.membership.cache.QueryType;
import com.atlassian.crowd.dao.membership.cache.QueryTypeCacheKey;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CacheInvalidations {
    private final Set<QueryTypeCacheKey> queryTypesInvalidations = new HashSet<QueryTypeCacheKey>();
    private final Map<QueryTypeCacheKey, Set<String>> keyInvalidations = new HashMap<QueryTypeCacheKey, Set<String>>();
    private final Set<QueryType> cacheableTypes;
    private final int queryTypeInvalidationThreshold;

    public CacheInvalidations(Set<QueryType> cacheableTypes, int queryTypeInvalidationThreshold) {
        this.cacheableTypes = ImmutableSet.copyOf(cacheableTypes);
        this.queryTypeInvalidationThreshold = queryTypeInvalidationThreshold;
    }

    public boolean isInvalidated(QueryTypeCacheKey cacheKey, String key) {
        if (this.queryTypesInvalidations.contains(cacheKey)) {
            return true;
        }
        Set<String> invalidatedKeys = this.keyInvalidations.get(cacheKey);
        return invalidatedKeys != null && invalidatedKeys.contains(IdentifierUtils.toLowerCase((String)key));
    }

    public void addInvalidation(long directoryId) {
        for (QueryType type : this.cacheableTypes) {
            this.addInvalidation(directoryId, type);
        }
    }

    public void addInvalidation(long directoryId, QueryType queryType) {
        QueryTypeCacheKey cacheKey = new QueryTypeCacheKey(directoryId, queryType);
        this.queryTypesInvalidations.add(cacheKey);
        this.keyInvalidations.remove(cacheKey);
    }

    public void addInvalidation(long directoryId, QueryType queryType, String key) {
        QueryTypeCacheKey cacheKey = new QueryTypeCacheKey(directoryId, queryType);
        if (!this.queryTypesInvalidations.contains(cacheKey)) {
            Set keys = this.keyInvalidations.computeIfAbsent(cacheKey, k -> new HashSet());
            keys.add(IdentifierUtils.toLowerCase((String)key));
            if (keys.size() > this.queryTypeInvalidationThreshold) {
                this.queryTypesInvalidations.add(cacheKey);
                this.keyInvalidations.remove(cacheKey);
            }
        }
    }

    public Set<QueryTypeCacheKey> getQueryTypesInvalidations() {
        return this.queryTypesInvalidations;
    }

    public Map<QueryTypeCacheKey, Set<String>> getKeyInvalidations() {
        return this.keyInvalidations;
    }
}

