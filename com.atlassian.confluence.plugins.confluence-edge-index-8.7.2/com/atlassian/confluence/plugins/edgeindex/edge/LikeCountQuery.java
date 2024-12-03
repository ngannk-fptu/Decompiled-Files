/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityEdgeCountQuery;
import com.atlassian.confluence.plugins.edgeindex.rest.ContentEntityHelper;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeCountQuery
extends ContentEntityEdgeCountQuery {
    private final LikeManager likeManager;
    private final ContentEntityHelper contentEntityHelper;

    @Autowired
    public LikeCountQuery(LikeManager likeManager, ContentEntityHelper contentEntityHelper) {
        this.likeManager = likeManager;
        this.contentEntityHelper = contentEntityHelper;
    }

    @Override
    protected Map<Long, Integer> getEdgeCountForContentIds(List<Long> contentIds) {
        List<ContentEntityObject> contentEntities = this.contentEntityHelper.getContentEntities(contentIds);
        Map likes = this.likeManager.getLikes(contentEntities);
        return Maps.transformValues((Map)likes, List::size);
    }
}

