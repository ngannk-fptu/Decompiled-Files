/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.macro.TaskEntity;
import com.atlassian.user.EntityException;
import java.util.List;

public interface TaskEntityHelper {
    public TaskEntity createSingleTaskEntity(Task var1) throws EntityException;

    public List<TaskEntity> createTaskEntities(List<Task> var1);
}

