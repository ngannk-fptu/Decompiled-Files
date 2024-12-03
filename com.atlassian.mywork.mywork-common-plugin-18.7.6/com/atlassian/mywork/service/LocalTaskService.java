/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.service.TaskService;

public interface LocalTaskService
extends TaskService {
    @Override
    public void delete(String var1, String var2);

    public Iterable<Task> findAll(String var1);

    public boolean hasTasksToMigrate(String var1);

    public Iterable<Task> findAllTasksToMigrate(String var1);

    public Iterable<Task> findAllTasksByType(String var1, String var2);

    public void delete(String var1, long var2);

    public Task get(long var1);

    public void moveBefore(String var1, long var2, Long var4);

    public Task update(String var1, Task var2);

    public Task updateNotes(String var1, long var2, String var4);
}

