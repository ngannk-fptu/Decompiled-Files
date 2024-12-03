/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;

public interface DimensionAwareWebResourceTransformerFactory
extends WebResourceTransformerFactory {
    public Dimensions computeDimensions();

    @Override
    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters var1);
}

