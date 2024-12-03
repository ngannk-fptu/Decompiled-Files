/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.webresource.ResourceType
 *  com.atlassian.plugin.webresource.CssWebResource
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceFormatter
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.api.model.content.webresource.ResourceType;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceService;
import com.atlassian.confluence.plugin.webresource.CssResourceCounterManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.themes.DefaultTheme;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.WebResourceFormatter;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfluenceWebResourceService
implements ConfluenceWebResourceService {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfluenceWebResourceService.class);
    private static final WebResourceFormatter CSS_FORMATTER = new CssWebResource();
    private static final JavascriptWebResource JS_FORMATTER = new JavascriptWebResource();
    public static final Predicate<WebResource> JS_RESOURCE_PREDICATE = webResource -> webResource instanceof PluginJsResource;
    private final PageBuilderService pageBuilderService;
    private final CssResourceCounterManager cssResourceCounterManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final StylesheetManager stylesheetManager;
    private final ThemeManager themeManager;
    private final WebResourceIntegration webResourceIntegration;
    private final Supplier<DarkFeaturesManager> darkFeaturesManagerSupplier;
    private final ColourSchemeManager colourSchemeManager;

    public DefaultConfluenceWebResourceService(PageBuilderService pageBuilderService, CssResourceCounterManager cssResourceCounterManager, WebResourceUrlProvider webResourceUrlProvider, StylesheetManager stylesheetManager, ThemeManager themeManager, WebResourceIntegration webResourceIntegration, DarkFeaturesManager darkFeaturesManager, ColourSchemeManager colourSchemeManager) {
        this.pageBuilderService = pageBuilderService;
        this.cssResourceCounterManager = cssResourceCounterManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.stylesheetManager = stylesheetManager;
        this.themeManager = themeManager;
        this.webResourceIntegration = webResourceIntegration;
        this.darkFeaturesManagerSupplier = () -> darkFeaturesManager;
        this.colourSchemeManager = colourSchemeManager;
    }

    ColourSchemeManager getColourSchemeManager() {
        return this.colourSchemeManager;
    }

    WebResourceUrlProvider getWebResourceUrlProvider() {
        return this.webResourceUrlProvider;
    }

    ThemeManager getThemeManager() {
        return this.themeManager;
    }

    @Override
    public void writeConfluenceResourceTags(@NonNull Writer writer, @Nullable ConfluenceWebResourceService.Style style, @Nullable String spaceKey) {
        UrlMode urlMode = UrlMode.AUTO;
        WebResourceAssembler assembler = this.pageBuilderService.assembler();
        boolean enableDeferJSAttribute = this.webResourceIntegration.isDeferJsAttributeEnabled();
        WebResourceSet webResourceSet = assembler.assembled().drainIncludedResources();
        webResourceSet.writeHtmlTags(writer, urlMode, Predicates.not(JS_RESOURCE_PREDICATE));
        this.writeCombinedCssTags(writer, spaceKey, style);
        webResourceSet.writeHtmlTags(writer, urlMode, JS_RESOURCE_PREDICATE);
        this.writeThemeJsTags(writer, spaceKey, enableDeferJSAttribute);
    }

    @Override
    public Map<ResourceType, Iterable<String>> calculateConfluenceResourceUris(@Nullable ConfluenceWebResourceService.Style style, @Nullable String spaceKey) {
        List<String> css = this.computeConfluenceCssUris(spaceKey, style);
        return Map.of(ResourceType.CSS, css, ResourceType.JS, Collections.emptyList(), ResourceType.DATA, Collections.emptyList());
    }

    @VisibleForTesting
    void writeAdminCssTags(Appendable writer, String spaceKey) {
        this.writeCombinedCssTags(writer, spaceKey, ConfluenceWebResourceService.Style.ADMIN);
    }

    @VisibleForTesting
    void writeCombinedCssTags(Appendable writer, String spaceKey, ConfluenceWebResourceService.Style style) {
        List<String> resources = this.computeConfluenceCssUris(spaceKey, style);
        for (String url : resources) {
            if (StringUtils.isBlank((CharSequence)url)) continue;
            this.writeCustomStylesheet(writer, url);
        }
    }

    @VisibleForTesting
    List<String> computeConfluenceCssUris(String spaceKey, ConfluenceWebResourceService.Style style) {
        LinkedList css = Lists.newLinkedList();
        String colorsCssUrl = this.getColorsCssUrl(spaceKey, style);
        List<String> themeCssUrls = this.getThemeCssUrls(spaceKey, style);
        String customCssUrl = this.getCustomCssUrl(spaceKey, style);
        if (colorsCssUrl != null) {
            css.add(colorsCssUrl);
        }
        if (themeCssUrls != null) {
            css.addAll(themeCssUrls);
        }
        if (customCssUrl != null) {
            css.add(customCssUrl);
        }
        return css;
    }

    private String getColorsCssUrl(@Nullable String spaceKey, @Nullable ConfluenceWebResourceService.Style style) {
        String prefix;
        if (this.isDefaultColorScheme(spaceKey)) {
            return null;
        }
        StringBuilder colorsUrl = new StringBuilder();
        boolean isColorsGlobal = StringUtils.isBlank((CharSequence)spaceKey) || this.isGlobalColorScheme(spaceKey) || style == ConfluenceWebResourceService.Style.ADMIN;
        String string = prefix = isColorsGlobal ? this.getGlobalCssResourcePrefix() : this.getSpaceCssPrefix(spaceKey);
        if (isColorsGlobal) {
            colorsUrl.append(prefix).append("/styles/colors.css");
        } else {
            colorsUrl.append(prefix).append("/styles/colors.css?spaceKey=").append(HtmlUtil.urlEncode(spaceKey));
        }
        return this.transformToCdnUrl(colorsUrl.toString());
    }

    private String getCustomCssUrl(@Nullable String spaceKey, @Nullable ConfluenceWebResourceService.Style style) {
        String prefix;
        String customStylesheet;
        if (style == ConfluenceWebResourceService.Style.ADMIN) {
            return null;
        }
        String globalStylesheet = this.stylesheetManager.getGlobalStylesheet();
        String string = customStylesheet = StringUtils.isBlank((CharSequence)spaceKey) ? globalStylesheet : this.stylesheetManager.getSpaceStylesheet(spaceKey);
        if (StringUtils.isBlank((CharSequence)customStylesheet)) {
            return null;
        }
        StringBuilder customUrl = new StringBuilder();
        boolean isStylesheetGlobal = StringUtils.isBlank((CharSequence)spaceKey) || customStylesheet.equals(globalStylesheet);
        String string2 = prefix = isStylesheetGlobal ? this.getGlobalCssResourcePrefix() : this.getSpaceCssPrefix(spaceKey);
        if (isStylesheetGlobal) {
            customUrl.append(prefix).append("/styles/custom.css");
        } else {
            customUrl.append(prefix).append("/styles/custom.css?spaceKey=").append(HtmlUtil.urlEncode(spaceKey));
        }
        return this.transformToCdnUrl(customUrl.toString());
    }

    private List<String> getThemeCssUrls(@Nullable String spaceKey, @Nullable ConfluenceWebResourceService.Style style) {
        if (style == ConfluenceWebResourceService.Style.ADMIN) {
            return null;
        }
        Theme theme = this.getActiveTheme(spaceKey);
        if (theme == null || theme.getClass() == DefaultTheme.class) {
            return null;
        }
        ArrayList<String> urls = new ArrayList<String>();
        String prefix = StringUtils.isBlank((CharSequence)spaceKey) ? this.getGlobalCssResourcePrefix() : this.getSpaceCssPrefix(spaceKey);
        for (ThemeResource themeResource : theme.getStylesheets()) {
            String location = themeResource.getLocation();
            String url = location.endsWith(".vm") ? this.buildVmStylesheetUrl(themeResource, prefix, spaceKey) : this.webResourceUrlProvider.getStaticPluginResourceUrl(themeResource.getCompleteModuleKey(), themeResource.getName(), com.atlassian.plugin.webresource.UrlMode.AUTO);
            urls.add(this.transformToCdnUrl(url));
        }
        return urls;
    }

    private void writeCustomStylesheet(Appendable writer, String url) {
        try {
            writer.append(CSS_FORMATTER.formatResource(url, Collections.emptyMap()));
        }
        catch (IOException e) {
            log.error("IOException encountered rendering custom css tag: " + url, (Throwable)e);
        }
    }

    @VisibleForTesting
    void writeThemeJsTags(Appendable writer, String spaceKey) {
        this.writeThemeJsTags(writer, spaceKey, false);
    }

    private void writeThemeJsTags(Appendable writer, String spaceKey, boolean enableJsDeferAttribute) {
        Theme theme = this.getActiveTheme(spaceKey);
        if (theme == null) {
            theme = DefaultTheme.getInstance();
        }
        Collection<? extends ThemeResource> resources = theme.getJavascript();
        try {
            for (ThemeResource themeResource : resources) {
                String url = this.transformToCdnUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(themeResource.getCompleteModuleKey(), themeResource.getName(), com.atlassian.plugin.webresource.UrlMode.AUTO));
                Map attributes = enableJsDeferAttribute ? Collections.singletonMap("defer", "") : Collections.emptyMap();
                String scriptTag = JS_FORMATTER.formatResource(url, attributes);
                writer.append(scriptTag);
            }
        }
        catch (IOException e) {
            log.error("IOException encountered rendering theme js tags", (Throwable)e);
        }
    }

    private Theme getActiveTheme(String spaceKey) {
        return StringUtils.isBlank((CharSequence)spaceKey) ? this.themeManager.getGlobalTheme() : this.themeManager.getSpaceTheme(spaceKey);
    }

    private String buildVmStylesheetUrl(ThemeResource stylesheet, String prefix, String spaceKey) {
        StringBuilder url = new StringBuilder();
        url.append(prefix);
        url.append("/styles/theme-colors.css?completeModuleKey=");
        url.append(HtmlUtil.urlEncode(stylesheet.getCompleteModuleKey()));
        url.append("&stylesheetName=");
        url.append(HtmlUtil.urlEncode(stylesheet.getName()));
        if (!StringUtils.isBlank((CharSequence)spaceKey)) {
            url.append("&spaceKey=");
            url.append(HtmlUtil.urlEncode(spaceKey));
        }
        return url.toString();
    }

    private String transformToCdnUrl(String relativeUrl) {
        CDNStrategy cdnStrategy = this.webResourceIntegration.getCDNStrategy();
        return cdnStrategy != null ? cdnStrategy.transformRelativeUrl(relativeUrl) : relativeUrl;
    }

    private boolean isGlobalColorScheme(String spaceKey) {
        return this.colourSchemeManager.getSpaceColourScheme(spaceKey).equals(this.colourSchemeManager.getGlobalColourScheme());
    }

    private boolean isDefaultColorScheme(String spaceKey) {
        ColourScheme colourScheme = StringUtils.isBlank((CharSequence)spaceKey) ? this.colourSchemeManager.getGlobalColourScheme() : this.colourSchemeManager.getSpaceColourScheme(spaceKey);
        return colourScheme.isDefaultColourScheme();
    }

    String getGlobalCssResourcePrefix() {
        return this.getGlobalCssResourcePrefix(com.atlassian.plugin.webresource.UrlMode.AUTO);
    }

    String getGlobalCssResourcePrefix(com.atlassian.plugin.webresource.UrlMode urlMode) {
        int globalCssResourceCounter = this.cssResourceCounterManager.getGlobalCssResourceCounter();
        return this.webResourceUrlProvider.getStaticResourcePrefix(String.valueOf(globalCssResourceCounter), urlMode);
    }

    String getSpaceCssPrefix(String spaceKey) {
        return this.getSpaceCssPrefix(spaceKey, com.atlassian.plugin.webresource.UrlMode.AUTO);
    }

    String getSpaceCssPrefix(String spaceKey, com.atlassian.plugin.webresource.UrlMode urlMode) {
        int spaceCssResourceCounter = this.cssResourceCounterManager.getSpaceCssResourceCounter(spaceKey);
        return this.webResourceUrlProvider.getStaticResourcePrefix(String.valueOf(spaceCssResourceCounter), urlMode);
    }
}

