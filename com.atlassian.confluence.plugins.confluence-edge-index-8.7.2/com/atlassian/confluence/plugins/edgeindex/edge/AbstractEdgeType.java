/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.edge.DefaultEdgeTypePermissionDelegate;
import com.atlassian.confluence.plugins.edgeindex.edge.DefaultEdgeUiSupport;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEdgeType
implements EdgeType {
    protected EdgeType.EdgeTypePermissionDelegate edgeTypePermissionDelegate = new DefaultEdgeTypePermissionDelegate();
    protected EdgeType.EdgeUiSupport uiSupport = new DefaultEdgeUiSupport();

    protected AbstractEdgeType() {
    }

    @Override
    public float getScore() {
        return 1.0f;
    }

    @Override
    public EdgeType.DeletionMode getDeletionMode() {
        return EdgeType.DeletionMode.BY_ID;
    }

    @Override
    public EdgeType.EdgeTypePermissionDelegate getPermissionDelegate() {
        return this.edgeTypePermissionDelegate;
    }

    @Override
    public EdgeType.EdgeUiSupport getEdgeUiSupport() {
        return this.uiSupport;
    }

    @Override
    public Map<EdgeCountQuery, Set<String>> getEdgeCountQueries(Set<String> edgeTypes) {
        return Collections.emptyMap();
    }
}

