/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;

public interface TaskRenderService {
    public Iterable<TaskModfication> renderTasksOnPage(Iterable<TaskModfication> var1, Content var2);
}

