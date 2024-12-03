/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.SetupLocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupConfluenceWebResourceIntegration
implements WebResourceIntegration {
    private static final Random random = new Random();
    private final String systemCounter = "s" + Integer.toString(random.nextInt());
    private static final Logger log = LoggerFactory.getLogger(SetupConfluenceWebResourceIntegration.class);
    private final PluginAccessor pluginAccessor;
    private final PluginDirectoryProvider pluginDirectoryProvider;
    private final SetupLocaleManager localeManager;
    private final DefaultI18NBeanFactory i18NBeanFactory;
    private final PluginEventManager pluginEventManager;
    private final DarkFeatureManager darkFeatureManager;
    private final EventPublisher eventPublisher;
    private final HttpContext httpContext;

    public SetupConfluenceWebResourceIntegration(PluginAccessor pluginAccessor, PluginDirectoryProvider pluginDirectoryProvider, SetupLocaleManager localeManager, DefaultI18NBeanFactory i18NBeanFactory, PluginEventManager pluginEventManager, DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher, HttpContext httpContext) {
        this.pluginAccessor = pluginAccessor;
        this.pluginDirectoryProvider = pluginDirectoryProvider;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.pluginEventManager = pluginEventManager;
        this.darkFeatureManager = darkFeatureManager;
        this.eventPublisher = eventPublisher;
        this.httpContext = httpContext;
    }

    public String getStaticResourceLocale() {
        return this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()).toString();
    }

    public void rebuildResourceUrlPrefix() {
    }

    public String getI18nStateHash() {
        return this.i18NBeanFactory.getStateHash();
    }

    public PluginAccessor getPluginAccessor() {
        return this.pluginAccessor;
    }

    public PluginEventManager getPluginEventManager() {
        return this.pluginEventManager;
    }

    @Nonnull
    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @Nonnull
    public DarkFeatureManager getDarkFeatureManager() {
        return this.darkFeatureManager;
    }

    public Map<String, Object> getRequestCache() {
        return RequestCacheThreadLocal.getRequestCache();
    }

    public String getSystemCounter() {
        return this.systemCounter;
    }

    public String getSystemBuildNumber() {
        return GeneralUtil.getBuildNumber();
    }

    public String getHostApplicationVersion() {
        return BuildInformation.INSTANCE.getVersionNumber();
    }

    public String getBaseUrl() {
        return this.getBaseUrl(UrlMode.AUTO);
    }

    public String getBaseUrl(UrlMode urlMode) {
        switch (urlMode) {
            case RELATIVE: 
            case AUTO: {
                return this.getRelativeBaseUrl();
            }
        }
        throw new AssertionError((Object)("Unsupported URLMode: " + urlMode));
    }

    public File getTemporaryDirectory() {
        return this.pluginDirectoryProvider.getWebResourceIntegrationTempDirectory();
    }

    private String getRelativeBaseUrl() {
        try {
            return this.httpContext.getRequest().getContextPath();
        }
        catch (Exception e) {
            log.debug("httpContext.getRequest().getContextPath() threw Exception. Ignoring.");
            if (RequestCacheThreadLocal.getContextPath() != null) {
                return RequestCacheThreadLocal.getContextPath();
            }
            throw new RuntimeException("No non-null relative base URL found", e);
        }
    }

    public String getSuperBatchVersion() {
        return "0";
    }

    public CDNStrategy getCDNStrategy() {
        return null;
    }

    public Locale getLocale() {
        return this.localeManager.getLocale(AuthenticatedUserThreadLocal.get());
    }

    public Iterable<Locale> getSupportedLocales() {
        return new HashSet<Locale>();
    }

    public String getI18nRawText(Locale locale, String key) {
        return this.i18NBeanFactory.getI18NBean(locale).getText(key, null, true);
    }

    public String getI18nText(Locale locale, String key) {
        return this.i18NBeanFactory.getI18NBean(locale).getText(key);
    }

    public Set<String> allowedCondition1Keys() {
        return new HashSet<String>();
    }

    public Set<String> allowedTransform1Keys() {
        return new HashSet<String>();
    }

    public boolean forbidCondition1AndTransformer1() {
        return false;
    }

    public boolean isIncrementalCacheEnabled() {
        return false;
    }

    public boolean isDeferJsAttributeEnabled() {
        return false;
    }
}

