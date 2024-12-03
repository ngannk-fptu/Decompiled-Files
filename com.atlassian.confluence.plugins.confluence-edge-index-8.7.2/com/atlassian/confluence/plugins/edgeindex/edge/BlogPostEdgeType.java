/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.edge.AbstractEdgeType;

public class BlogPostEdgeType
extends AbstractEdgeType {
    public static final String KEY = "blogpost.create";

    @Override
    public String getKey() {
        return KEY;
    }
}

