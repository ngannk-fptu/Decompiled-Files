/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.QueryBuilderProperties;
import com.atlassian.marketplace.client.api.QueryProperties;
import com.google.common.base.Preconditions;

public final class VendorQuery
implements QueryProperties.Bounds {
    private static final VendorQuery DEFAULT_QUERY = VendorQuery.builder().build();
    private final QueryBounds bounds;
    private final boolean forThisUserOnly;

    public static Builder builder() {
        return new Builder();
    }

    public static VendorQuery any() {
        return DEFAULT_QUERY;
    }

    public static Builder builder(VendorQuery query) {
        return VendorQuery.builder().bounds(query.getBounds()).forThisUserOnly(query.isForThisUserOnly());
    }

    private VendorQuery(Builder builder) {
        this.bounds = builder.bounds;
        this.forThisUserOnly = builder.forThisUserOnly;
    }

    @Override
    public QueryBounds getBounds() {
        return this.bounds;
    }

    public boolean isForThisUserOnly() {
        return this.forThisUserOnly;
    }

    public String toString() {
        return QueryProperties.describeParams("VendorQuery", QueryProperties.describeOptBoolean("forThisUserOnly", this.forThisUserOnly), this.bounds.describe());
    }

    public boolean equals(Object other) {
        return other instanceof VendorQuery ? this.toString().equals(other.toString()) : false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static class Builder
    implements QueryBuilderProperties.Bounds<Builder> {
        private QueryBounds bounds = QueryBounds.defaultBounds();
        private boolean forThisUserOnly;

        public VendorQuery build() {
            return new VendorQuery(this);
        }

        @Override
        public Builder bounds(QueryBounds bounds) {
            this.bounds = (QueryBounds)Preconditions.checkNotNull((Object)bounds);
            return this;
        }

        public Builder forThisUserOnly(boolean forThisUserOnly) {
            this.forThisUserOnly = forThisUserOnly;
            return this;
        }
    }
}

