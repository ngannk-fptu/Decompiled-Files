/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskListItem;
import com.atlassian.confluence.plugins.tasklist.Task;
import java.util.Map;

public interface InlineTaskFinder {
    public Map<Long, InlineTaskListItem> findTasksInContent(long var1, String var3, ConversionContext var4);

    public Task parseTask(InlineTaskListItem var1, long var2, ConversionContext var4);

    public Map<Long, Task> extractTasks(long var1, String var3, ConversionContext var4);
}

