/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetId;
import java.util.List;
import java.util.Map;

public interface EdgeCountQuery {
    public Map<EdgeTargetId, Integer> getEdgeCountForTargetIds(List<EdgeTargetId> var1);
}

