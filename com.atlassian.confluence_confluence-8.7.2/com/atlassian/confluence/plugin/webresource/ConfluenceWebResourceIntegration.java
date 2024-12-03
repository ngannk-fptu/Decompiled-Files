/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.hash.Hasher
 *  com.google.common.hash.Hashing
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.confluence.plugin.webresource.Counter;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceWebResourceIntegration
implements WebResourceIntegration {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceWebResourceIntegration.class);
    public static final String NOCACHE_PREFIX = "NOCACHE";
    private final PluginAccessor pluginAccessor;
    private final SettingsManager settingsManager;
    private final Counter pluginResourceCounter;
    private final LocaleManager localeManager;
    private final PluginDirectoryProvider pluginDirectoryProvider;
    private final ContextPathHolder contextPathHolder;
    private final I18NBeanFactory i18NBeanFactory;
    private final Supplier<CDNStrategy> cdnStrategySupplier;
    private final PluginEventManager pluginEventManager;
    private final Supplier<DarkFeaturesManager> confluenceDarkFeatureManagerSupplier;
    private final Supplier<DarkFeatureManager> salDarkFeatureManagerSupplier;
    private final LanguageManager languageManager;
    private final EventPublisher eventPublisher;

    public ConfluenceWebResourceIntegration(PluginAccessor pluginAccessor, PluginDirectoryProvider pluginDirectoryProvider, SettingsManager settingsManager, Counter pluginResourceCounter, LocaleManager localeManager, ContextPathHolder contextPathHolder, I18NBeanFactory i18NBeanFactory, Supplier<CDNStrategy> cdnStrategySupplier, PluginEventManager pluginEventManager, DarkFeaturesManager confluenceDarkFeaturesManager, DarkFeatureManager salDarkFeatureManager, LanguageManager languageManager, EventPublisher eventPublisher) {
        this.pluginAccessor = pluginAccessor;
        this.pluginDirectoryProvider = pluginDirectoryProvider;
        this.settingsManager = settingsManager;
        this.pluginResourceCounter = pluginResourceCounter;
        this.localeManager = localeManager;
        this.contextPathHolder = contextPathHolder;
        this.i18NBeanFactory = i18NBeanFactory;
        this.cdnStrategySupplier = cdnStrategySupplier;
        this.pluginEventManager = pluginEventManager;
        this.confluenceDarkFeatureManagerSupplier = () -> confluenceDarkFeaturesManager;
        this.salDarkFeatureManagerSupplier = () -> salDarkFeatureManager;
        this.languageManager = languageManager;
        this.eventPublisher = eventPublisher;
    }

    public String getStaticResourceLocale() {
        return this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()).toString();
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
        return this.salDarkFeatureManagerSupplier.get();
    }

    public Map<String, Object> getRequestCache() {
        return RequestCacheThreadLocal.getRequestCache();
    }

    public void rebuildResourceUrlPrefix() {
    }

    public String getSystemCounter() {
        if (ConfluenceSystemProperties.isDisableCaches() || ConfluenceSystemProperties.isDevMode()) {
            return NOCACHE_PREFIX;
        }
        return this.calculateResourcePrefix();
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
            case RELATIVE: {
                return this.contextPathHolder.getContextPath();
            }
            case AUTO: {
                String url = this.contextPathHolder.getContextPath();
                if (url != null) {
                    return url;
                }
            }
            case ABSOLUTE: {
                return this.getAbsoluteBaseUrl();
            }
        }
        throw new AssertionError((Object)("Unsupported URLMode: " + urlMode));
    }

    public String getSuperBatchVersion() {
        return String.valueOf(this.pluginResourceCounter.getCounter());
    }

    public File getTemporaryDirectory() {
        return this.pluginDirectoryProvider.getWebResourceIntegrationTempDirectory();
    }

    private String getAbsoluteBaseUrl() {
        if (this.settingsManager.getGlobalSettings().getBaseUrl() != null) {
            return this.settingsManager.getGlobalSettings().getBaseUrl();
        }
        log.error("No non-null base URL found");
        return null;
    }

    public CDNStrategy getCDNStrategy() {
        return this.cdnStrategySupplier.get();
    }

    public Locale getLocale() {
        return this.localeManager.getLocale(AuthenticatedUserThreadLocal.get());
    }

    public Iterable<Locale> getSupportedLocales() {
        return this.languageManager.getLanguages().stream().map(l -> l.getLocale()).collect(Collectors.toSet());
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
        return !Boolean.getBoolean("confluence.wrm.incremental.cache.disabled");
    }

    public boolean isDeferJsAttributeEnabled() {
        Boolean optedIn;
        return this.confluenceDarkFeatureManagerSupplier.get().getDarkFeatures().isFeatureEnabled("defer.js.enable") && (optedIn = (Boolean)ServletContextThreadLocal.getRequest().getAttribute("defer.js.opt.in")) != null && optedIn != false;
    }

    private String calculateResourcePrefix() {
        Hasher hasher = Hashing.murmur3_32().newHasher();
        String scmChangeset = BuildInformation.INSTANCE.getGitCommitHash();
        hasher.putString((CharSequence)scmChangeset, StandardCharsets.UTF_8);
        CDNStrategy cdnStrategy = this.getCDNStrategy();
        if (cdnStrategy != null && cdnStrategy.supportsCdn()) {
            hasher.putString((CharSequence)cdnStrategy.transformRelativeUrl(""), StandardCharsets.UTF_8);
        }
        return Long.toString(hasher.hash().padToLong(), 36);
    }
}

