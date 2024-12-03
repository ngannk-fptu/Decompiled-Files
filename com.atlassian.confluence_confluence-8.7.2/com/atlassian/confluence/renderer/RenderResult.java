/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import java.util.Map;

public class RenderResult {
    private final String renderedContent;
    private final Map metadata;

    public RenderResult(String renderedContent, Map metadata) {
        this.renderedContent = renderedContent;
        this.metadata = metadata;
    }

    public boolean containsMetadata(String key) {
        return this.metadata.containsKey(key);
    }

    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }

    public String getRenderedContent() {
        return this.renderedContent;
    }

    public String toString() {
        return this.renderedContent;
    }
}

