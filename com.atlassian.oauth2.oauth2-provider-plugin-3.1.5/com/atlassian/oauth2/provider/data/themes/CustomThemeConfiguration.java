/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.data.themes;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.oauth2.provider.data.themes.ProductCustomTheme;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public class CustomThemeConfiguration
implements WebResourceDataProvider {
    private final ProductCustomThemeFactory themeFactory;

    public CustomThemeConfiguration(ProductCustomThemeFactory factory) {
        this.themeFactory = factory;
    }

    public Jsonable get() {
        Gson gson = new Gson();
        ProductCustomTheme customTheme = this.getTheme();
        return writer -> gson.toJson((Object)ImmutableMap.of((Object)"headerColor", (Object)(StringUtils.isNotBlank((CharSequence)customTheme.getHeaderColor()) ? customTheme.getHeaderColor() : "#0747a6"), (Object)"logoUrl", (Object)customTheme.getLogoUrl(), (Object)"useBrandLogoLibrary", (Object)StringUtils.isBlank((CharSequence)customTheme.getLogoUrl())), (Appendable)writer);
    }

    public ProductCustomTheme getTheme() {
        return this.themeFactory.get();
    }
}

