/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;

@Immutable
class CachedCrowdUserEmailSearchQuery
implements Serializable {
    private final long directoryId;
    private final String email;
    private final int maxResults;
    private final int startIndex;

    public CachedCrowdUserEmailSearchQuery(long directoryId, EntityQuery<?> emailQuery) {
        this.directoryId = directoryId;
        this.maxResults = emailQuery.getMaxResults();
        this.startIndex = emailQuery.getStartIndex();
        SearchRestriction searchRestriction = emailQuery.getSearchRestriction();
        if (!(searchRestriction instanceof TermRestriction)) {
            throw new IllegalArgumentException("Not a simple term restriction search");
        }
        TermRestriction term = (TermRestriction)searchRestriction;
        if (!"email".equals(term.getProperty().getPropertyName())) {
            throw new IllegalArgumentException("Not a simple email search");
        }
        this.email = IdentifierUtils.toLowerCase((String)((String)term.getValue()));
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getEmail() {
        return this.email;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public EntityQuery<TimestampedUser> toEmailQuery() {
        return QueryBuilder.queryFor(TimestampedUser.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.EMAIL).exactlyMatching((Object)this.email)).startingAt(this.startIndex).returningAtMost(this.maxResults);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CachedCrowdUserEmailSearchQuery that = (CachedCrowdUserEmailSearchQuery)o;
        if (this.directoryId != that.directoryId) {
            return false;
        }
        if (this.maxResults != that.maxResults) {
            return false;
        }
        if (this.startIndex != that.startIndex) {
            return false;
        }
        return !(this.email != null ? !this.email.equals(that.email) : that.email != null);
    }

    public int hashCode() {
        int result = (int)(this.directoryId ^ this.directoryId >>> 32);
        result = 31 * result + (this.email != null ? this.email.hashCode() : 0);
        result = 31 * result + this.maxResults;
        result = 31 * result + this.startIndex;
        return result;
    }

    public String toString() {
        return "CachedCrowdEntityCacheKey{directoryId=" + this.directoryId + ", email='" + this.email + "', startIndex=" + this.startIndex + ", maxResults=" + this.maxResults + "}";
    }
}

