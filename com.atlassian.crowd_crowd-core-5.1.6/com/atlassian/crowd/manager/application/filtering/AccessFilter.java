/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 */
package com.atlassian.crowd.manager.application.filtering;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.application.search.DirectoryQueryWithFilter;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface AccessFilter {
    public static final AccessFilter UNFILTERED = new AccessFilter(){

        @Override
        public boolean requiresFiltering(Entity entity) {
            return false;
        }

        @Override
        public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory directory, EntityQuery<T> query) {
            return Optional.of(new DirectoryQueryWithFilter<T>(directory, query, UnaryOperator.identity()));
        }

        @Override
        public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory directory, MembershipQuery<T> query) {
            return Optional.of(new DirectoryQueryWithFilter<T>(directory, query, UnaryOperator.identity()));
        }

        @Override
        public boolean hasAccess(long directoryId, Entity entity, String name) {
            return true;
        }
    };

    public boolean requiresFiltering(Entity var1);

    public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory var1, EntityQuery<T> var2);

    public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory var1, MembershipQuery<T> var2);

    public boolean hasAccess(long var1, Entity var3, String var4);
}

