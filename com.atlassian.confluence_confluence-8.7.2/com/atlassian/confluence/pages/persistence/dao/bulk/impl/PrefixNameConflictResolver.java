/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;

public class PrefixNameConflictResolver
implements PageNameConflictResolver {
    private static final int DEFAULT_MAX_RETRY = 5;
    private final String prefix;
    private final boolean shouldApplyForNewName;
    private final int maxRetry;

    public PrefixNameConflictResolver(String prefix) {
        this(false, prefix, 5);
    }

    public PrefixNameConflictResolver(boolean shouldApplyForNewName, String prefix) {
        this(shouldApplyForNewName, prefix, 5);
    }

    public PrefixNameConflictResolver(boolean shouldApplyForNewName, String prefix, int maxRetry) {
        this.shouldApplyForNewName = shouldApplyForNewName;
        this.prefix = prefix;
        this.maxRetry = maxRetry;
    }

    @Override
    public boolean couldProvideNewName() {
        return this.shouldApplyForNewName;
    }

    @Override
    public int getMaxRetryNumber() {
        return this.maxRetry;
    }

    @Override
    public String resolveConflict(int currentRetryNumber, String originalName) {
        return this.prefix + originalName;
    }
}

