/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task;

import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task.ModificationType;
import com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.task.TaskLevel;

public enum InlineTaskIndexTaskType {
    REINDEX_ALL(TaskLevel.EVERYTHING, ModificationType.REMOVE_AND_ADD),
    REMOVE_ALL_INLINE_TASKS_FROM_PAGE(TaskLevel.PAGE, ModificationType.REMOVE),
    REINDEX_INLINE_TASKS_FROM_PAGE(TaskLevel.PAGE, ModificationType.REMOVE_AND_ADD),
    REINDEX_INLINE_TASKS_FROM_PAGE_INCLUDING_CHILDREN(TaskLevel.ANCESTOR, ModificationType.REMOVE_AND_ADD),
    REINDEX_INLINE_TASK(TaskLevel.TASK, ModificationType.REMOVE_AND_ADD),
    REMOVE_INLINE_TASK(TaskLevel.TASK, ModificationType.REMOVE);

    public final TaskLevel taskLevel;
    public final ModificationType modificationType;

    private InlineTaskIndexTaskType(TaskLevel taskLevel, ModificationType modificationType) {
        this.taskLevel = taskLevel;
        this.modificationType = modificationType;
    }
}

