/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.context.i18n.LocaleContext
 *  org.springframework.context.i18n.TimeZoneAwareLocaleContext
 *  org.springframework.lang.Nullable
 *  org.springframework.ui.context.Theme
 *  org.springframework.ui.context.ThemeSource
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.context.ContextLoader
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.WebApplicationContextUtils
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class RequestContextUtils {
    public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

    @Nullable
    public static WebApplicationContext findWebApplicationContext(HttpServletRequest request, @Nullable ServletContext servletContext) {
        WebApplicationContext webApplicationContext = (WebApplicationContext)request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            if (servletContext != null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext((ServletContext)servletContext);
            }
            if (webApplicationContext == null) {
                webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            }
        }
        return webApplicationContext;
    }

    @Nullable
    public static WebApplicationContext findWebApplicationContext(HttpServletRequest request) {
        return RequestContextUtils.findWebApplicationContext(request, request.getServletContext());
    }

    @Nullable
    public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (LocaleResolver)request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
    }

    public static Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        return localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale();
    }

    @Nullable
    public static TimeZone getTimeZone(HttpServletRequest request) {
        LocaleContext localeContext;
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver instanceof LocaleContextResolver && (localeContext = ((LocaleContextResolver)localeResolver).resolveLocaleContext(request)) instanceof TimeZoneAwareLocaleContext) {
            return ((TimeZoneAwareLocaleContext)localeContext).getTimeZone();
        }
        return null;
    }

    @Nullable
    public static ThemeResolver getThemeResolver(HttpServletRequest request) {
        return (ThemeResolver)request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE);
    }

    @Nullable
    public static ThemeSource getThemeSource(HttpServletRequest request) {
        return (ThemeSource)request.getAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE);
    }

    @Nullable
    public static Theme getTheme(HttpServletRequest request) {
        ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
        ThemeSource themeSource = RequestContextUtils.getThemeSource(request);
        if (themeResolver != null && themeSource != null) {
            String themeName = themeResolver.resolveThemeName(request);
            return themeSource.getTheme(themeName);
        }
        return null;
    }

    @Nullable
    public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
        return (Map)request.getAttribute(DispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE);
    }

    public static FlashMap getOutputFlashMap(HttpServletRequest request) {
        return (FlashMap)request.getAttribute(DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE);
    }

    @Nullable
    public static FlashMapManager getFlashMapManager(HttpServletRequest request) {
        return (FlashMapManager)request.getAttribute(DispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE);
    }

    public static void saveOutputFlashMap(String location, HttpServletRequest request, HttpServletResponse response) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (CollectionUtils.isEmpty((Map)flashMap)) {
            return;
        }
        UriComponents uriComponents = UriComponentsBuilder.fromUriString((String)location).build();
        flashMap.setTargetRequestPath(uriComponents.getPath());
        flashMap.addTargetRequestParams((MultiValueMap<String, String>)uriComponents.getQueryParams());
        FlashMapManager manager = RequestContextUtils.getFlashMapManager(request);
        Assert.state((manager != null ? 1 : 0) != 0, (String)"No FlashMapManager. Is this a DispatcherServlet handled request?");
        manager.saveOutputFlashMap(flashMap, request, response);
    }
}

