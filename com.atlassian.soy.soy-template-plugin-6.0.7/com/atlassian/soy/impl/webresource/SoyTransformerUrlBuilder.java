/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.soy.impl.functions.LocaleUtils
 *  com.atlassian.soy.spi.web.WebContextProvider
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 */
package com.atlassian.soy.impl.webresource;

import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.soy.impl.functions.LocaleUtils;
import com.atlassian.soy.impl.functions.UrlEncodingSoyFunctionSupplier;
import com.atlassian.soy.spi.web.WebContextProvider;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import java.util.Locale;

public class SoyTransformerUrlBuilder
implements DimensionAwareTransformerUrlBuilder {
    private final UrlEncodingSoyFunctionSupplier soyFunctionSupplier;
    private final WebContextProvider webContextProvider;

    public SoyTransformerUrlBuilder(UrlEncodingSoyFunctionSupplier soyFunctionSupplier, WebContextProvider webContextProvider) {
        this.soyFunctionSupplier = soyFunctionSupplier;
        this.webContextProvider = webContextProvider;
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        this.soyFunctionSupplier.addToUrl(urlBuilder);
        urlBuilder.addToQueryString("locale", LocaleUtils.serialize((Locale)this.webContextProvider.getLocale()));
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        String locale = coordinate.get("locale");
        this.soyFunctionSupplier.addToUrl(urlBuilder, coordinate);
        urlBuilder.addToQueryString("locale", locale);
    }
}

