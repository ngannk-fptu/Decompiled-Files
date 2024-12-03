/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.Dimensions;

public interface DimensionAwareUrlReadingCondition
extends UrlReadingCondition {
    public Dimensions computeDimensions();

    public void addToUrl(UrlBuilder var1, Coordinate var2);
}

