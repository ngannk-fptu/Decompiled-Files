/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.tasklist.helpers;

import com.atlassian.confluence.plugins.tasklist.helpers.TaskStorageFormatBuilder;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.List;

public class TaskListStorageFormatBuilder {
    private List<String> tasks = Lists.newArrayList();

    public TaskListStorageFormatBuilder addTask(TaskStorageFormatBuilder task) {
        return this.addTask(task.build());
    }

    public TaskListStorageFormatBuilder addTask(String task) {
        this.tasks.add(task);
        return this;
    }

    public String build() {
        return "<ac:task-list>\n" + Joiner.on((String)"\n").join(this.tasks) + "\n</ac:task-list>";
    }
}

