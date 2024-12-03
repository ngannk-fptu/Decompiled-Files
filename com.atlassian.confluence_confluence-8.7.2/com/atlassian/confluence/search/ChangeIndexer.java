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

public interface ChangeIndexer
extends Indexer {
    public void reIndexAllVersions(Searchable var1);

    default public ChangeIndexer synchronous() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void unIndexSpace(Space var1);

    public void reindexUsersInGroup(String var1);
}

