/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.user.ConfluenceUser;

public class DefaultEdgeTypePermissionDelegate
implements EdgeType.EdgeTypePermissionDelegate {
    @Override
    public boolean canView(ConfluenceUser user) {
        return true;
    }
}

