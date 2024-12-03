/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.JvmCacheSettingsBuilder
 *  com.atlassian.vcache.VCacheFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.languages;

import com.atlassian.cache.CacheManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.languages.BrowserLanguageUtils;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.johnson.Johnson;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettingsBuilder;
import com.atlassian.vcache.VCacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.util.Assert;

@Deprecated
public class DefaultLocaleManager
implements LocaleManager {
    private static final ThreadLocalCacheAccessor<LocaleInfo.CacheKey, LocaleInfo> threadLocalCache = ThreadLocalCacheAccessor.newInstance();
    @VisibleForTesting
    static final String REQUEST_LANG_HEADER = "requestLangHeader";
    @VisibleForTesting
    static final String REQUEST_LANG_OVERRIDE = "requestLangOverride";
    private SettingsManager settingsManager;
    private Supplier<UserAccessor> userAccessorSupplier;
    private LanguageManager languageManager;
    private JvmCache<String, Locale> localeCache;

    public DefaultLocaleManager(SettingsManager settingsManager, UserAccessor userAccessor, LanguageManager languageManager, VCacheFactory cacheFactory) {
        this.setLanguageManager(languageManager);
        this.setSettingsManager(settingsManager);
        this.setCacheFactory(cacheFactory);
        this.setUserAccessorGenerator(() -> userAccessor);
    }

    @Deprecated
    public DefaultLocaleManager() {
    }

    @Deprecated
    public void setUserAccessorSupplier(com.atlassian.util.concurrent.Supplier<UserAccessor> userAccessorSupplier) {
        this.userAccessorSupplier = () -> userAccessorSupplier.get();
    }

    public void setUserAccessorGenerator(Supplier<UserAccessor> userAccessorSupplier) {
        this.setUserAccessorSupplier((com.atlassian.util.concurrent.Supplier<UserAccessor>)((com.atlassian.util.concurrent.Supplier)userAccessorSupplier::get));
    }

    @Deprecated
    public void setCacheManager(CacheManager cacheManager) {
    }

    public void setCacheFactory(VCacheFactory cacheFactory) {
        this.localeCache = CoreCache.LOCALE_BY_USER_NAME.resolve(cacheName -> cacheFactory.getJvmCache(cacheName, new JvmCacheSettingsBuilder().build()));
    }

    @VisibleForTesting
    void setLocaleCache(JvmCache<String, Locale> localeCache) {
        this.localeCache = localeCache;
    }

    public void setLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @Override
    public final void invalidateLocaleInfoCache(@Nullable User user) {
        if (user != null) {
            threadLocalCache.put(new LocaleInfo.CacheKey(user.getName()), null);
        }
    }

    @Override
    public final @NonNull LocaleInfo getLocaleInfo(@Nullable User user) {
        if (!GeneralUtil.isSetupComplete()) {
            ApplicationConfiguration applicationConfig = (ApplicationConfiguration)BootstrapUtils.getBootstrapContext().getBean("applicationConfig");
            Locale setupLocale = LocaleParser.toLocale((String)applicationConfig.getProperty((Object)"confluence.setup.locale"));
            Locale targetLocale = Optional.ofNullable(setupLocale).orElse(this.getSiteDefaultLocale());
            return new LocaleInfo(targetLocale, targetLocale, LocaleInfo.SelectionReason.GLOBAL);
        }
        Map localeInfoCache = RequestCacheThreadLocal.getRequestCache();
        return localeInfoCache.computeIfAbsent(this.toUserLocaleCacheKey(user), k -> this.resolveLocaleInfo(user));
    }

    private LocaleInfo.CacheKey toUserLocaleCacheKey(@Nullable User user) {
        return user != null ? new LocaleInfo.CacheKey(user.getName()) : new LocaleInfo.CacheKey("confluence.locale.info.default");
    }

    private void clearUserLocaleCache(@Nullable User user) {
        Map localeInfoCache = RequestCacheThreadLocal.getRequestCache();
        localeInfoCache.remove(this.toUserLocaleCacheKey(user));
    }

    @VisibleForTesting
    @NonNull LocaleInfo resolveLocaleInfo(@Nullable User user) {
        Locale requestedLocale;
        Map requestedLanguagesCache = RequestCacheThreadLocal.getRequestCache();
        boolean useRequestedLanguage = this.useRequestLangForUser(user);
        Locale requestedBrowserLocale = (Locale)requestedLanguagesCache.get(new UserLocaleCacheKey(REQUEST_LANG_HEADER));
        Locale requestedOverrideLocale = (Locale)requestedLanguagesCache.get(new UserLocaleCacheKey(REQUEST_LANG_OVERRIDE));
        boolean shouldOverrideLocale = requestedOverrideLocale != null;
        Locale locale = requestedLocale = shouldOverrideLocale ? requestedOverrideLocale : requestedBrowserLocale;
        if (user == null) {
            Locale anonymousLocale;
            boolean shouldUseRequestedLanguage = useRequestedLanguage && requestedLocale != null;
            Locale locale2 = anonymousLocale = shouldUseRequestedLanguage ? requestedLocale : this.getSiteDefaultLocale();
            LocaleInfo.SelectionReason anonymousReason = shouldOverrideLocale ? LocaleInfo.SelectionReason.OVERRIDE : (shouldUseRequestedLanguage ? LocaleInfo.SelectionReason.BROWSER : LocaleInfo.SelectionReason.GLOBAL);
            return new LocaleInfo(requestedBrowserLocale, anonymousLocale, anonymousReason);
        }
        String userName = user.getName();
        LocaleInfo fromCache = threadLocalCache.get(new LocaleInfo.CacheKey(userName));
        if (fromCache != null) {
            return new LocaleInfo(requestedBrowserLocale, fromCache.getSelectedLocale(), fromCache.getSelectionReason());
        }
        Locale locale3 = null;
        LocaleInfo.SelectionReason selectionReason = LocaleInfo.SelectionReason.GLOBAL;
        if (this.doesUserExists(userName)) {
            ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
            PropertySet userProperties = this.getUserAccessor().getPropertySet(confluenceUser);
            locale3 = new ConfluenceUserPreferences(userProperties).getLocale();
            selectionReason = LocaleInfo.SelectionReason.PROFILE;
        }
        if (locale3 == null || this.getBestLanguage(Collections.singleton(locale3)) == null) {
            if (shouldOverrideLocale) {
                locale3 = requestedOverrideLocale;
                selectionReason = LocaleInfo.SelectionReason.OVERRIDE;
            } else if (useRequestedLanguage && requestedBrowserLocale != null) {
                locale3 = requestedBrowserLocale;
                selectionReason = LocaleInfo.SelectionReason.BROWSER;
            } else {
                locale3 = this.getSiteDefaultLocale();
                selectionReason = LocaleInfo.SelectionReason.GLOBAL;
            }
        }
        LocaleInfo localeInfo = new LocaleInfo(requestedBrowserLocale, locale3, selectionReason);
        threadLocalCache.put(new LocaleInfo.CacheKey(userName), localeInfo);
        return localeInfo;
    }

    @Override
    public Locale getLocale(User user) {
        return this.getLocaleInfo(user).getSelectedLocale();
    }

    private boolean useRequestLangForUser(User user) {
        if (user != null && user.equals(AuthenticatedUserThreadLocal.get())) {
            return BrowserLanguageUtils.isBrowserLanguageEnabled();
        }
        return user == null && AuthenticatedUserThreadLocal.get() == null && BrowserLanguageUtils.isBrowserLanguageEnabled();
    }

    @Override
    public Locale getSiteDefaultLocale() {
        if (this.settingsManager == null || !ContainerManager.isContainerSetup()) {
            return DEFAULT_LOCALE;
        }
        String localeSetting = this.settingsManager.getGlobalSettings().getGlobalDefaultLocale();
        if (localeSetting == null) {
            return DEFAULT_LOCALE;
        }
        return LocaleParser.toLocale(localeSetting);
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public UserAccessor getUserAccessor() {
        return this.userAccessorSupplier.get();
    }

    @VisibleForTesting
    boolean doesUserExists(String name) {
        try {
            return this.getUserAccessor().exists(name);
        }
        catch (CannotCreateTransactionException e) {
            if (!Johnson.getEventContainer().hasEvents()) {
                throw e;
            }
            return false;
        }
    }

    @Override
    public void setRequestLanguages(String languageString) {
        Locale locale;
        Map requestLanguages = RequestCacheThreadLocal.getRequestCache();
        Locale fromCache = this.localeCache.get((Object)languageString).orElse(null);
        UserLocaleCacheKey key = new UserLocaleCacheKey(REQUEST_LANG_HEADER);
        if (fromCache == null) {
            String[] languages = languageString.split("[,;]");
            Collection preferredLocales = Arrays.stream(languages).map(LocaleParser::toLocaleFromHttpHeader).filter(Objects::nonNull).collect(Collectors.toList());
            locale = this.getBestLanguage(preferredLocales);
            if (this.localeValidAndSaveable(locale)) {
                this.localeCache.put((Object)languageString, (Object)locale);
            }
        } else {
            locale = fromCache;
        }
        requestLanguages.putIfAbsent(key, locale);
    }

    @VisibleForTesting
    boolean localeValidAndSaveable(Locale locale) {
        return locale != null && this.settingsManager.getGlobalSettings().isSaveable();
    }

    @Override
    public void setLanguage(String language) {
        if (this.languageManager.getLanguage(language) == null) {
            return;
        }
        Map requestLanguages = RequestCacheThreadLocal.getRequestCache();
        Locale locale = LocaleParser.toLocale(language);
        UserLocaleCacheKey key = new UserLocaleCacheKey(REQUEST_LANG_OVERRIDE);
        requestLanguages.put(key, locale);
        this.clearUserLocaleCache(AuthenticatedUserThreadLocal.get());
    }

    @VisibleForTesting
    @Nullable Locale getBestLanguage(Collection<Locale> preferredLocales) {
        List<Language> installedLanguages = this.languageManager.getLanguages();
        Locale goodEnoughMatch = null;
        String lastLanguage = null;
        for (Locale preferredLocale : preferredLocales) {
            if (goodEnoughMatch != null && !preferredLocale.getLanguage().equalsIgnoreCase(lastLanguage)) {
                return goodEnoughMatch;
            }
            for (Language installedLanguage : installedLanguages) {
                Locale installedLocale = installedLanguage.getLocale();
                if (!installedLocale.getLanguage().equalsIgnoreCase(preferredLocale.getLanguage())) continue;
                if (goodEnoughMatch == null) {
                    goodEnoughMatch = installedLocale;
                }
                if (!installedLanguage.getCountry().equalsIgnoreCase(preferredLocale.getCountry())) continue;
                return installedLocale;
            }
            lastLanguage = preferredLocale.getLanguage();
        }
        return goodEnoughMatch;
    }

    @VisibleForTesting
    static class UserLocaleCacheKey {
        private final String username;

        @VisibleForTesting
        UserLocaleCacheKey(String username) {
            Assert.notNull((Object)username, (String)"username must not be null");
            this.username = username;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserLocaleCacheKey that = (UserLocaleCacheKey)o;
            return Objects.equals(this.username, that.username);
        }

        public int hashCode() {
            return Objects.hash(this.username);
        }
    }
}

