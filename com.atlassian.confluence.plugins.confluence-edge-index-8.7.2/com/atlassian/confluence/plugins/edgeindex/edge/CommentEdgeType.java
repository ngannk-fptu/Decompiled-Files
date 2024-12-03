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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;

public class CommentEdgeType
extends AbstractEdgeType {
    public static final String KEY = "comment.create";
    private final Map<EdgeCountQuery, Set<String>> queries;

    public CommentEdgeType(@Qualifier(value="commentCountQuery") EdgeCountQuery commentCountQuery, @Qualifier(value="nestedCommentCountQuery") EdgeCountQuery nestedCommentCountQuery) {
        this.uiSupport = new DefaultEdgeUiSupport(110, "aui-icon aui-icon-small content-type-comment-count", "");
        this.queries = ImmutableMap.of((Object)commentCountQuery, (Object)ImmutableSet.of((Object)"page", (Object)"blogpost", (Object)"attachment"), (Object)nestedCommentCountQuery, (Object)ImmutableSet.of((Object)"comment"));
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Map<EdgeCountQuery, Set<String>> getEdgeCountQueries(Set<String> edgeTypes) {
        return Maps.filterValues(this.queries, input -> !Sets.intersection((Set)input, (Set)edgeTypes).isEmpty());
    }
}

