/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContainingContentTypeQuery
implements SearchQuery {
    public static final String KEY = "containingContentType";
    private final ContentTypeQuery contentTypeQuery;

    public static SearchQuery searchForTypesWithinContainerType(ContentTypeEnum containerType, Set<ContentTypeEnum> contentTypes) {
        return (SearchQuery)BooleanQuery.builder().addMust((U[])new SearchQuery[]{new ContainingContentTypeQuery(containerType), new ContentTypeQuery(contentTypes)}).build();
    }

    public ContainingContentTypeQuery(ContentTypeEnum contentType) {
        this(new ContentTypeQuery(contentType));
    }

    public ContainingContentTypeQuery(Collection<ContentTypeEnum> contentTypes) {
        this(new ContentTypeQuery(contentTypes));
    }

    private ContainingContentTypeQuery(ContentTypeQuery query) {
        this.contentTypeQuery = query;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> getParameters() {
        return this.contentTypeQuery.getParameters();
    }

    public Set<ContentTypeEnum> getContentTypes() {
        return this.contentTypeQuery.getContentTypes();
    }

    @Override
    public SearchQuery expand() {
        SearchQuery toWrap = this.internalConvertToV2Query();
        return new ConstantScoreQuery(toWrap);
    }

    private SearchQuery internalConvertToV2Query() {
        Set termQueries = this.contentTypeQuery.getContentTypes().stream().map(x -> new TermQuery(SearchFieldNames.CONTAINER_CONTENT_TYPE, x.getRepresentation())).collect(Collectors.toSet());
        return (SearchQuery)BooleanQuery.builder().addShould(termQueries).build();
    }

    public boolean equals(Object obj) {
        return obj instanceof ContainingContentTypeQuery && this.contentTypeQuery.equals(((ContainingContentTypeQuery)obj).contentTypeQuery);
    }

    public int hashCode() {
        return this.contentTypeQuery.hashCode();
    }
}

