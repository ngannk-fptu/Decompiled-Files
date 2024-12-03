/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.fugue.Effect
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.IndexTaskType;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.fugue.Effect;

public interface EdgeIndexTaskQueue {
    public void enqueue(IndexTaskType var1, Edge var2);

    public void enqueue(IndexTaskType var1, ContentEntityObject var2);

    public long getSize();

    public void processEntries(Effect<EdgeIndexTask> var1);
}

