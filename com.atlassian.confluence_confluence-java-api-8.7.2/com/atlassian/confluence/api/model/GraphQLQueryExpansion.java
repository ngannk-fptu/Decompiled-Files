/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import java.util.Objects;

@Internal
public class GraphQLQueryExpansion
extends Expansion {
    private final String query;

    public GraphQLQueryExpansion(String query) {
        super(null);
        this.query = Objects.requireNonNull(query);
    }

    public String getQuery() {
        return this.query;
    }

    @Override
    public String getPropertyName() {
        throw new UnsupportedOperationException("GraphQLQueryExpansion is not supported in this context");
    }

    @Override
    public Expansions getSubExpansions() {
        throw new UnsupportedOperationException("GraphQLQueryExpansion is not supported in this context");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphQLQueryExpansion)) {
            return false;
        }
        GraphQLQueryExpansion rhs = (GraphQLQueryExpansion)obj;
        return this.query.equals(rhs.getQuery());
    }

    @Override
    public int hashCode() {
        return this.query.hashCode();
    }

    @Override
    public String toString() {
        return this.query;
    }
}

