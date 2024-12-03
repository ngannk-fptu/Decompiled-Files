/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.Set;

public interface StaticTransformers
extends TwoPhaseResourceTransformer {
    public static final String[] PARAMETERS_USED = new String[]{"relative-url"};

    public Dimensions computeDimensions();

    public Dimensions computeBundleDimensions(Bundle var1);

    public void addToUrl(String var1, TransformerParameters var2, UrlBuilder var3, UrlBuildingStrategy var4);

    public Content transform(Content var1, TransformerParameters var2, ResourceLocation var3, QueryParams var4, String var5);

    public Set<String> getParamKeys();
}

