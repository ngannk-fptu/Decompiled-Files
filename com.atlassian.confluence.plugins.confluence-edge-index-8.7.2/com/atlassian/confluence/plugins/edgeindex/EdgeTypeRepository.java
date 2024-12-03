/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.fugue.Option;
import java.util.Collection;

public interface EdgeTypeRepository {
    public Option<EdgeType> getEdgeIndexTypeByKey(String var1);

    public Collection<EdgeType> getEdgeIndexTypes();
}

