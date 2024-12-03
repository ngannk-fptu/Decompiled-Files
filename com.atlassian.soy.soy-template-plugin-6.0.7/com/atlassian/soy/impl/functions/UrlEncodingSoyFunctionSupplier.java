/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.spi.functions.SoyFunctionSupplier
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;

public interface UrlEncodingSoyFunctionSupplier
extends SoyFunctionSupplier,
DimensionAwareTransformerUrlBuilder {
    public Dimensions computeDimensions();
}

