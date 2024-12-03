/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.edge.AbstractEdgeType;
import com.atlassian.confluence.plugins.edgeindex.edge.DefaultEdgeUiSupport;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;

public class LikeEdgeType
extends AbstractEdgeType {
    public static final String KEY = "like.create";
    private final Map<EdgeCountQuery, Set<String>> queries;

    public LikeEdgeType(@Qualifier(value="likeCountQuery") EdgeCountQuery likeCountQuery) {
        this.uiSupport = new DefaultEdgeUiSupport(100, "aui-icon aui-icon-small content-type-like", "");
        this.queries = ImmutableMap.of((Object)likeCountQuery, (Object)ImmutableSet.of((Object)"page", (Object)"blogpost", (Object)"comment"));
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public EdgeType.DeletionMode getDeletionMode() {
        return EdgeType.DeletionMode.BY_TARGET_ID_AND_USER;
    }

    @Override
    public Map<EdgeCountQuery, Set<String>> getEdgeCountQueries(Set<String> edgeTypes) {
        return Maps.filterValues(this.queries, input -> !Sets.intersection((Set)input, (Set)edgeTypes).isEmpty());
    }
}

