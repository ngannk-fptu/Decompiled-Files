/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentTypeQuery
implements SearchQuery {
    public static final String KEY = "contentType";
    private final Set<ContentTypeEnum> contentTypes;

    public ContentTypeQuery(ContentTypeEnum contentType) {
        this(Collections.singletonList(contentType));
    }

    public ContentTypeQuery(Collection<ContentTypeEnum> contentTypes) {
        if (contentTypes == null) {
            throw new IllegalArgumentException("contentTypes should not be null");
        }
        if (contentTypes.isEmpty()) {
            throw new IllegalArgumentException("contentTypes should not be an empty list");
        }
        if (contentTypes.contains(null)) {
            throw new IllegalArgumentException("contentTypes should not contain a null value");
        }
        this.contentTypes = new HashSet<ContentTypeEnum>(contentTypes);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> getParameters() {
        ArrayList<String> types = new ArrayList<String>(this.contentTypes.size());
        for (ContentTypeEnum ct : this.contentTypes) {
            types.add(ct.getRepresentation());
        }
        return types;
    }

    public Set<ContentTypeEnum> getContentTypes() {
        return this.contentTypes;
    }

    @Override
    public SearchQuery expand() {
        SearchQuery query = this.contentTypes.size() == 1 ? this.toTermQuery(this.contentTypes.iterator().next()) : (SearchQuery)BooleanQuery.builder().addShould(this.contentTypes.stream().map(this::toTermQuery).collect(Collectors.toList())).build();
        return new ConstantScoreQuery(query);
    }

    private SearchQuery toTermQuery(ContentTypeEnum contentType) {
        return new TermQuery(SearchFieldNames.TYPE, contentType.getRepresentation());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ContentTypeQuery)) {
            return false;
        }
        ContentTypeQuery other = (ContentTypeQuery)obj;
        return this.contentTypes.equals(other.contentTypes);
    }

    public int hashCode() {
        return this.contentTypes.hashCode() + KEY.hashCode();
    }
}

