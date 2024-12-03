/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.model.Task;
import java.util.List;
import java.util.concurrent.Future;

public interface TaskService {
    public Future<Task> createOrUpdate(String var1, Task var2);

    public Future<List<Task>> createOrUpdate(String var1, List<Task> var2);

    public void delete(String var1, String var2);

    public Task find(String var1, String var2);

    public Future<Task> markComplete(String var1, String var2);

    public Future<Task> markIncomplete(String var1, String var2);

    public Future<Task> setTitle(String var1, String var2, String var3);
}

