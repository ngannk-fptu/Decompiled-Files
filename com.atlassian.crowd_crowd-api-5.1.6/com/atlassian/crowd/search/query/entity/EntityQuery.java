/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search.query.entity;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class EntityQuery<T>
implements Query<T> {
    private final EntityDescriptor entityDescriptor;
    private final SearchRestriction searchRestriction;
    private final int startIndex;
    private final int maxResults;
    private Class<T> returnType;
    public static final int MAX_MAX_RESULTS = 1000;
    public static final int ALL_RESULTS = -1;

    public EntityQuery(Class<T> returnType, EntityDescriptor entityDescriptor, SearchRestriction searchRestriction, int startIndex, int maxResults) {
        Validate.notNull((Object)entityDescriptor, (String)"entity cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)searchRestriction, (String)"searchRestriction cannot be null", (Object[])new Object[0]);
        Validate.notNull(returnType, (String)"returnType cannot be null", (Object[])new Object[0]);
        Validate.isTrue((maxResults == -1 || maxResults > 0 ? 1 : 0) != 0, (String)"maxResults must be greater than 0 (unless set to EntityQuery.ALL_RESULTS)", (Object[])new Object[0]);
        Validate.isTrue((startIndex >= 0 ? 1 : 0) != 0, (String)"startIndex cannot be less than zero", (Object[])new Object[0]);
        this.entityDescriptor = entityDescriptor;
        this.searchRestriction = searchRestriction;
        this.startIndex = startIndex;
        this.maxResults = maxResults;
        this.returnType = returnType;
    }

    public EntityQuery(EntityQuery query, Class<T> returnType) {
        this(returnType, query.getEntityDescriptor(), query.getSearchRestriction(), query.getStartIndex(), query.getMaxResults());
    }

    public EntityQuery(EntityQuery<T> query, int startIndex, int maxResults) {
        this(query.getReturnType(), query.getEntityDescriptor(), query.getSearchRestriction(), startIndex, maxResults);
    }

    public EntityDescriptor getEntityDescriptor() {
        return this.entityDescriptor;
    }

    public SearchRestriction getSearchRestriction() {
        return this.searchRestriction;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public Class<T> getReturnType() {
        return this.returnType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityQuery)) {
            return false;
        }
        EntityQuery query = (EntityQuery)o;
        if (this.maxResults != query.maxResults) {
            return false;
        }
        if (this.startIndex != query.startIndex) {
            return false;
        }
        if (this.entityDescriptor != null ? !this.entityDescriptor.equals(query.entityDescriptor) : query.entityDescriptor != null) {
            return false;
        }
        if (this.returnType != query.returnType) {
            return false;
        }
        return !(this.searchRestriction != null ? !this.searchRestriction.equals(query.searchRestriction) : query.searchRestriction != null);
    }

    public static int addToMaxResults(int maxResults, int add) {
        if (maxResults == -1) {
            return -1;
        }
        long sum = (long)maxResults + (long)add;
        if (sum < 0L) {
            return 0;
        }
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }

    public static long allResultsToLongMax(int maxResults) {
        return maxResults == -1 ? Long.MAX_VALUE : (long)maxResults;
    }

    public int hashCode() {
        int result = this.entityDescriptor != null ? this.entityDescriptor.hashCode() : 0;
        result = 31 * result + (this.searchRestriction != null ? this.searchRestriction.hashCode() : 0);
        result = 31 * result + this.startIndex;
        result = 31 * result + this.maxResults;
        result = 31 * result + (this.returnType != null ? this.returnType.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("entity", (Object)this.entityDescriptor).append("returnType", this.returnType).append("searchRestriction", (Object)this.searchRestriction).append("startIndex", this.startIndex).append("maxResults", this.maxResults).toString();
    }

    public <Q> EntityQuery<Q> withReturnType(Class<Q> returnType) {
        return QueryBuilder.queryFor(returnType, this.entityDescriptor, this.searchRestriction, this.startIndex, this.maxResults);
    }

    public EntityQuery<T> withStartIndex(int startIndex) {
        return this.withStartIndexAndMaxResults(startIndex, this.maxResults);
    }

    public EntityQuery<T> withMaxResults(int maxResults) {
        return this.withStartIndexAndMaxResults(this.startIndex, maxResults);
    }

    public EntityQuery<T> withStartIndexAndMaxResults(int startIndex, int maxResults) {
        return QueryBuilder.queryFor(this.returnType, this.entityDescriptor, this.searchRestriction, startIndex, maxResults);
    }

    public EntityQuery<T> withSearchRestriction(SearchRestriction searchRestriction) {
        return QueryBuilder.queryFor(this.returnType, this.entityDescriptor, searchRestriction, this.startIndex, this.maxResults);
    }

    public EntityQuery<T> baseSplitQuery() {
        return this.withStartIndexAndMaxResults(0, EntityQuery.addToMaxResults(this.maxResults, this.startIndex));
    }

    public Optional<List<EntityQuery<T>>> splitOrRestrictionIfNeeded(int maxSize) {
        BooleanRestriction booleanRestriction;
        if (this.searchRestriction instanceof BooleanRestriction && (booleanRestriction = (BooleanRestriction)this.searchRestriction).getBooleanLogic() == BooleanRestriction.BooleanLogic.OR && booleanRestriction.getRestrictions().size() > maxSize) {
            EntityQuery base = this.baseSplitQuery();
            return Optional.of(Lists.partition((List)ImmutableList.copyOf((Collection)booleanRestriction.getRestrictions()), (int)maxSize).stream().map(restrictions -> base.withSearchRestriction((SearchRestriction)Combine.anyOf((Collection)restrictions))).collect(Collectors.toList()));
        }
        return Optional.empty();
    }

    public EntityQuery<T> withAllResults() {
        return this.withStartIndexAndMaxResults(0, -1);
    }

    public EntityQuery<T> addToMaxResults(int add) {
        return this.withMaxResults(EntityQuery.addToMaxResults(this.maxResults, add));
    }

    public boolean hasAllResults() {
        return this.startIndex == 0 && this.maxResults == -1;
    }
}

