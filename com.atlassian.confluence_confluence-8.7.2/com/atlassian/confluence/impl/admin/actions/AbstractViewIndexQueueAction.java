/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.impl.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.confluence.search.IndexTask;
import com.atlassian.confluence.search.IndexTaskQueue;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.List;

public abstract class AbstractViewIndexQueueAction<T extends IndexTask>
extends ConfluenceActionSupport {
    protected IncrementalIndexManager indexManager;
    protected IndexTaskQueue<T> taskQueue;
    private List<T> queuedEntries = null;

    public abstract SearchIndex getSearchIndex();

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public FlushStatistics getLastNonEmptyFlushStats() {
        return this.indexManager.getLastNonEmptyFlushStats();
    }

    public boolean isFlushing() {
        return this.indexManager.isFlushing();
    }

    public List<T> getQueue() {
        if (this.queuedEntries == null) {
            this.queuedEntries = this.taskQueue.getQueuedEntries();
        }
        return this.queuedEntries;
    }

    public Collection<T> getQueueFirstHundred() {
        List<T> queue = this.getQueue();
        if (queue.size() > 100) {
            return queue.subList(0, 100);
        }
        return queue;
    }
}

