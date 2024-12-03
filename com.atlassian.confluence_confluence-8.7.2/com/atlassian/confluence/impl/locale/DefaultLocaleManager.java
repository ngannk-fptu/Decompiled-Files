/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 */
package com.atlassian.confluence.impl.locale;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.impl.locale.LocaleSelector;
import com.atlassian.confluence.languages.BrowserLanguageUtils;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.languages.LocaleParser;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserExistenceChecker;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.johnson.Johnson;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;

public class DefaultLocaleManager
implements LocaleManager {
    private static final ThreadLocalCacheAccessor<LocaleInfoCacheKey, LocaleInfo> threadLocalCache = ThreadLocalCacheAccessor.newInstance();
    @VisibleForTesting
    static final String REQUEST_LANG_HEADER = "requestLangHeader";
    @VisibleForTesting
    static final String REQUEST_LANG_OVERRIDE = "requestLangOverride";
    private final GlobalSettingsManager settingsManager;
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final UserExistenceChecker userExistenceChecker;
    private final LanguageManager languageManager;
    private final ApplicationConfiguration applicationConfig;
    private final Cache<String, Locale> localeCache;

    public DefaultLocaleManager(GlobalSettingsManager settingsManager, UserPreferencesAccessor userPreferencesAccessor, UserExistenceChecker userExistenceChecker, LanguageManager languageManager, CacheFactory cacheFactory, ApplicationConfiguration applicationConfig) {
        this.userExistenceChecker = userExistenceChecker;
        this.languageManager = languageManager;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.settingsManager = settingsManager;
        this.applicationConfig = applicationConfig;
        this.localeCache = CoreCache.LOCALE_BY_USER_NAME.getCache(cacheFactory);
    }

    @Override
    public final void invalidateLocaleInfoCache(@Nullable User user) {
        if (user != null) {
            threadLocalCache.put(new LocaleInfoCacheKey(user.getName()), null);
        }
    }

    @Override
    public final @NonNull LocaleInfo getLocaleInfo(@Nullable User user) {
        if (!BootstrapUtils.getBootstrapManager().isSetupComplete()) {
            Locale setupLocale = LocaleParser.toLocale((String)this.applicationConfig.getProperty((Object)"confluence.setup.locale"));
            Locale targetLocale = Optional.ofNullable(setupLocale).orElse(this.getSiteDefaultLocale());
            return new LocaleInfo(targetLocale, targetLocale, LocaleInfo.SelectionReason.GLOBAL);
        }
        Map localeInfoCache = RequestCacheThreadLocal.getRequestCache();
        return localeInfoCache.computeIfAbsent(this.toUserLocaleCacheKey(user), k -> this.resolveLocaleInfo(user));
    }

    private LocaleInfoCacheKey toUserLocaleCacheKey(@Nullable User user) {
        return user != null ? new LocaleInfoCacheKey(user.getName()) : new LocaleInfoCacheKey("confluence.locale.info.default");
    }

    private void clearUserLocaleCache(@Nullable User user) {
        Map localeInfoCache = RequestCacheThreadLocal.getRequestCache();
        localeInfoCache.remove(this.toUserLocaleCacheKey(user));
    }

    @VisibleForTesting
    @NonNull LocaleInfo resolveLocaleInfo(@Nullable User user) {
        Locale requestedLocale;
        Map requestedLanguagesCache = RequestCacheThreadLocal.getRequestCache();
        boolean useRequestedLanguage = DefaultLocaleManager.useRequestLangForUser(user);
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
        LocaleInfo fromCache = threadLocalCache.get(new LocaleInfoCacheKey(userName));
        if (fromCache != null) {
            return new LocaleInfo(requestedBrowserLocale, fromCache.getSelectedLocale(), fromCache.getSelectionReason());
        }
        Locale locale3 = null;
        LocaleInfo.SelectionReason selectionReason = LocaleInfo.SelectionReason.GLOBAL;
        if (this.doesUserExists(userName)) {
            ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
            locale3 = this.userPreferencesAccessor.getConfluenceUserPreferences(confluenceUser).getLocale();
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
        threadLocalCache.put(new LocaleInfoCacheKey(userName), localeInfo);
        return localeInfo;
    }

    @Override
    public Locale getLocale(User user) {
        return this.getLocaleInfo(user).getSelectedLocale();
    }

    private static boolean useRequestLangForUser(User user) {
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

    @VisibleForTesting
    boolean doesUserExists(String name) {
        try {
            return this.userExistenceChecker.exists(name);
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
        Locale fromCache = (Locale)this.localeCache.get((Object)languageString);
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

    private @Nullable Locale getBestLanguage(Collection<Locale> preferredLocales) {
        return new LocaleSelector(this.languageManager).getBestLanguage(preferredLocales);
    }

    static class LocaleInfoCacheKey {
        public static final String DEFAULT_LOCALE_INFO_CACHE_KEY = "confluence.locale.info.default";
        private final String userName;

        public LocaleInfoCacheKey(@NonNull String userName) {
            this.userName = Objects.requireNonNull(userName);
        }

        public boolean equals(Object other) {
            return this == other || other != null && other.getClass() == this.getClass() && this.userName.equals(((LocaleInfoCacheKey)other).userName);
        }

        public int hashCode() {
            return this.userName.hashCode();
        }
    }

    @VisibleForTesting
    static class UserLocaleCacheKey {
        private final String username;

        @VisibleForTesting
        UserLocaleCacheKey(String username) {
            Objects.requireNonNull(username, "username must not be null");
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

