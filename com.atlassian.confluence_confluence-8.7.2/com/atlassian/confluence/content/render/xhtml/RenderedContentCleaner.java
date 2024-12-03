/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

public interface RenderedContentCleaner {
    public String cleanQuietly(String var1);

    public String cleanStyleAttribute(String var1);

    public boolean isCleanUrlAttribute(String var1);
}

