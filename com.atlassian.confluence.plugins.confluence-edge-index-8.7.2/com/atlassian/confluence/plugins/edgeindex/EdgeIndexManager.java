/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;

public interface EdgeIndexManager {
    public void index(Edge var1);

    public void unIndex(Edge var1);

    public void reIndexPermissions(Object var1);

    public void contentEntityRemoved(ContentEntityObject var1);

    public void contentEntityVersionRemoved(ContentEntityObject var1);
}

