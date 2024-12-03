/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Task
 *  javax.annotation.Nonnull
 */
package com.atlassian.mywork.host.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.model.Task;
import com.atlassian.sal.usercompatibility.UserKey;
import javax.annotation.Nonnull;

@Transactional
public interface TaskDao {
    public Task get(long var1);

    public Iterable<Task> findAll(String var1);

    public Task find(String var1, String var2);

    public Pair<Boolean, Task> createOrUpdate(Task var1);

    public Task update(Task var1);

    public Task updateNotes(long var1, String var3);

    public Task delete(String var1, String var2);

    public Task delete(String var1, long var2);

    public int deleteOldCompletedTasks(int var1);

    public int deleteExpiredTasks(int var1);

    public int deleteAll(@Nonnull UserKey var1);

    public boolean hasTasksToMigrate(String var1);

    public Iterable<Task> findAllTasksToMigrate(String var1);

    public Iterable<Task> findAllTasksByEntity(String var1, String var2);
}

