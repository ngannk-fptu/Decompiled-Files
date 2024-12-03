/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Indexer
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.search;

import com.atlassian.bonnie.Indexer;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.spaces.Space;

public interface ConfluenceIndexer
extends Indexer {
    public void index(Searchable var1);

    public void unIndex(Searchable var1);

    public void reIndex(Searchable var1);

    public void reIndexExcludingDependents(Searchable var1);

    public void unIndexSpace(Space var1);

    public void reindexUsersInGroup(String var1);

    public void unIndexIncludingDependents(Searchable var1);

    public void indexIncludingDependents(Searchable var1);

    default public ConfluenceIndexer synchronous() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

