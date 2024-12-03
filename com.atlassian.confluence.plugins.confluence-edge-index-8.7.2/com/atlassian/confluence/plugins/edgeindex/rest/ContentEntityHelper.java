/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.List;
import java.util.Map;

public interface ContentEntityHelper {
    public List<ContentEntityObject> getContentEntities(List<Long> var1);

    public Map<Long, Integer> getCommentCounts(List<Long> var1);

    public Map<Long, Integer> getNestedCommentCounts(List<Long> var1);
}

