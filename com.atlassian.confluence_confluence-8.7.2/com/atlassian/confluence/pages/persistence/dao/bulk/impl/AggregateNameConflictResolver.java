/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class AggregateNameConflictResolver
implements PageNameConflictResolver {
    private static final int MAX_RETRY = 5;
    private List<PageNameConflictResolver> pageNameConflictResolvers;

    public AggregateNameConflictResolver(PageNameConflictResolver ... pageNameConflictResolvers) {
        this.pageNameConflictResolvers = ImmutableList.copyOf((Object[])pageNameConflictResolvers);
    }

    @Override
    public boolean couldProvideNewName() {
        return this.pageNameConflictResolvers.stream().anyMatch(resolver -> resolver.couldProvideNewName());
    }

    @Override
    public int getMaxRetryNumber() {
        return 5;
    }

    @Override
    public String resolveConflict(int currentRetryNumber, String originalName) {
        Object newName = originalName;
        for (PageNameConflictResolver pageNameConflictResolver : this.pageNameConflictResolvers) {
            if (!pageNameConflictResolver.couldProvideNewName()) continue;
            newName = " " + pageNameConflictResolver.resolveConflict(currentRetryNumber, (String)newName);
        }
        return newName;
    }
}

