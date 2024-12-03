/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityObjectId;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetId;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ContentEntityEdgeCountQuery
implements EdgeCountQuery {
    public static final Function<EdgeTargetId, Long> EDGE_TARGET_ID_TO_ID = input -> {
        if (!(input instanceof ContentEntityObjectId)) {
            throw new IllegalArgumentException("Only ContentEntityObjectId are supported.");
        }
        return ((ContentEntityObjectId)input).getId();
    };

    @Override
    public Map<EdgeTargetId, Integer> getEdgeCountForTargetIds(List<EdgeTargetId> targetIds) {
        List contentIds = Lists.transform(targetIds, EDGE_TARGET_ID_TO_ID);
        Map<Long, Integer> edgeCountForContentIds = this.getEdgeCountForContentIds(contentIds);
        HashMap<EdgeTargetId, Integer> countForTargetIds = new HashMap<EdgeTargetId, Integer>(edgeCountForContentIds.size());
        for (Map.Entry<Long, Integer> kvp : edgeCountForContentIds.entrySet()) {
            countForTargetIds.put(new ContentEntityObjectId(kvp.getKey()), kvp.getValue());
        }
        return countForTargetIds;
    }

    protected abstract Map<Long, Integer> getEdgeCountForContentIds(List<Long> var1);
}

