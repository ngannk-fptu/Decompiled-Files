/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.rpc.NotPermittedException
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.TaskStatus;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.service.InlineTaskResponse;
import com.atlassian.confluence.rpc.NotPermittedException;
import java.util.Set;

public interface InlineTaskService {
    public InlineTaskResponse setTaskStatus(ContentEntityObject var1, String var2, TaskStatus var3, PageUpdateTrigger var4) throws NotPermittedException;

    public Task find(long var1);

    public Task find(long var1, long var3);

    public Set<Long> findTaskIdsByContentId(long var1);

    public Task create(Task var1);

    public Task update(Task var1, String var2, boolean var3, boolean var4);

    public void delete(long var1);

    public void delete(long var1, long var3);

    public void delete(Task var1);

    public void deleteBySpaceId(long var1);

    public PageResponse<Task> searchTasks(SearchTaskParameters var1);

    public long countAllTasks();
}

