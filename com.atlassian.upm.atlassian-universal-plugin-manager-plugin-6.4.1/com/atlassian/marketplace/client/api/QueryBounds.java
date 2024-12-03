/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.util.Convert;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.Optional;

public class QueryBounds {
    private static final QueryBounds DEFAULT = new QueryBounds(0, Optional.empty());
    private static final QueryBounds EMPTY = new QueryBounds(0, Optional.of(0));
    private final int offset;
    private final Optional<Integer> limit;

    private QueryBounds(int offset, Optional<Integer> limit) {
        Preconditions.checkArgument((offset >= 0 ? 1 : 0) != 0, (Object)"offset may not be negative");
        Iterator iterator = Convert.iterableOf((Optional)Preconditions.checkNotNull(limit)).iterator();
        while (iterator.hasNext()) {
            int l = (Integer)iterator.next();
            Preconditions.checkArgument((l >= 0 ? 1 : 0) != 0, (Object)"limit may not be negative");
        }
        this.offset = offset;
        this.limit = limit;
    }

    public static QueryBounds offset(int offset) {
        return new QueryBounds(offset, Optional.empty());
    }

    public static QueryBounds limit(Optional<Integer> limit) {
        return new QueryBounds(0, limit);
    }

    public static QueryBounds defaultBounds() {
        return DEFAULT;
    }

    public static QueryBounds empty() {
        return EMPTY;
    }

    public int getOffset() {
        return this.offset;
    }

    public Optional<Integer> safeGetLimit() {
        return this.limit;
    }

    public QueryBounds withOffset(int offset) {
        return new QueryBounds(offset, this.limit);
    }

    public QueryBounds withLimit(Optional<Integer> limit) {
        return new QueryBounds(this.offset, limit);
    }

    public boolean equals(Object other) {
        if (other instanceof QueryBounds) {
            QueryBounds o = (QueryBounds)other;
            return this.offset == o.offset && this.limit.equals(o.limit);
        }
        return false;
    }

    public int hashCode() {
        return this.offset + this.limit.hashCode();
    }

    public Iterable<String> describe() {
        ImmutableList.Builder ret = ImmutableList.builder();
        if (this.offset > 0) {
            ret.add((Object)("offset(" + this.offset + ")"));
        }
        for (Integer l : Convert.iterableOf(this.limit)) {
            ret.add((Object)("limit(" + l + ")"));
        }
        return ret.build();
    }
}

