/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchivedSpacesQuery
implements SearchQuery {
    public static final String KEY = "archivedSpaces";
    private final boolean inverse;
    private final SpaceManager spaceManager;

    public ArchivedSpacesQuery(boolean inverse, SpaceManager spaceManager) {
        this.inverse = inverse;
        this.spaceManager = spaceManager;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }

    @Override
    public SearchQuery expand() {
        Collection<String> archivedSpaceKeys = this.spaceManager.getAllSpaceKeys(SpaceStatus.ARCHIVED);
        if (archivedSpaceKeys.isEmpty()) {
            return this.isInverse() ? MatchNoDocsQuery.getInstance() : AllQuery.getInstance();
        }
        Set termSearchFilters = archivedSpaceKeys.stream().map(key -> new TermQuery(SearchFieldNames.SPACE_KEY, (String)key)).collect(Collectors.toSet());
        BooleanQuery.Builder builder = BooleanQuery.builder();
        if (this.isInverse()) {
            builder.addShould(termSearchFilters);
        } else {
            builder.addMustNot(termSearchFilters);
        }
        return builder.build();
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArchivedSpacesQuery)) {
            return false;
        }
        ArchivedSpacesQuery that = (ArchivedSpacesQuery)obj;
        return this.isInverse() == that.isInverse();
    }

    public int hashCode() {
        return Objects.hash(this.inverse);
    }
}

