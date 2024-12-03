/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeEntity
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.fugue.Pair;
import java.util.Date;
import java.util.List;

public interface EdgeContentQueries {
    public List<Pair<ContentEntityObject, LikeEntity>> getLikesSince(Date var1);

    public List<ContentEntityObject> getContentCreatedSince(Date var1);
}

