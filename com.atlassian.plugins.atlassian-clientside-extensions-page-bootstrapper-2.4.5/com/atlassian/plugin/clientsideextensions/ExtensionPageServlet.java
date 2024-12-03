/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletResponse
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.clientsideextensions;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class ExtensionPageServlet
extends HttpServlet {
    @VisibleForTesting
    static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String EXTENSION_KEY_PARAM_NAME = "extension-key";
    public static final String EXTENSION_POINT_PARAM_NAME = "extension-point";
    public static final String PAGE_DATA_PROVIDER_KEY_PARAM_NAME = "page-data-provider-key";
    public static final String PAGE_DECORATOR_PARAM_NAME = "page-decorator";
    public static final String PAGE_TITLE_PARAM_NAME = "page-title";
    public static final String PAGE_TITLE_KEY_PARAM_NAME = "page-title-key";
    public static final String WEB_RESOURCE_KEYS_PARAM_NAME = "web-resources";
    public static final String WEB_RESOURCE_KEYS_SEPARATOR = ",";
    @VisibleForTesting
    static final String DEFAULT_PAGE_DECORATOR = "atl.general";
    @VisibleForTesting
    static final String DEFAULT_PAGE_TITLE = "";
    @VisibleForTesting
    static final String RESOURCE_KEY = "com.atlassian.plugins.atlassian-clientside-extensions-page-bootstrapper:server-soy-templates";
    @VisibleForTesting
    static final String TEMPLATE_KEY = "CSE.Templates.page";
    @VisibleForTesting
    static final String MODEL_KEY_EXTENSION_POINT = "extensionPoint";
    @VisibleForTesting
    static final String MODEL_KEY_EXTENSION_KEY = "extensionKey";
    @VisibleForTesting
    static final String MODEL_KEY_PAGE_DATA_PROVIDER_KEY = "pageDataProviderKey";
    @VisibleForTesting
    static final String MODEL_KEY_PAGE_DECORATOR = "pageDecorator";
    @VisibleForTesting
    static final String MODEL_KEY_PAGE_TITLE = "pageTitle";
    @VisibleForTesting
    static final String MODEL_KEY_PATH = "path";
    @VisibleForTesting
    static final String MODEL_KEY_WEB_RESOURCE_KEYS = "webResourceKeys";
    private final I18nResolver i18nResolver;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private String extensionPoint;
    private String extensionKey;
    private String pageDecorator;
    private String pageTitle;
    private String pageDataProviderKey;
    private String pageTitleKey;
    private String webResourceKeys;

    public ExtensionPageServlet(I18nResolver i18nResolver, SoyTemplateRenderer soyTemplateRenderer) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.soyTemplateRenderer = Objects.requireNonNull(soyTemplateRenderer);
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.extensionPoint = ExtensionPageServlet.getRequiredInitParam(EXTENSION_POINT_PARAM_NAME, config);
        this.extensionKey = ExtensionPageServlet.getRequiredInitParam(EXTENSION_KEY_PARAM_NAME, config);
        this.webResourceKeys = ExtensionPageServlet.getRequiredInitParam(WEB_RESOURCE_KEYS_PARAM_NAME, config);
        this.pageDataProviderKey = ExtensionPageServlet.getOptionalInitParam(PAGE_DATA_PROVIDER_KEY_PARAM_NAME, config, null);
        this.pageDecorator = ExtensionPageServlet.getOptionalInitParam(PAGE_DECORATOR_PARAM_NAME, config, DEFAULT_PAGE_DECORATOR);
        this.pageTitle = ExtensionPageServlet.getOptionalInitParam(PAGE_TITLE_PARAM_NAME, config, DEFAULT_PAGE_TITLE);
        this.pageTitleKey = ExtensionPageServlet.getOptionalInitParam(PAGE_TITLE_KEY_PARAM_NAME, config, null);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ImmutableMap.Builder params = ImmutableMap.builder();
        String path = req.getPathInfo();
        if (path != null) {
            params.put((Object)MODEL_KEY_PATH, (Object)path);
        }
        params.put((Object)MODEL_KEY_PAGE_TITLE, (Object)this.getPageTitle()).put((Object)MODEL_KEY_PAGE_DECORATOR, (Object)this.pageDecorator).put((Object)MODEL_KEY_EXTENSION_POINT, (Object)this.extensionPoint).put((Object)MODEL_KEY_EXTENSION_KEY, (Object)this.extensionKey).put((Object)MODEL_KEY_WEB_RESOURCE_KEYS, this.getWebResourceKeys());
        if (this.pageDataProviderKey != null) {
            params.put((Object)MODEL_KEY_PAGE_DATA_PROVIDER_KEY, (Object)this.pageDataProviderKey);
        }
        this.render((ServletResponse)resp, (Map<String, Object>)params.build());
    }

    private String getPageTitle() {
        return Optional.ofNullable(this.pageTitleKey).map(arg_0 -> ((I18nResolver)this.i18nResolver).getText(arg_0)).filter(Objects::nonNull).filter(text -> !text.equals(this.pageTitleKey)).orElse(this.pageTitle);
    }

    private List<String> getWebResourceKeys() {
        return Arrays.stream(this.webResourceKeys.split(WEB_RESOURCE_KEYS_SEPARATOR)).map(String::trim).collect(Collectors.toList());
    }

    private void render(ServletResponse resp, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType(CONTENT_TYPE);
        try {
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, data);
        }
        catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new ServletException((Throwable)e);
        }
    }

    private static String getRequiredInitParam(String key, ServletConfig config) throws UnavailableException {
        String paramValue = config.getInitParameter(key);
        if (StringUtils.isBlank((CharSequence)paramValue)) {
            throw new UnavailableException("The required '" + key + "' init-param is missing or undefined");
        }
        return paramValue;
    }

    private static String getOptionalInitParam(String key, ServletConfig config, String defaultValue) {
        return (String)StringUtils.defaultIfEmpty((CharSequence)config.getInitParameter(key), (CharSequence)defaultValue);
    }
}

