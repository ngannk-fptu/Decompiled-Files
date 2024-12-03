/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;

public interface StaticTransformersSupplier {
    public Dimensions computeDimensions();

    public Iterable<DimensionAwareWebResourceTransformerFactory> get(String var1);

    public Iterable<DimensionAwareWebResourceTransformerFactory> get(ResourceLocation var1);
}

