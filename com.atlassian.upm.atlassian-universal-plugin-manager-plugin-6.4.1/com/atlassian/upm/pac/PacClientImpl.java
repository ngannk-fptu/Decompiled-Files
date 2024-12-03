/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.base.Predicate
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  io.atlassian.util.concurrent.RuntimeTimeoutException
 *  org.joda.time.Duration
 *  org.joda.time.ReadableDuration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.pac;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.AddonVersionSpecifier;
import com.atlassian.marketplace.client.api.AddonVersionsQuery;
import com.atlassian.marketplace.client.api.Addons;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.ApplicationVersionsQuery;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.VersionCompatibility;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.upm.ApplicationKeyUtils;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.LazyReferences;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.analytics.event.PluginUpdateCheckAnalyticsEvent;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import com.atlassian.upm.core.pac.PlatformBuildNumberChangeEvent;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.CompatibilityCheckData;
import com.atlassian.upm.pac.Execution;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.MpacApplication;
import com.atlassian.upm.pac.MpacApplicationCacheManager;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.pac.PluginVersionComparator;
import com.atlassian.upm.pac.PluginVersionPair;
import com.atlassian.upm.pac.SpiPluginComparator;
import io.atlassian.util.concurrent.ResettableLazyReference;
import io.atlassian.util.concurrent.RuntimeTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public final class PacClientImpl
implements PacClient,
DisposableBean,
InitializingBean {
    private final Duration UPDATE_CHECK_TIMEOUT = Duration.standardSeconds((long)10L);
    private final Duration UPDATE_CHECK_MAX_TIMEOUT = Duration.standardSeconds((long)60L);
    private final Duration PLUGIN_DETAILS_TIMEOUT = Duration.standardSeconds((long)10L);
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;
    private final PluginRetriever pluginRetriever;
    private final PluginMetadataAccessor metadata;
    private final SenFinder senFinder;
    private final PluginVersionComparator pluginVersionComparator;
    private final SpiPluginComparator pluginComparator;
    private final ResettableLazyReference<Boolean> reachable;
    private final ResettableLazyReference<Execution> async;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SysPersisted sysPersisted;
    private final MarketplaceClientManager mpacV2ClientFactory;
    private final UpmHostApplicationInformation hostApplicationInformation;
    private final AnalyticsLogger analytics;
    private final ApplicationPluginsManager applicationPluginsManager;
    private final Cache<Pair<String, String>, Option<IncompatiblePluginData.IncompatibilityType>> pluginIncompatibilityCache;
    private final PluginLicenseRepository pluginLicenseRepository;
    private final MpacApplicationCacheManager mpacApplicationCacheManager;
    private Function<com.atlassian.upm.core.Plugin, Callable<Option<IncompatiblePluginData>>> toIncompatiblePluginCallables = new Function<com.atlassian.upm.core.Plugin, Callable<Option<IncompatiblePluginData>>>(){

        @Override
        public Callable<Option<IncompatiblePluginData>> apply(com.atlassian.upm.core.Plugin plugin) {
            return () -> {
                Iterator iterator = ((Option)PacClientImpl.this.pluginIncompatibilityCache.get(Pair.pair(plugin.getKey(), plugin.getVersion()))).iterator();
                if (iterator.hasNext()) {
                    IncompatiblePluginData.IncompatibilityType incompatibility = (IncompatiblePluginData.IncompatibilityType)((Object)((Object)iterator.next()));
                    return Option.some(new IncompatiblePluginData(plugin, incompatibility));
                }
                return Option.none();
            };
        }
    };

    public PacClientImpl(ApplicationProperties applicationProperties, CacheFactory cacheFactory, EventPublisher eventPublisher, final MarketplaceClientManager mpacV2ClientFactory, AnalyticsLogger analytics, PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, SysPersisted sysPersisted, final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, UpmHostApplicationInformation hostApplicationInformation, PluginLicenseRepository pluginLicenseRepository, SenFinder senFinder, ApplicationPluginsManager applicationPluginsManager, MpacApplicationCacheManager mpacApplicationCacheManager) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.mpacV2ClientFactory = Objects.requireNonNull(mpacV2ClientFactory, "mpacV2ClientFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.senFinder = Objects.requireNonNull(senFinder, "senFinder");
        this.mpacApplicationCacheManager = Objects.requireNonNull(mpacApplicationCacheManager, "mpacApplicationCacheManager");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
        this.pluginVersionComparator = new PluginVersionComparator();
        this.pluginComparator = new SpiPluginComparator();
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
        this.pluginLicenseRepository = Objects.requireNonNull(pluginLicenseRepository, "pluginLicenseRepository");
        this.reachable = new ResettableLazyReference<Boolean>(){

            protected Boolean create() {
                return mpacV2ClientFactory.getMarketplaceClient().isReachable();
            }
        };
        this.async = new ResettableLazyReference<Execution>(){

            protected Execution create() {
                return new Execution(Objects.requireNonNull(threadLocalDelegateExecutorFactory, "threadLocalDelegateExecutorFactory"));
            }
        };
        CacheSettings settings = new CacheSettingsBuilder().expireAfterWrite(12L, TimeUnit.HOURS).local().build();
        this.pluginIncompatibilityCache = cacheFactory.getCache("pluginCompatibilityCache", keyAndVersion -> {
            Iterator<AddonVersion> iterator = this.getAvailablePluginVersion((String)keyAndVersion.first(), (String)keyAndVersion.second()).iterator();
            if (iterator.hasNext()) {
                AddonVersion v = iterator.next();
                return this.getIncompatibilityType(v, pluginRetriever.getPlugin((String)keyAndVersion.first()));
            }
            return Option.none();
        }, settings);
    }

    private static Predicate<ApplicationKey> compatibleWithAppKey(ApplicationKey appKey) {
        return appKey::equals;
    }

    @Override
    public Option<Boolean> isUnknownProductVersion() {
        return this.sysPersisted.is(UpmSettings.PAC_DISABLED) ? Option.none(Boolean.class) : this.unknownAppVersion();
    }

    private Option<MpacApplication> mpacApp() {
        return this.mpacApplicationCacheManager.getCachedReference().getIfPresent().orElse(Option.none(MpacApplication.class));
    }

    private Option<Boolean> unknownAppVersion() {
        return this.mpacApp().map(MpacApplication::getUnknown);
    }

    private int getMpacAppBuildNumber() {
        return (Integer)this.mpacApp().flatMap(MpacApplication::getBuildNumber).getOrElse(this.hostApplicationInformation.getBuildNumber());
    }

    @Override
    public boolean isPacReachable() {
        if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            return false;
        }
        return LazyReferences.safeGet(this.reachable);
    }

    @Override
    public Option<Links> getMarketplaceRootLinks() {
        if (this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            return Option.none();
        }
        try {
            return Option.some(this.mpacV2ClientFactory.getMarketplaceClient().getRootLinks());
        }
        catch (MpacException e) {
            return Option.none();
        }
    }

    @EventListener
    public void onPlatformBuildNumberChangeEvent(PlatformBuildNumberChangeEvent event) {
        this.clearAllCachedMarketplaceState();
    }

    @Override
    public void forgetPacReachableState(boolean force) {
        this.reachable.reset();
        if (!this.sysPersisted.is(UpmSettings.PAC_DISABLED) && !this.mpacApp().isDefined() || force) {
            this.mpacApplicationCacheManager.reset();
        }
    }

    @Override
    public void clearAllCachedMarketplaceState() {
        this.forgetPacReachableState(true);
        this.mpacApplicationCacheManager.reset();
        this.pluginIncompatibilityCache.removeAll();
    }

    @Override
    public Page<AddonSummary> findPlugins(AddonQuery query) throws MpacException {
        if (!this.isPacReachable()) {
            return Page.empty();
        }
        return this.mpacV2ClientFactory.getMarketplaceClient().addons().find(this.addRequiredQueries(query));
    }

    private AddonQuery addRequiredQueries(AddonQuery query) {
        AddonQuery.Builder builder = ((AddonQuery.Builder)((AddonQuery.Builder)AddonQuery.builder(query).application((Optional)Optional.of(this.getMarketplaceApplicationKey()))).appBuildNumber((Optional)Optional.of(this.getMpacAppBuildNumber()))).hosting(Optional.of(HostingType.SERVER)).includeHidden(Optional.of(AddonQuery.IncludeHiddenType.VISIBLE_IN_APP)).bounds(query.getBounds().withLimit(Optional.of(10)));
        if (this.hostApplicationInformation.isHostDataCenterEnabled()) {
            builder.hosting((List)Arrays.asList(HostingType.DATA_CENTER, HostingType.SERVER));
        }
        return builder.build();
    }

    @Override
    public Option<AvailableAddonWithVersion> getAvailablePlugin(String key) throws MpacException {
        return this.getAvailablePluginInternal(key, true);
    }

    private Option<AddonVersion> getAvailablePluginVersion(String key, String version) {
        try {
            Optional<AddonVersion> maybeVersion;
            AddonVersionsQuery.Builder query = AddonVersionsQuery.builder();
            Addons addons = this.mpacV2ClientFactory.getMarketplaceClient().addons();
            if (this.hostApplicationInformation.isHostDataCenterEnabled() && (maybeVersion = addons.safeGetVersion(key, AddonVersionSpecifier.versionName(version), ((AddonVersionsQuery.Builder)query.hosting((Optional)Optional.of(HostingType.DATA_CENTER))).build())).isPresent()) {
                return Option.some(maybeVersion.get());
            }
            return UpmFugueConverters.toUpmOption(addons.safeGetVersion(key, AddonVersionSpecifier.versionName(version), ((AddonVersionsQuery.Builder)query.hosting((Optional)Optional.of(HostingType.SERVER))).build()));
        }
        catch (MpacException ex) {
            this.logger.debug("Error when retrieving plugin version from MPAC: " + key + "-" + version, (Throwable)ex);
            return Option.none();
        }
    }

    private Option<AvailableAddonWithVersion> getAvailablePluginInternal(String key, boolean withCompatibility) throws MpacException {
        if (!this.isPacReachable()) {
            return Option.none();
        }
        AddonQuery.Builder query = this.addonQueryDefaults().withVersion(true);
        if (!withCompatibility) {
            query.application(Optional.empty());
            query.appBuildNumber(Optional.empty());
        }
        return this.mpacV2ClientFactory.getMarketplaceClient().addons().safeGetByKey(key, query.build()).map(AvailableAddonWithVersion::fromAddon).orElseGet(Option::none);
    }

    @Override
    public Collection<String> getCategories() throws MpacException {
        if (!this.isPacReachable()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(StreamSupport.stream(this.mpacV2ClientFactory.getMarketplaceClient().addonCategories().findForApplication(this.getMarketplaceApplicationKey()).spliterator(), false).map(addonCategorySummary -> addonCategorySummary != null ? addonCategorySummary.getName() : null).collect(Collectors.toList()));
    }

    @Override
    public Page<AddonReference> findBanners(AddonQuery query) throws MpacException {
        if (!this.isPacReachable()) {
            return Page.empty();
        }
        return this.mpacV2ClientFactory.getMarketplaceClient().addons().findBanners(this.addRequiredQueries(query));
    }

    @Override
    public Collection<AddonReference> getPluginRecommendations(String pluginKey, int maxItems) throws MpacException {
        Page<AddonReference> recommendations = this.mpacV2ClientFactory.getMarketplaceClient().addons().findRecommendations(pluginKey, this.addonQueryDefaults().bounds(QueryBounds.limit(Optional.of(maxItems))).build());
        return StreamSupport.stream(recommendations.spliterator(), false).collect(Collectors.toList());
    }

    private Execution getAsync() {
        return LazyReferences.safeGet(this.async);
    }

    @Override
    public Collection<AvailableAddonWithVersion> getUpdates() {
        return this.getUpdates(true);
    }

    @Override
    public Collection<AvailableAddonWithVersion> getUpdatesViaAutomatedJob() {
        return this.getUpdates(false);
    }

    @Override
    public Optional<AvailableAddonWithVersion> getUpdate(com.atlassian.upm.core.Plugin installedPlugin) {
        if (!this.isPacReachable()) {
            return Optional.empty();
        }
        Collection<AvailableAddonWithVersion> updates = this.getUpdates(false, true, Collections.singletonList(installedPlugin));
        return updates.stream().findFirst();
    }

    private Collection<AvailableAddonWithVersion> getUpdates(boolean userInitiated) {
        if (!this.isPacReachable()) {
            return Collections.emptyList();
        }
        return this.getUpdates(userInitiated, false, this.getUpdatablePlugins());
    }

    private Collection<AvailableAddonWithVersion> getUpdates(boolean userInitiated, boolean skipAnalytics, Collection<com.atlassian.upm.core.Plugin> updatablePlugins) {
        try {
            Addons addonsApi = this.mpacV2ClientFactory.getMarketplaceClient().addons();
            if (!skipAnalytics) {
                this.analytics.log(new PluginUpdateCheckAnalyticsEvent(updatablePlugins, userInitiated, this.senFinder));
            }
            ArrayList callables = new ArrayList();
            updatablePlugins.forEach(p -> callables.add(() -> this.getAvailableUpdate(addonsApi, (com.atlassian.upm.core.Plugin)p)));
            Duration computedUpdateCheckTimeout = this.naturalMin(this.UPDATE_CHECK_MAX_TIMEOUT, this.naturalMax(this.UPDATE_CHECK_TIMEOUT, Duration.standardSeconds((long)(updatablePlugins.size() / 2))));
            if (userInitiated && computedUpdateCheckTimeout.isLongerThan((ReadableDuration)this.UPDATE_CHECK_TIMEOUT)) {
                this.logger.warn(String.format("The request to check for app updates may take longer than expected because %d apps are installed that could have updates. This may impact the performance of loading the Manage Apps page.", updatablePlugins.size()));
                this.logger.debug("Updatable apps {}", updatablePlugins);
            }
            Set result = this.getAsync().invokeAll(Collections.unmodifiableList(callables), computedUpdateCheckTimeout).stream().filter(Option::isDefined).map(Option::get).collect(Collectors.toSet());
            return this.sortVersions(Collections.unmodifiableSet(result));
        }
        catch (RuntimeTimeoutException ex) {
            this.logger.warn("Timed out while checking for app updates: " + (Object)((Object)ex));
            return Collections.emptyList();
        }
        catch (MpacException ex) {
            this.logger.debug("Error while accessing Marketplace: " + ex);
            return Collections.emptyList();
        }
    }

    private Option<AvailableAddonWithVersion> getAvailableUpdate(Addons addonsApi, com.atlassian.upm.core.Plugin plugin) {
        AddonQuery addonQuery = this.addonQueryDefaults().build();
        try {
            Iterator<AddonVersion> iterator = this.getUpdateVersion(addonsApi, plugin, plugin.getVersion(), AddonVersionsQuery.fromAddonQuery(addonQuery)).iterator();
            if (iterator.hasNext()) {
                AddonVersion v = iterator.next();
                return UpmFugueConverters.toUpmOption(addonsApi.safeGetByKey(plugin.getKey(), addonQuery).map(a -> new AvailableAddonWithVersion((Addon)a, v)));
            }
            return Option.none();
        }
        catch (MpacException ex) {
            this.logger.debug("Error when retrieving available update: " + plugin.getKey(), (Throwable)ex);
            return Option.none();
        }
    }

    private Option<AddonVersion> getUpdateVersion(Addons addonsApi, com.atlassian.upm.core.Plugin plugin, String version, AddonVersionsQuery.Builder queryDefaults) throws MpacException {
        if (this.hostApplicationInformation.isHostDataCenterEnabled()) {
            Option<AddonVersion> dataCenterVersion = UpmFugueConverters.toUpmOption(addonsApi.safeGetVersion(plugin.getKey(), AddonVersionSpecifier.latest(), ((AddonVersionsQuery.Builder)queryDefaults.afterVersion(Optional.of(version)).hosting((Optional)Optional.of(HostingType.DATA_CENTER))).build()));
            if (Plugins.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin) || this.pluginLicenseRepository.getPluginLicense(plugin.getKey()).exists((com.google.common.base.Predicate<PluginLicense>)((com.google.common.base.Predicate)PluginLicense::isDataCenter))) {
                return dataCenterVersion;
            }
            return dataCenterVersion.orElse(UpmFugueConverters.toUpmOption(addonsApi.safeGetVersion(plugin.getKey(), AddonVersionSpecifier.latest(), ((AddonVersionsQuery.Builder)queryDefaults.afterVersion(Optional.of(version)).hosting((Optional)Optional.of(HostingType.SERVER))).build())));
        }
        return UpmFugueConverters.toUpmOption(addonsApi.safeGetVersion(plugin.getKey(), AddonVersionSpecifier.latest(), ((AddonVersionsQuery.Builder)queryDefaults.afterVersion(Optional.of(version)).hosting((Optional)Optional.of(HostingType.SERVER))).build()));
    }

    @Override
    public Collection<AvailableAddonWithVersion> getPlugins(Collection<String> pluginKeys) {
        return this.getPluginsInternal(pluginKeys, true);
    }

    @Override
    public Collection<AvailableAddonWithVersion> getLatestVersionOfPlugins(Collection<String> pluginKeys) {
        return this.getPluginsInternal(pluginKeys, false);
    }

    private Collection<AvailableAddonWithVersion> getPluginsInternal(Iterable<String> pluginKeys, boolean withCompatibility) {
        if (!this.isPacReachable()) {
            return Collections.emptyList();
        }
        List callables = StreamSupport.stream(pluginKeys.spliterator(), false).map(this.toAvailablePluginCallables(withCompatibility)).collect(Collectors.toList());
        try {
            return Collections.unmodifiableList(this.getAsync().invokeAll(callables, this.PLUGIN_DETAILS_TIMEOUT).stream().filter(Option::isDefined).map(Option::get).collect(Collectors.toList()));
        }
        catch (RuntimeTimeoutException ex) {
            this.logger.debug("Timeout while accessing plugins API: " + (Object)((Object)ex));
            return Collections.emptyList();
        }
    }

    private ApplicationKey getMarketplaceApplicationKey() {
        return ApplicationKeyUtils.getMarketplaceApplicationKey(ApplicationKey.valueOf(this.applicationProperties.getDisplayName()));
    }

    private Collection<com.atlassian.upm.core.Plugin> getUpdatablePlugins() {
        Collection installedPlugins = StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).collect(Collectors.toList());
        return Collections.unmodifiableList(installedPlugins.stream().filter(this.isIncludedInUpdateCheck(this.getApplicationRelatedPluginKeys(installedPlugins))).collect(Collectors.toList()));
    }

    private Collection<String> getUpdatablePluginKeys() {
        return this.getUpdatablePlugins().stream().map(com.atlassian.upm.core.Plugin::getKey).collect(Collectors.toList());
    }

    private Collection<String> getApplicationRelatedPluginKeys(Collection<com.atlassian.upm.core.Plugin> plugins) {
        List<Plugin> collect = plugins.stream().map(com.atlassian.upm.core.Plugin::getPlugin).collect(Collectors.toList());
        return this.applicationPluginsManager.getApplicationRelatedPlugins(collect).keySet();
    }

    private Predicate<com.atlassian.upm.core.Plugin> isIncludedInUpdateCheck(Collection<String> applicationRelatedPluginKeys) {
        return Plugins.waitingForRestart().negate().and(this.applicationRelatedPlugin(applicationRelatedPluginKeys).negate()).and(Plugins.userInstalled(this.metadata).or(Plugins.upmPlugin()).or(Plugins.optional(this.metadata)));
    }

    @Override
    public Collection<ApplicationVersion> getProductUpdates() throws MpacException {
        if (this.isPacReachable()) {
            int currentBuild = this.getMpacAppBuildNumber();
            Page<ApplicationVersion> results = this.mpacV2ClientFactory.getMarketplaceClient().applications().getVersions(this.getMarketplaceApplicationKey(), ((ApplicationVersionsQuery.Builder)ApplicationVersionsQuery.builder().afterBuildNumber(Optional.of(currentBuild)).bounds(QueryBounds.limit(Optional.of(50))).hosting((Optional)(this.hostApplicationInformation.isHostDataCenterEnabled() ? Optional.of(HostingType.DATA_CENTER) : Optional.empty()))).build());
            List pageResults = StreamSupport.stream(results.spliterator(), false).collect(Collectors.toList());
            ArrayList<ApplicationVersion> allResults = new ArrayList<ApplicationVersion>();
            while (!pageResults.isEmpty()) {
                allResults.addAll(pageResults);
                Optional<PageReference<ApplicationVersion>> maybeNext = results.safeGetNext();
                if (!maybeNext.isPresent()) break;
                results = this.mpacV2ClientFactory.getMarketplaceClient().getMore(maybeNext.get());
                pageResults = StreamSupport.stream(results.spliterator(), false).collect(Collectors.toList());
            }
            return allResults;
        }
        return Collections.emptyList();
    }

    @Override
    public ProductUpdatePluginCompatibility getProductUpdatePluginCompatibility(Collection<com.atlassian.upm.core.Plugin> installedPlugins, int buildNumber) throws MpacException {
        if (!this.isPacReachable()) {
            return new ProductUpdatePluginCompatibility.Builder().build();
        }
        Collection<com.atlassian.upm.core.Plugin> sortedPlugins = this.sort(installedPlugins);
        return this.createProductUpdatePluginCompatibilityStatuses(sortedPlugins, buildNumber);
    }

    @Override
    public Collection<IncompatiblePluginData> getIncompatiblePlugins(Collection<String> skipList) {
        if (!this.isPacReachable()) {
            return Collections.emptyList();
        }
        Collection<com.atlassian.upm.core.Plugin> installedPlugins = this.sort(StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).filter(Plugins.userInstalled(this.metadata).and(this.pluginAlreadyChecked(skipList).negate())).collect(Collectors.toList()));
        return this.getIncompatiblePluginsInternal(installedPlugins);
    }

    private Collection<IncompatiblePluginData> getIncompatiblePluginsInternal(Iterable<com.atlassian.upm.core.Plugin> installedPlugins) {
        if (!this.isPacReachable()) {
            return Collections.emptyList();
        }
        List callables = StreamSupport.stream(installedPlugins.spliterator(), false).map(this.toIncompatiblePluginCallables).collect(Collectors.toList());
        try {
            return Collections.unmodifiableList(this.getAsync().invokeAll(callables, this.UPDATE_CHECK_TIMEOUT).stream().filter(Option::isDefined).map(Option::get).collect(Collectors.toList()));
        }
        catch (RuntimeTimeoutException ex) {
            this.logger.debug("Timeout while accessing plugins API: " + (Object)((Object)ex));
            return Collections.emptyList();
        }
    }

    @Override
    public Option<IncompatiblePluginData> getPluginIncompatibility(com.atlassian.upm.core.Plugin installedPlugin) {
        return Iterables.toOption(this.getIncompatiblePluginsInternal(Collections.singletonList(installedPlugin)));
    }

    private ProductUpdatePluginCompatibility createProductUpdatePluginCompatibilityStatuses(Iterable<com.atlassian.upm.core.Plugin> installedPlugins, int targetBuild) throws MpacException {
        ProductUpdatePluginCompatibility.Builder compatibilityBuilder = new ProductUpdatePluginCompatibility.Builder();
        List callables = StreamSupport.stream(installedPlugins.spliterator(), false).map(this.toCompatibilityCallables(targetBuild, this.mpacV2ClientFactory.getMarketplaceClient().addons())).collect(Collectors.toList());
        int currentBuild = this.getMpacAppBuildNumber();
        try {
            for (CompatibilityCheckData pluginData : this.getAsync().invokeAll(callables, this.UPDATE_CHECK_TIMEOUT)) {
                if (pluginData.getInstalledVersionListing().isDefined()) {
                    if (pluginData.getInstalledVersionListing().exists((com.google.common.base.Predicate<AddonVersion>)((com.google.common.base.Predicate)this.versionCompatibleWithAppVersion(targetBuild)::test))) {
                        compatibilityBuilder.addCompatible(pluginData.getInstalledPlugin());
                        continue;
                    }
                    if (pluginData.getLatestVersionCompatibleWithTargetProduct().isDefined()) {
                        if (pluginData.getLatestVersionCompatibleWithTargetProduct().exists((com.google.common.base.Predicate<AddonVersion>)((com.google.common.base.Predicate)this.versionCompatibleWithAppVersion(currentBuild)::test))) {
                            compatibilityBuilder.addUpdateRequired(pluginData.getInstalledPlugin());
                            continue;
                        }
                        if (pluginData.getLatestVersionCompatibleWithTargetProduct().exists((com.google.common.base.Predicate<AddonVersion>)((com.google.common.base.Predicate)this.versionCompatibleWithAppVersion(targetBuild)::test))) {
                            compatibilityBuilder.addUpdateRequiredAfterProductUpdate(pluginData.getInstalledPlugin());
                            continue;
                        }
                        compatibilityBuilder.addIncompatible(pluginData.getInstalledPlugin());
                        continue;
                    }
                    compatibilityBuilder.addIncompatible(pluginData.getInstalledPlugin());
                    continue;
                }
                compatibilityBuilder.addUnknown(pluginData.getInstalledPlugin());
            }
        }
        catch (RuntimeTimeoutException ex) {
            this.logger.debug("Timeout while accessing plugins API: " + (Object)((Object)ex));
        }
        return compatibilityBuilder.build();
    }

    private Predicate<AddonVersion> versionCompatibleWithAppVersion(int buildNumber) {
        return v -> StreamSupport.stream(v.getCompatibilities().spliterator(), false).anyMatch(this.compatibleWithAppVersion(v.isDataCenterStatusCompatible() ? HostingType.DATA_CENTER : HostingType.SERVER, buildNumber));
    }

    private Predicate<VersionCompatibility> compatibleWithAppVersion(HostingType hostingType, int buildNumber) {
        return vc -> vc.isCompatibleWith((com.google.common.base.Predicate<ApplicationKey>)((com.google.common.base.Predicate)this.getMarketplaceApplicationKey()::equals), hostingType, buildNumber);
    }

    private Option<IncompatiblePluginData.IncompatibilityType> getIncompatibilityType(AddonVersion v, Option<com.atlassian.upm.core.Plugin> p) {
        if (StreamSupport.stream(v.getCompatibleApplications().spliterator(), false).noneMatch(PacClientImpl.compatibleWithAppKey(this.getMarketplaceApplicationKey()))) {
            return Option.some(IncompatiblePluginData.IncompatibilityType.APPLICATION);
        }
        if (!this.versionCompatibleWithAppVersion(this.getMpacAppBuildNumber()).test(v)) {
            return Option.some(IncompatiblePluginData.IncompatibilityType.APPLICATION_VERSION);
        }
        if (MarketplacePlugins.isDataCenterIncompatible(v, this.hostApplicationInformation) && !MarketplacePlugins.isLegacyDataCenterIncompatible(v, this.hostApplicationInformation)) {
            return Option.some(IncompatiblePluginData.IncompatibilityType.LEGACY_DATA_CENTER);
        }
        if (MarketplacePlugins.isDataCenterIncompatible(v, this.hostApplicationInformation)) {
            return Option.some(IncompatiblePluginData.IncompatibilityType.DATA_CENTER);
        }
        return Option.none();
    }

    @Override
    public Option<PluginVersionPair> getSpecificAndLatestAvailablePluginVersions(com.atlassian.upm.core.Plugin plugin, String version) {
        try {
            if (this.isPacReachable()) {
                Addons addonsApi = this.mpacV2ClientFactory.getMarketplaceClient().addons();
                AddonQuery addonQuery = this.addonQueryDefaults().build();
                Optional<Addon> maybeAddon = addonsApi.safeGetByKey(plugin.getKey(), addonQuery);
                if (maybeAddon.isPresent()) {
                    Option<AddonVersion> latest = this.getUpdatablePluginKeys().contains(plugin.getKey()) ? this.getUpdateVersion(addonsApi, plugin, version, AddonVersionsQuery.fromAddonQuery(addonQuery)) : Option.none(AddonVersion.class);
                    Option<AddonVersion> specific = this.getAvailablePluginVersion(plugin.getKey(), version);
                    return Option.some(new PluginVersionPair(maybeAddon.get(), specific, latest));
                }
            }
        }
        catch (MpacException ex) {
            this.logger.debug("Error in accessing plugins API");
        }
        return Option.none();
    }

    private AddonQuery.Builder addonQueryDefaults() {
        AddonQuery.Builder builder = ((AddonQuery.Builder)((AddonQuery.Builder)AddonQuery.builder().application((Optional)Optional.of(this.getMarketplaceApplicationKey()))).appBuildNumber((Optional)Optional.of(this.getMpacAppBuildNumber()))).hosting(Optional.of(HostingType.SERVER)).includeHidden(Optional.of(AddonQuery.IncludeHiddenType.ALL));
        if (this.hostApplicationInformation.isHostDataCenterEnabled()) {
            builder.hosting((List)Arrays.asList(HostingType.DATA_CENTER, HostingType.SERVER));
        }
        return builder;
    }

    private Predicate<com.atlassian.upm.core.Plugin> applicationRelatedPlugin(Collection<String> applicationRelatedPluginKeys) {
        ArrayList<String> keys = new ArrayList<String>(applicationRelatedPluginKeys);
        return plugin -> keys.contains(plugin.getKey());
    }

    private Predicate<com.atlassian.upm.core.Plugin> pluginAlreadyChecked(Collection<String> pluginsChecked) {
        return plugin -> pluginsChecked.contains(plugin.getKey());
    }

    private HostingType getHostingType(com.atlassian.upm.core.Plugin installedPlugin) {
        return this.hostApplicationInformation.isHostDataCenterEnabled() && (Plugins.isStatusDataCenterCompatibleAccordingToPluginDescriptor(installedPlugin) || this.pluginLicenseRepository.getPluginLicense(installedPlugin.getKey()).exists((com.google.common.base.Predicate<PluginLicense>)((com.google.common.base.Predicate)PluginLicense::isDataCenter))) ? HostingType.DATA_CENTER : HostingType.SERVER;
    }

    private Function<com.atlassian.upm.core.Plugin, Callable<CompatibilityCheckData>> toCompatibilityCallables(int targetBuildNumber, Addons addonsApi) {
        return installedPlugin -> () -> {
            try {
                AddonVersionsQuery targetQuery = ((AddonVersionsQuery.Builder)((AddonVersionsQuery.Builder)((AddonVersionsQuery.Builder)AddonVersionsQuery.builder().application((Optional)Optional.of(this.getMarketplaceApplicationKey()))).appBuildNumber((Optional)Optional.of(targetBuildNumber))).hosting((Optional)Optional.of(this.getHostingType((com.atlassian.upm.core.Plugin)installedPlugin)))).build();
                String pluginKey = installedPlugin.getKey();
                AddonVersionsQuery query = AddonVersionsQuery.builder(targetQuery).build();
                Option<AddonVersion> installedVersion = UpmFugueConverters.toUpmOption(addonsApi.safeGetVersion(pluginKey, AddonVersionSpecifier.versionName(installedPlugin.getVersion()), query));
                Option<AddonVersion> latestVersion = UpmFugueConverters.toUpmOption(addonsApi.safeGetVersion(pluginKey, AddonVersionSpecifier.latest(), query));
                return new CompatibilityCheckData((com.atlassian.upm.core.Plugin)installedPlugin, installedVersion, latestVersion);
            }
            catch (MpacException ex) {
                this.logger.debug("Error when retrieving compatible plugin versions from MPAC: " + installedPlugin.getKey(), (Throwable)ex);
                return new CompatibilityCheckData((com.atlassian.upm.core.Plugin)installedPlugin, Option.none(AddonVersion.class), Option.none(AddonVersion.class));
            }
        };
    }

    private Function<String, Callable<Option<AvailableAddonWithVersion>>> toAvailablePluginCallables(boolean withCompatibility) {
        return pluginKey -> () -> {
            try {
                return this.getAvailablePluginInternal((String)pluginKey, withCompatibility);
            }
            catch (MpacException e) {
                this.logger.warn("Error when retrieving available plugin: " + pluginKey + " : " + e);
                return Option.none();
            }
        };
    }

    private Collection<AvailableAddonWithVersion> sortVersions(Collection<AvailableAddonWithVersion> listToSort) {
        return listToSort.stream().sorted(this.pluginVersionComparator).collect(Collectors.toList());
    }

    private Collection<com.atlassian.upm.core.Plugin> sort(Collection<com.atlassian.upm.core.Plugin> listToSort) {
        return listToSort.stream().sorted(this.pluginComparator).collect(Collectors.toList());
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        if (this.async.isInitialized()) {
            this.getAsync().close();
        }
        this.async.reset();
        this.eventPublisher.unregister((Object)this);
    }

    private <E extends Comparable<E>> E naturalMin(E o1, E o2) {
        return Comparator.naturalOrder().compare(o1, o2) <= 0 ? o1 : o2;
    }

    private <E extends Comparable<E>> E naturalMax(E o1, E o2) {
        return Comparator.naturalOrder().compare(o1, o2) >= 0 ? o1 : o2;
    }
}

