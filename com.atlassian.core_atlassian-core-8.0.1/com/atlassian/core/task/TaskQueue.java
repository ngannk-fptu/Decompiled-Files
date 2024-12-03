/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.task;

import com.atlassian.core.task.Task;
import java.sql.Timestamp;
import java.util.Collection;

public interface TaskQueue {
    public void flush();

    public int size();

    public void addTask(Task var1);

    public boolean isFlushing();

    public Timestamp getFlushStarted();

    public void clear();

    public Collection<Task> getTasks();
}

