/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexSchema;
import com.atlassian.confluence.plugins.edgeindex.model.Edge;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.ImmutableSet;
import java.util.Date;
import java.util.Set;

public interface EdgeFactory {
    public static final Set<String> REQUIRED_FIELDS = ImmutableSet.of((Object)EdgeIndexSchema.EDGE_TARGET_ID, (Object)"edge.targetType", (Object)"edge.date", (Object)"edge.type", (Object)EdgeIndexSchema.EDGE_USERKEY);

    public Edge getCreateEdge(ContentEntityObject var1);

    public Edge getLikeEdge(ConfluenceUser var1, ContentEntityObject var2, Date var3);

    public boolean canBuildCreatEdge(ContentEntityObject var1);
}

