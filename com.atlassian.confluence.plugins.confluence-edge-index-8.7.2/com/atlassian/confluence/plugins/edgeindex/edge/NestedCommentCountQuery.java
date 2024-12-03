/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityEdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.rest.ContentEntityHelper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NestedCommentCountQuery
extends ContentEntityEdgeCountQuery {
    private final ContentEntityHelper contentEntityHelper;

    @Autowired
    public NestedCommentCountQuery(ContentEntityHelper contentEntityHelper) {
        this.contentEntityHelper = contentEntityHelper;
    }

    @Override
    protected Map<Long, Integer> getEdgeCountForContentIds(List<Long> contentIds) {
        return this.contentEntityHelper.getNestedCommentCounts(contentIds);
    }
}

