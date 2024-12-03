/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import java.util.List;

public interface EdgeQueries {
    public List<EdgeTargetInfo> getMostPopular(EdgeQueryParameter var1);
}

