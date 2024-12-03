/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.search;

import com.atlassian.sal.api.search.SearchResults;
import com.atlassian.sal.api.user.UserKey;

public interface SearchProvider {
    @Deprecated
    public SearchResults search(String var1, String var2);

    public SearchResults search(UserKey var1, String var2);
}

