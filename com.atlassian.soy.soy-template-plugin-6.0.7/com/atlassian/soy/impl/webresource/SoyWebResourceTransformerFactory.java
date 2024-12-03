/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.soy.impl.functions.LocaleUtils
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.soy.impl.webresource;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.soy.impl.functions.LocaleUtils;
import com.atlassian.soy.impl.functions.UrlEncodingSoyFunctionSupplier;
import com.atlassian.soy.impl.webresource.SoyTransformerUrlBuilder;
import com.atlassian.soy.impl.webresource.SoyWebResourceTransformer;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SoyWebResourceTransformerFactory
implements DimensionAwareWebResourceTransformerFactory {
    private final SoyTransformerUrlBuilder soyTransformerUrlBuilder;
    private final SoyWebResourceTransformer soyWebResourceTransformer;
    private final WebResourceIntegration webResourceIntegration;
    private final UrlEncodingSoyFunctionSupplier soyFunctionSupplier;

    public SoyWebResourceTransformerFactory(SoyTransformerUrlBuilder soyTransformerUrlBuilder, SoyWebResourceTransformer soyWebResourceTransformer, WebResourceIntegration webResourceIntegration, UrlEncodingSoyFunctionSupplier soyFunctionSupplier) {
        this.soyTransformerUrlBuilder = soyTransformerUrlBuilder;
        this.soyWebResourceTransformer = soyWebResourceTransformer;
        this.webResourceIntegration = webResourceIntegration;
        this.soyFunctionSupplier = soyFunctionSupplier;
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters ignored) {
        return this.soyWebResourceTransformer;
    }

    public Dimensions computeDimensions() {
        List locales = StreamSupport.stream(this.webResourceIntegration.getSupportedLocales().spliterator(), false).map(LocaleUtils::serialize).collect(Collectors.toList());
        return Dimensions.empty().andExactly("locale", locales).product(this.soyFunctionSupplier.computeDimensions());
    }

    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters ignored) {
        return this.soyTransformerUrlBuilder;
    }
}

