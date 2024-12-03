/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.impl.admin.actions;

import com.atlassian.confluence.impl.admin.actions.AbstractViewIndexQueueAction;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@WebSudoRequired
@AdminOnly
public class ViewChangeIndexQueueAction<T extends IndexTask>
extends AbstractViewIndexQueueAction<T> {
    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CHANGE;
    }

    public void setLuceneChangeIndexManager(IncrementalIndexManager luceneChangeIndexManager) {
        this.indexManager = luceneChangeIndexManager;
    }

    public void setChangeTaskQueue(IndexTaskQueue<T> changeTaskQueue) {
        this.taskQueue = changeTaskQueue;
    }
}

