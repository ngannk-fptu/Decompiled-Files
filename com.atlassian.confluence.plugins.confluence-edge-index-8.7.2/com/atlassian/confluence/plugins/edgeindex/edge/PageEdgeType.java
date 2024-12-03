/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.edge;

import com.atlassian.confluence.plugins.edgeindex.edge.AbstractEdgeType;

public class PageEdgeType
extends AbstractEdgeType {
    public static final String KEY = "page.create";

    @Override
    public String getKey() {
        return KEY;
    }
}

