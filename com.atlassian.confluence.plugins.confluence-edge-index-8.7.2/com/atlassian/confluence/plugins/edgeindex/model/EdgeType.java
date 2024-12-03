/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeCountQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;
import java.util.Set;

public interface EdgeType {
    public String getKey();

    public float getScore();

    public DeletionMode getDeletionMode();

    public EdgeUiSupport getEdgeUiSupport();

    public EdgeTypePermissionDelegate getPermissionDelegate();

    public Map<EdgeCountQuery, Set<String>> getEdgeCountQueries(Set<String> var1);

    public static interface EdgeTypePermissionDelegate {
        public boolean canView(ConfluenceUser var1);
    }

    public static interface EdgeUiSupport {
        public int getWeight();

        public String getCssClass();

        public String getI18NKey();
    }

    public static enum DeletionMode {
        BY_ID,
        BY_TARGET_ID,
        BY_TARGET_ID_AND_USER;

    }
}

