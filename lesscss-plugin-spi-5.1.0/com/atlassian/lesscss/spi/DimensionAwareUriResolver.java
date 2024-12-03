/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.lesscss.spi;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.lesscss.spi.EncodeStateResult;
import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.net.URI;

@ExperimentalApi
public interface DimensionAwareUriResolver
extends UriResolver {
    public Dimensions computeDimensions();

    public EncodeStateResult encodeState(URI var1, Coordinate var2);
}

