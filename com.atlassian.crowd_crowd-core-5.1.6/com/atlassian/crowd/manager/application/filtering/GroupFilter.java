/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.manager.application.filtering;

import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class GroupFilter {
    private DirectoryManagerSearchWrapper searcher;
    private final long directoryId;
    private final IdentifierMap<String> withAccess;
    private Set<String> toCheck;

    GroupFilter(DirectoryManagerSearchWrapper searcher, ApplicationDirectoryMapping mapping) {
        Preconditions.checkArgument((!mapping.isAllowAllToAuthenticate() ? 1 : 0) != 0);
        this.searcher = searcher;
        this.directoryId = mapping.getDirectory().getId();
        this.withAccess = new IdentifierMap();
        mapping.getAuthorisedGroupNames().forEach(e -> {
            String cfr_ignored_0 = (String)this.withAccess.put(e, e);
        });
        this.toCheck = new HashSet<String>(mapping.getAuthorisedGroupNames());
    }

    boolean hasAccess(String name) {
        return this.anyHasAccess((Collection<String>)ImmutableList.of((Object)name));
    }

    boolean anyHasAccess(Collection<String> names) {
        Set lowerCasedNames = IdentifierUtils.toLowerCase(names);
        if (this.anyHasAccessInternal(lowerCasedNames)) {
            return true;
        }
        while (!this.toCheck.isEmpty()) {
            this.fetchNext();
            if (!this.anyHasAccessInternal(lowerCasedNames)) continue;
            return true;
        }
        return false;
    }

    private boolean anyHasAccessInternal(Collection<String> lowerCasedNames) {
        for (String lowerCasedName : lowerCasedNames) {
            if (!this.withAccess.keySet().contains(lowerCasedName)) continue;
            return true;
        }
        return false;
    }

    Collection<String> getAllWithAccess() {
        while (!this.toCheck.isEmpty()) {
            this.fetchNext();
        }
        return this.withAccess.values();
    }

    private void fetchNext() {
        MembershipQuery membershipQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).childrenOf(EntityDescriptor.group()).withNames(this.toCheck).returningAtMost(-1);
        List names = this.searcher.searchDirectGroupRelationships(this.directoryId, membershipQuery);
        this.toCheck = names.stream().filter(e -> this.withAccess.put(e, e) == null).collect(Collectors.toSet());
    }

    public boolean isEmpty() {
        return this.withAccess.isEmpty();
    }
}

