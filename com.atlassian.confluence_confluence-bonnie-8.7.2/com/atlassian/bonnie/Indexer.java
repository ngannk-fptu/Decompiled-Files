/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bonnie;

import com.atlassian.bonnie.Searchable;

public interface Indexer {
    public void index(Searchable var1);

    public void unIndex(Searchable var1);

    public void reIndex(Searchable var1);
}

