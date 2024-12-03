/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.GroupQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.UserQuery;
import com.atlassian.user.util.Assert;
import java.util.Collection;
import java.util.Collections;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class QueryValidator {
    public void assertValid(Query query) throws EntityQueryException {
        Collection<EntityQueryException> validationErrors = this.validateQuery(query);
        if (!validationErrors.isEmpty()) {
            throw validationErrors.iterator().next();
        }
    }

    public Collection<EntityQueryException> validateQuery(Query query) {
        Assert.notNull(query, "Query should not be null");
        if (!(query instanceof BooleanQuery)) {
            return Collections.emptyList();
        }
        BooleanQuery booleanQuery = (BooleanQuery)query;
        Class<? extends Query> queryType = this.getNestedQueryType(booleanQuery);
        return this.validateBooleanQuery(booleanQuery, queryType);
    }

    public Collection<EntityQueryException> validateBooleanQuery(BooleanQuery<?> booleanQuery, Class<? extends Query> queryType) {
        for (Query<?> query : booleanQuery.getQueries()) {
            if (query instanceof BooleanQuery) {
                return this.validateBooleanQuery((BooleanQuery)query, queryType);
            }
            if (queryType.isInstance(query)) continue;
            return Collections.singleton(new EntityQueryException("Boolean query type " + queryType.getName() + " isn't matched by clause: " + query));
        }
        return Collections.emptyList();
    }

    private Class<? extends Query> getNestedQueryType(BooleanQuery<?> booleanQuery) throws IllegalArgumentException {
        Query<?> query = booleanQuery.getQueries().get(0);
        if (query instanceof BooleanQuery) {
            return this.getNestedQueryType((BooleanQuery)query);
        }
        if (UserQuery.class.isInstance(query)) {
            return UserQuery.class;
        }
        if (GroupQuery.class.isInstance(query)) {
            return GroupQuery.class;
        }
        throw new IllegalArgumentException("Clause of unknown type in boolean query: " + booleanQuery);
    }
}

