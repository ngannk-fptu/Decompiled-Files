/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.search.v2.SearchSort;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserSearchQueryParameters {
    private final @NonNull String query;
    private final @Nullable SearchSort sort;
    private final @NonNull Set<UserCategory> userCategories;
    private final boolean excludeEmptyUsernameUsers;

    private UserSearchQueryParameters(String query, @Nullable SearchSort sort, boolean excludeEmptyUsernameUsers, Set<UserCategory> userCategories) {
        this.query = Objects.requireNonNull(query, "query");
        this.sort = sort;
        this.excludeEmptyUsernameUsers = excludeEmptyUsernameUsers;
        this.userCategories = Objects.requireNonNull(userCategories, "userCategories");
    }

    public static Builder builder() {
        return new Builder().addUserCategory(UserCategory.LICENSED);
    }

    public boolean includeExternallyDeletedUsers() {
        return this.userCategories.contains((Object)UserCategory.EXTERNALLY_DELETED);
    }

    public boolean includeDeactivatedUsers() {
        return this.userCategories.contains((Object)UserCategory.DEACTIVATED);
    }

    public boolean includeUnlicensedUsers() {
        return this.userCategories.contains((Object)UserCategory.UNLICENSED);
    }

    public boolean excludeEmptyUsernameUsers() {
        return this.excludeEmptyUsernameUsers;
    }

    public @NonNull String getQuery() {
        return this.query;
    }

    public @Nullable SearchSort getSort() {
        return this.sort;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("query", (Object)this.query).append("sort", (Object)this.sort).append("excludeEmptyUsernameUsers", this.excludeEmptyUsernameUsers).append("userCategories", this.userCategories).build();
    }

    public static class Builder {
        private String query;
        private SearchSort sort;
        private ImmutableSet.Builder<UserCategory> userCategories = ImmutableSet.builder();
        private boolean excludeEmptyUsernameUsers;

        private Builder() {
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder sort(SearchSort sort) {
            this.sort = sort;
            return this;
        }

        public Builder addUserCategory(UserCategory userCategory) {
            this.userCategories.add((Object)userCategory);
            return this;
        }

        public Builder setExcludeEmptyUsernameUsers(boolean excludeEmptyUsernameUsers) {
            this.excludeEmptyUsernameUsers = excludeEmptyUsernameUsers;
            return this;
        }

        public UserSearchQueryParameters build() {
            return new UserSearchQueryParameters(this.query, this.sort, this.excludeEmptyUsernameUsers, (Set<UserCategory>)this.userCategories.build());
        }
    }

    public static enum UserCategory {
        EXTERNALLY_DELETED,
        DEACTIVATED,
        UNLICENSED,
        LICENSED;

    }
}

