/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.application.search;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectoryQueryWithFilter<T> {
    @Nonnull
    private final Directory directory;
    @Nonnull
    private final Query<T> query;
    @Nonnull
    private final UnaryOperator<List<T>> filter;

    public DirectoryQueryWithFilter(@Nonnull Directory directory, @Nullable Query<T> query, @Nonnull UnaryOperator<List<T>> filter) {
        this.directory = Objects.requireNonNull(directory);
        this.query = Objects.requireNonNull(query);
        this.filter = Objects.requireNonNull(filter);
    }

    public Directory getDirectory() {
        return this.directory;
    }

    @Nonnull
    public Query<T> getQuery() {
        return this.query;
    }

    @Nonnull
    public MembershipQuery<T> getMembershipQuery() {
        return (MembershipQuery)this.query;
    }

    public List<T> filterResults(List<T> list) {
        return (List)this.filter.apply(list);
    }
}

