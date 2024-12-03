/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpandable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.serialization.graphql;

import com.atlassian.graphql.annotations.GraphQLTypeName;
import com.atlassian.graphql.annotations.expansions.GraphQLExpandable;
import org.codehaus.jackson.annotate.JsonProperty;

@GraphQLTypeName(value="{T1}Edge")
public class GraphQLPaginationEdge<T> {
    @JsonProperty
    @GraphQLExpandable(skip=true)
    private T node;
    @JsonProperty
    private String cursor;

    public GraphQLPaginationEdge() {
    }

    public GraphQLPaginationEdge(T node, String cursor) {
        this.node = node;
        this.cursor = cursor;
    }

    public T getNode() {
        return this.node;
    }

    public String getCursor() {
        return this.cursor;
    }
}

