/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;

public interface DimensionAwareTransformerUrlBuilder
extends TransformerUrlBuilder {
    public void addToUrl(UrlBuilder var1, Coordinate var2);
}

