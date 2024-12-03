/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;

public class DefaultPageNameConflictResolver
implements PageNameConflictResolver {
    public static final int MAX_RETRY = 10;
    private PageNameConflictResolver delegateConflictResolver;

    public DefaultPageNameConflictResolver(PageNameConflictResolver delegateConflictResolver) {
        this.delegateConflictResolver = delegateConflictResolver;
    }

    public DefaultPageNameConflictResolver() {
        this(null);
    }

    @Override
    public boolean couldProvideNewName() {
        return this.delegateConflictResolver == null ? false : this.delegateConflictResolver.couldProvideNewName();
    }

    @Override
    public int getMaxRetryNumber() {
        return this.delegateConflictResolver == null ? 10 : this.delegateConflictResolver.getMaxRetryNumber();
    }

    @Override
    public String resolveConflict(int currentRetryNumber, String originalName) {
        int lastOpenBraketIndex = originalName.lastIndexOf("(");
        if (currentRetryNumber > 0 && lastOpenBraketIndex > 0) {
            originalName = originalName.substring(0, lastOpenBraketIndex - 1);
        }
        String newName = this.delegateConflictResolver == null ? originalName : this.delegateConflictResolver.resolveConflict(currentRetryNumber, originalName);
        newName = currentRetryNumber > 0 ? newName + " (" + (currentRetryNumber + 1) + ")" : newName;
        return newName.trim();
    }
}

