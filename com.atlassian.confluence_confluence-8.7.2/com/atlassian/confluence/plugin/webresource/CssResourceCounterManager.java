/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.webresource;

public interface CssResourceCounterManager {
    public int getGlobalCssResourceCounter();

    public int getSpaceCssResourceCounter(String var1);

    public void invalidateGlobalCssResourceCounter();

    public void invalidateSpaceCssResourceCounter(String var1);
}

