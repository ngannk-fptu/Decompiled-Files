/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.tasklist.ao.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.service.TaskPaginationService;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@Transactional
public interface InlineTaskDao {
    public Task create(Task var1);

    public Task update(Task var1);

    public Task find(long var1);

    public Task find(long var1, long var3);

    public long countAll();

    public List<Task> findAll();

    public List<Task> findByContentId(long var1);

    public Set<Long> findTaskIdsByContentId(long var1);

    public List<Task> findByCreator(UserKey var1);

    public List<Task> findByAssignee(UserKey var1);

    public void delete(long var1);

    public void delete(long var1, long var3);

    public void deleteAll();

    public void deleteByContentId(long var1);

    public void deleteBySpaceId(long var1);

    public PageResponse<Task> searchTask(@Nonnull SearchTaskParameters var1, TaskPaginationService var2, PageRequest var3);

    public AOInlineTask[] getFirstTasksOrderedById(int var1);

    public AOInlineTask[] getTasksWithIdGreaterThan(long var1, int var3);

    public AOInlineTask get(long var1);

    public Collection<AOInlineTask> getByContentId(long var1);
}

