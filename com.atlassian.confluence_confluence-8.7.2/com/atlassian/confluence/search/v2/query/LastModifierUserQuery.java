/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LastModifierUserQuery
implements SearchQuery {
    private static final String KEY = "lastModifierUser";
    private final Collection<ConfluenceUser> lastModifiers;

    public LastModifierUserQuery(Set<ConfluenceUser> lastModifiers) {
        if (lastModifiers == null) {
            throw new IllegalArgumentException("lastModifiers cannot be null");
        }
        if (lastModifiers.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one lastModifier");
        }
        this.lastModifiers = lastModifiers;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LastModifierUserQuery that = (LastModifierUserQuery)o;
        return this.lastModifiers.equals(that.lastModifiers);
    }

    public int hashCode() {
        return this.lastModifiers.hashCode();
    }

    @Override
    public SearchQuery expand() {
        BooleanQuery.Builder builder = BooleanQuery.builder();
        this.lastModifiers.forEach(user -> builder.addShould(new TermQuery(SearchFieldNames.LAST_MODIFIER, user.getKey().getStringValue())));
        return builder.build();
    }
}

