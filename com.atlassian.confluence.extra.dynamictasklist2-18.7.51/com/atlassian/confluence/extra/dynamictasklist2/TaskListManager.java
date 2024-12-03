/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;

public interface TaskListManager {
    public TaskList getTaskList(ContentEntityObject var1, String var2);

    public TaskList getTaskList(ContentEntityObject var1, TaskListId var2);

    public TaskList getTaskListWithNameFromContent(ContentEntityObject var1, String var2, int var3);

    public void saveTaskList(ContentEntityObject var1, TaskList var2, String var3);
}

