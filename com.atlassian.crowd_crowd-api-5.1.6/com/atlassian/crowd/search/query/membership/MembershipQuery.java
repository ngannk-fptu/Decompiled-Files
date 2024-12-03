/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search.query.membership;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MembershipQuery<T>
implements Query<T> {
    private final EntityDescriptor entityToReturn;
    private final EntityDescriptor entityToMatch;
    private final boolean findChildren;
    private final Set<String> entityNamesToMatch;
    private final int startIndex;
    private final int maxResults;
    private final Class<T> returnType;
    private final SearchRestriction searchRestriction;

    @Deprecated
    public MembershipQuery(Class<T> returnType, boolean findChildren, EntityDescriptor entityToMatch, String entityNameToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults) {
        this(returnType, findChildren, entityToMatch, entityToReturn, startIndex, maxResults, QueryBuilder.NULL_RESTRICTION, entityNameToMatch);
    }

    public MembershipQuery(Class<T> returnType, boolean findChildren, EntityDescriptor entityToMatch, String entityNameToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults, SearchRestriction searchRestriction) {
        this(returnType, findChildren, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, entityNameToMatch);
    }

    public MembershipQuery(Class<T> returnType, boolean findChildren, EntityDescriptor entityToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults, SearchRestriction searchRestriction, String ... entityNamesToMatch) {
        this(returnType, findChildren, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, MembershipQuery.validateAndCopyEntityNamesToMatch(entityNamesToMatch));
    }

    public MembershipQuery(Class<T> returnType, boolean findChildren, EntityDescriptor entityToMatch, EntityDescriptor entityToReturn, int startIndex, int maxResults, SearchRestriction searchRestriction, Collection<String> entityNamesToMatch) {
        Validate.notNull((Object)entityToMatch, (String)"entityToMatch argument cannot be null", (Object[])new Object[0]);
        Validate.notNull(entityNamesToMatch, (String)"entityNamesToMatch argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)entityToReturn, (String)"entityToReturn argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((maxResults == -1 || maxResults > 0 ? 1 : 0) != 0, (String)"maxResults must be greater than 0 (unless set to EntityQuery.ALL_RESULTS)", (Object[])new Object[0]);
        Validate.isTrue((startIndex >= 0 ? 1 : 0) != 0, (String)"startIndex cannot be less than zero", (Object[])new Object[0]);
        Validate.notNull(returnType, (String)"returnType cannot be null", (Object[])new Object[0]);
        if (findChildren) {
            Validate.isTrue((entityToMatch.getEntityType() == Entity.GROUP ? 1 : 0) != 0, (String)("Cannot find the children of type: " + entityToMatch), (Object[])new Object[0]);
        } else {
            Validate.isTrue((entityToReturn.getEntityType() == Entity.GROUP ? 1 : 0) != 0, (String)("Cannot return parents of type: " + entityToMatch), (Object[])new Object[0]);
        }
        this.entityToReturn = entityToReturn;
        this.entityToMatch = entityToMatch;
        this.findChildren = findChildren;
        this.entityNamesToMatch = MembershipQuery.validateAndCopyEntityNamesToMatch(entityNamesToMatch);
        this.startIndex = startIndex;
        this.maxResults = maxResults;
        this.returnType = returnType;
        this.searchRestriction = searchRestriction;
    }

    private static Set<String> validateAndCopyEntityNamesToMatch(Collection<String> entityNamesToMatch) {
        Validate.noNullElements(entityNamesToMatch, (String)"entityNamesToMatch argument cannot contain any null elements", (Object[])new Object[0]);
        return ImmutableSet.copyOf(entityNamesToMatch);
    }

    private static Set<String> validateAndCopyEntityNamesToMatch(String ... entityNamesToMatch) {
        Validate.noNullElements((Object[])entityNamesToMatch, (String)"entityNamesToMatch argument cannot contain any null elements", (Object[])new Object[0]);
        return ImmutableSet.copyOf((Object[])entityNamesToMatch);
    }

    public MembershipQuery(MembershipQuery<T> query, int startIndex, int maxResults) {
        this(query.getReturnType(), query.isFindChildren(), query.getEntityToMatch(), query.getEntityToReturn(), startIndex, maxResults, query.getSearchRestriction(), (String[])Iterables.toArray(query.getEntityNamesToMatch(), String.class));
    }

    public MembershipQuery(MembershipQuery<?> query, Class<T> returnType) {
        this(returnType, query.isFindChildren(), query.getEntityToMatch(), query.getEntityToReturn(), query.getStartIndex(), query.getMaxResults(), query.getSearchRestriction(), (String[])Iterables.toArray(query.getEntityNamesToMatch(), String.class));
    }

    public EntityDescriptor getEntityToReturn() {
        return this.entityToReturn;
    }

    public EntityDescriptor getEntityToMatch() {
        return this.entityToMatch;
    }

    public boolean isFindChildren() {
        return this.findChildren;
    }

    public Set<String> getEntityNamesToMatch() {
        return this.entityNamesToMatch;
    }

    @Deprecated
    @Nullable
    public String getEntityNameToMatch() {
        return (String)Iterables.getOnlyElement(this.entityNamesToMatch, null);
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

    public SearchRestriction getSearchRestriction() {
        return this.searchRestriction;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MembershipQuery)) {
            return false;
        }
        MembershipQuery that = (MembershipQuery)o;
        if (this.findChildren != that.findChildren) {
            return false;
        }
        if (this.maxResults != that.maxResults) {
            return false;
        }
        if (this.startIndex != that.startIndex) {
            return false;
        }
        if (!Objects.equals(this.entityNamesToMatch, that.entityNamesToMatch)) {
            return false;
        }
        if (this.entityToMatch != null ? !this.entityToMatch.equals(that.entityToMatch) : that.entityToMatch != null) {
            return false;
        }
        if (this.entityToReturn != null ? !this.entityToReturn.equals(that.entityToReturn) : that.entityToReturn != null) {
            return false;
        }
        return this.returnType == that.returnType;
    }

    public int hashCode() {
        int result = this.entityToReturn != null ? this.entityToReturn.hashCode() : 0;
        result = 31 * result + (this.entityToMatch != null ? this.entityToMatch.hashCode() : 0);
        result = 31 * result + (this.findChildren ? 1 : 0);
        result = 31 * result + (this.entityNamesToMatch != null ? Objects.hashCode(this.entityNamesToMatch) : 0);
        result = 31 * result + this.startIndex;
        result = 31 * result + this.maxResults;
        result = 31 * result + (this.returnType != null ? this.returnType.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("entityToReturn", (Object)this.entityToReturn).append("entityToMatch", (Object)this.entityToMatch).append("findChildren", this.findChildren).append("entityNamesToMatch", (Object)Iterables.toString(this.entityNamesToMatch)).append("startIndex", this.startIndex).append("maxResults", this.maxResults).append("returnType", (Object)this.returnType.getSimpleName()).toString();
    }

    public MembershipQuery<T> withEntityNames(Collection<String> entityNamesToMatch) {
        return new MembershipQuery<T>(this.returnType, this.findChildren, this.entityToMatch, this.entityToReturn, this.startIndex, this.maxResults, this.searchRestriction, entityNamesToMatch);
    }

    public MembershipQuery<T> withEntityNames(String ... entityNameToMatch) {
        return this.withEntityNames(MembershipQuery.validateAndCopyEntityNamesToMatch(entityNameToMatch));
    }

    public List<MembershipQuery<T>> splitEntityNamesToMatch() {
        return this.splitEntityNamesToMatch(1);
    }

    public List<MembershipQuery<T>> splitEntityNamesToMatch(int batchSize) {
        MembershipQuery<T> base = this.baseSplitQuery();
        return Lists.partition(new ArrayList<String>(this.entityNamesToMatch), (int)batchSize).stream().map(base::withEntityNames).collect(Collectors.toList());
    }

    public MembershipQuery<T> withAllResults() {
        return this.withStartIndexAndMaxResult(0, -1);
    }

    public MembershipQuery<T> withStartIndex(int startIndex) {
        return this.withStartIndexAndMaxResult(startIndex, this.maxResults);
    }

    public MembershipQuery<T> withMaxResults(int maxResults) {
        return this.withStartIndexAndMaxResult(this.startIndex, maxResults);
    }

    public <Q> MembershipQuery<Q> withReturnType(Class<Q> returnType) {
        return new MembershipQuery<Q>(returnType, this.findChildren, this.entityToMatch, this.entityToReturn, this.startIndex, this.maxResults, this.searchRestriction, this.entityNamesToMatch);
    }

    public MembershipQuery<T> withEntityToReturn(EntityDescriptor entityToReturn) {
        return new MembershipQuery<T>(this.returnType, this.findChildren, this.entityToMatch, entityToReturn, this.startIndex, this.maxResults, this.searchRestriction, this.entityNamesToMatch);
    }

    public MembershipQuery<T> baseSplitQuery() {
        return this.withStartIndexAndMaxResult(0, EntityQuery.addToMaxResults(this.maxResults, this.startIndex));
    }

    public MembershipQuery<T> addToMaxResults(int add) {
        return this.withMaxResults(EntityQuery.addToMaxResults(this.maxResults, add));
    }

    public MembershipQuery<T> withStartIndexAndMaxResult(int startIndex, int maxResults) {
        return new MembershipQuery<T>(this.returnType, this.findChildren, this.entityToMatch, this.entityToReturn, startIndex, maxResults, this.searchRestriction, this.entityNamesToMatch);
    }

    public MembershipQuery<T> withSearchRestriction(SearchRestriction searchRestriction) {
        return new MembershipQuery<T>(this.returnType, this.findChildren, this.entityToMatch, this.entityToReturn, this.startIndex, this.maxResults, searchRestriction, this.entityNamesToMatch);
    }

    public boolean isWithAllResults() {
        return this.getStartIndex() == 0 && this.getMaxResults() == -1;
    }
}

