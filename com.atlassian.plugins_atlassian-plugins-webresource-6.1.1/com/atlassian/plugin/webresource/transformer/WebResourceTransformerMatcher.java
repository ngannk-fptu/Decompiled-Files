/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;

public interface WebResourceTransformerMatcher {
    public boolean matches(String var1);

    public boolean matches(ResourceLocation var1);
}

