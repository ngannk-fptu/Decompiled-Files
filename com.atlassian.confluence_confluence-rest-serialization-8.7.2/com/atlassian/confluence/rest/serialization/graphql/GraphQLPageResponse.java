/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 */
package com.atlassian.confluence.rest.serialization.graphql;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPagination;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationEdge;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@GraphQLTypeName(value="Paginated{T1}List")
public class GraphQLPageResponse<T>
extends GraphQLPagination<T>
implements PageResponse<T> {
    public GraphQLPageResponse() {
    }

    public GraphQLPageResponse(PageResponse<T> pageResponse, BiFunction<T, Integer, String> cursorMapper) {
        this(pageResponse, GraphQLPageResponse.buildEdges(pageResponse.getResults(), cursorMapper));
    }

    public GraphQLPageResponse(PageResponse<T> pageResponse, List<GraphQLPaginationEdge<T>> edges) {
        super(pageResponse.getResults(), edges, pageResponse.hasMore());
    }

    public Iterator<T> iterator() {
        return this.getResults().iterator();
    }

    public List<T> getResults() {
        return this.getNodes() != null ? this.getNodes() : this.getEdges().stream().map(GraphQLPaginationEdge::getNode).collect(Collectors.toList());
    }

    public int size() {
        return this.getCount();
    }

    public boolean hasMore() {
        return this.getPageInfo() != null && this.getPageInfo().hasNextPage();
    }

    public PageRequest getPageRequest() {
        return null;
    }
}

