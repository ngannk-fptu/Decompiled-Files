/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.embedded.api.SearchRestriction;

public interface Query<T> {
    public int getStartIndex();

    public int getMaxResults();

    public Class<T> getReturnType();

    public SearchRestriction getSearchRestriction();
}

