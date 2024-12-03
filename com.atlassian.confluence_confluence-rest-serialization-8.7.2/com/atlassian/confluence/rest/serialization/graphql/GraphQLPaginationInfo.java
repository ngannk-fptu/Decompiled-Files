/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.graphql.annotations.GraphQLTypeName
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.rest.serialization.graphql;

import com.atlassian.graphql.annotations.GraphQLTypeName;
import org.codehaus.jackson.annotate.JsonProperty;

@GraphQLTypeName(value="PageInfo")
public class GraphQLPaginationInfo {
    @JsonProperty
    private boolean hasNextPage;

    public GraphQLPaginationInfo() {
    }

    public GraphQLPaginationInfo(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean hasNextPage() {
        return this.hasNextPage;
    }
}

