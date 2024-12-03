/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import java.util.List;

public interface PopularContentQueries {
    public List<ContentEntityObject> getMostPopular(int var1, EdgeQueryParameter var2);
}

