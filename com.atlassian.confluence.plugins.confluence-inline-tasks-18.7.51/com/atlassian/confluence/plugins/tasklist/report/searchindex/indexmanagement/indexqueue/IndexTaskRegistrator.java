/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.indexmanagement.indexqueue;

public interface IndexTaskRegistrator {
    public void requestToReindexAllInlineTasks();

    public void requestToAddAllInlineTasks();

    public void requestToReindexAllInlineTasksOnPage(long var1);

    public void requestToRemoveAllTasksOnThePage(long var1);

    public void requestToReindexAllInlineTasksOnPageIncludingAllDescendants(long var1);

    public void requestToReindexInlineTask(long var1);

    public void requestToRemoveTask(long var1);

    public void requestToReindexTask(long var1);
}

