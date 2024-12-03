/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpandable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.serialization.graphql;

import com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationEdge;
import com.atlassian.confluence.rest.serialization.graphql.GraphQLPaginationInfo;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.graphql.annotations.expansions.GraphQLExpandable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.codehaus.jackson.annotate.JsonProperty;

@GraphQLTypeName(value="Paginated{T1}List")
public class GraphQLPagination<T> {
    @JsonProperty
    private Integer count;
    @JsonProperty
    private List<T> nodes;
    @JsonProperty
    private List<GraphQLPaginationEdge<T>> edges;
    @JsonProperty
    private GraphQLPaginationInfo pageInfo;

    public GraphQLPagination() {
    }

    public GraphQLPagination(List<T> nodes, BiFunction<T, Integer, String> cursorMapper, boolean hasNextPage) {
        this(nodes, GraphQLPagination.buildEdges(nodes, cursorMapper), hasNextPage);
    }

    public GraphQLPagination(List<T> nodes, List<GraphQLPaginationEdge<T>> edges, boolean hasNextPage) {
        this.count = nodes.size();
        this.nodes = nodes;
        this.edges = edges;
        this.pageInfo = new GraphQLPaginationInfo(hasNextPage);
    }

    @GraphQLName
    @JsonProperty
    public Integer getCount() {
        this.load();
        return this.count;
    }

    protected void setCount(Integer count) {
        this.count = count;
    }

    @GraphQLName
    @JsonProperty
    @GraphQLExpandable(skip=true)
    public List<T> getNodes() {
        this.load();
        return this.nodes;
    }

    protected void setNodes(List<T> nodes) {
        this.nodes = nodes;
    }

    @GraphQLName
    @JsonProperty
    @GraphQLExpandable(skip=true)
    public List<GraphQLPaginationEdge<T>> getEdges() {
        this.load();
        return this.edges;
    }

    protected void setEdges(List<GraphQLPaginationEdge<T>> edges) {
        this.edges = edges;
    }

    @GraphQLName
    @JsonProperty
    public GraphQLPaginationInfo getPageInfo() {
        this.load();
        return this.pageInfo;
    }

    protected void setPageInfo(GraphQLPaginationInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    protected void load() {
    }

    protected static <T> List<GraphQLPaginationEdge<T>> buildEdges(List<T> nodes, BiFunction<T, Integer, String> cursorMapper) {
        ArrayList<GraphQLPaginationEdge<T>> list = new ArrayList<GraphQLPaginationEdge<T>>();
        int i = 0;
        for (T item : nodes) {
            list.add(new GraphQLPaginationEdge<T>(item, cursorMapper.apply(item, i)));
            ++i;
        }
        return list;
    }
}

