/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.navigation.rest;

import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.rest.LanguageParameter;
import com.atlassian.plugins.navlink.producer.navigation.rest.MenuItemKey;
import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinkService;
import com.atlassian.plugins.navlink.util.CacheControlFactory;
import com.atlassian.plugins.navlink.util.JsonStringEncoder;
import com.atlassian.plugins.navlink.util.LastModifiedFormatter;
import com.atlassian.plugins.navlink.util.url.BaseUrl;
import com.atlassian.plugins.navlink.util.url.SelfUrl;
import com.atlassian.plugins.navlink.util.url.UrlFactory;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationServlet
extends HttpServlet {
    static final String INIT_PARAM_CAPABILITIES_REST_ENDPOINT = "capabilitiesRestEndpoint";
    private static final String RESPONSE_TEMPLATE = "templates/navigation.vm";
    private final Logger logger = LoggerFactory.getLogger(NavigationServlet.class);
    private final LocalNavigationLinkService localNavigationLinkService;
    private final TemplateRenderer templateRenderer;
    private final UrlFactory urlFactory;
    private final LocaleResolver localeResolver;
    private String capabilitiesRestEndpoint;

    public NavigationServlet(LocalNavigationLinkService localNavigationLinkService, TemplateRenderer templateRenderer, UrlFactory urlFactory, LocaleResolver localeResolver) {
        this.localNavigationLinkService = localNavigationLinkService;
        this.templateRenderer = templateRenderer;
        this.urlFactory = urlFactory;
        this.localeResolver = localeResolver;
    }

    public void init() throws ServletException {
        this.capabilitiesRestEndpoint = this.getInitParameter(INIT_PARAM_CAPABILITIES_REST_ENDPOINT);
        if (Strings.isNullOrEmpty((String)this.capabilitiesRestEndpoint)) {
            throw new ServletException("init param not specified or empty: 'capabilitiesRestEndpoint'");
        }
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            httpServletResponse.setHeader("Cache-Control", CacheControlFactory.withConfiguredMaxAgeAndStaleContentExtension().toString());
            httpServletResponse.setHeader("Last-Modified", LastModifiedFormatter.formatCurrentTimeMillis());
            Map<String, Object> context = this.createContext(httpServletRequest);
            PrintWriter writer = httpServletResponse.getWriter();
            this.renderTemplate(context, writer);
        }
        catch (IOException e) {
            this.handleException(httpServletResponse, e);
        }
    }

    private void renderTemplate(@Nonnull Map<String, Object> context, @Nonnull Writer writer) throws IOException {
        this.templateRenderer.render(RESPONSE_TEMPLATE, context, writer);
    }

    private Map<String, Object> createContext(@Nonnull HttpServletRequest httpServletRequest) {
        BaseUrl baseUrl = this.urlFactory.getCanonicalBaseUrl();
        String selfUrl = SelfUrl.extractFrom(httpServletRequest);
        Locale locale = this.getLocaleFromRequest(httpServletRequest);
        Set<NavigationLink> navigationLinks = this.localNavigationLinkService.all(locale);
        Map<MenuItemKey, List<NavigationLink>> navigationLinksGroupedByMenuItemKey = this.groupNavigationLinksByKey(navigationLinks);
        return ImmutableMap.builder().put((Object)"baseUrl", (Object)baseUrl).put((Object)"selfUrl", (Object)selfUrl).put((Object)"collectionUrl", (Object)this.capabilitiesRestEndpoint).put((Object)"languageTag", (Object)LanguageParameter.encodeValue(locale)).put((Object)"navigationLinks", navigationLinksGroupedByMenuItemKey).put((Object)"json", (Object)new JsonStringEncoder()).build();
    }

    @Nonnull
    private Locale getLocaleFromRequest(@Nonnull HttpServletRequest httpServletRequest) {
        Locale resolvedLocale;
        Locale defaultLocale = Locale.getDefault();
        Locale requestedLocale = LanguageParameter.extractFrom(httpServletRequest, defaultLocale);
        Locale locale = resolvedLocale = this.localeResolver.getSupportedLocales().contains(requestedLocale) ? requestedLocale : defaultLocale;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Supported locales in this product: {}", (Object)this.localeResolver.getSupportedLocales());
            this.logger.debug("Default locale: {}, Requested locale: {}, Resolved locale: {}", new Object[]{defaultLocale, requestedLocale, resolvedLocale});
        }
        return resolvedLocale;
    }

    @Nonnull
    private Map<MenuItemKey, List<NavigationLink>> groupNavigationLinksByKey(@Nonnull Set<NavigationLink> navigationLinks) {
        HashMap links = new HashMap();
        for (NavigationLink navigationLink : navigationLinks) {
            this.getOrCreateList(links, new MenuItemKey(navigationLink.getKey())).add(navigationLink);
        }
        return links;
    }

    @Nonnull
    private <T> List<T> getOrCreateList(@Nonnull Map<MenuItemKey, List<T>> map, @Nonnull MenuItemKey mapKey) {
        List<T> navigationLinkEntityList = map.get(mapKey);
        if (navigationLinkEntityList == null) {
            navigationLinkEntityList = new LinkedList<T>();
            map.put(mapKey, navigationLinkEntityList);
        }
        return navigationLinkEntityList;
    }

    private void handleException(@Nonnull HttpServletResponse httpServletResponse, @Nonnull Exception e) {
        this.logger.warn("Failed to serialize navigation items: {}", (Object)e.getMessage());
        this.logger.debug("Stacktrace:", (Throwable)e);
        httpServletResponse.setStatus(500);
    }
}

