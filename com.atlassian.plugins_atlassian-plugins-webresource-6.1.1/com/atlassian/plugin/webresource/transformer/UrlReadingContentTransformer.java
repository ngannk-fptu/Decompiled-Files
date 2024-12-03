/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.webresource.QueryParams
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.impl.support.Content;

public interface UrlReadingContentTransformer {
    public Content transform(CdnResourceUrlTransformer var1, Content var2, ResourceLocation var3, QueryParams var4, String var5);
}

