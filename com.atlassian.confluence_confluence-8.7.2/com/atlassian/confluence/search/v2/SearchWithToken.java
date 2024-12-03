/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.ISearch;

@Deprecated(since="8.7.0", forRemoval=true)
public interface SearchWithToken
extends ISearch {
    public long getSearchToken();
}

