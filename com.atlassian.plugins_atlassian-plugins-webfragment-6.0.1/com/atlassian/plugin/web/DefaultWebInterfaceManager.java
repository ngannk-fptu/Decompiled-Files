/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.api.DynamicWebInterfaceManager
 *  com.atlassian.plugin.web.api.WebItem
 *  com.atlassian.plugin.web.api.WebSection
 *  com.atlassian.plugin.web.api.model.WebFragmentBuilder
 *  com.atlassian.plugin.web.api.model.WebFragmentBuilder$WebItemBuilder
 *  com.atlassian.plugin.web.api.model.WeightedComparator
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.base.Function
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.web;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.api.DynamicWebInterfaceManager;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.WebSection;
import com.atlassian.plugin.web.api.model.WebFragmentBuilder;
import com.atlassian.plugin.web.api.model.WeightedComparator;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemProviderModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionProviderModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebInterfaceManager
implements DynamicWebInterfaceManager {
    public static final WeightedDescriptorComparator WEIGHTED_DESCRIPTOR_COMPARATOR = new WeightedDescriptorComparator();
    @VisibleForTesting
    static final String CONDITION_METRIC_KEY = "web.fragment.condition";
    @VisibleForTesting
    static final String FRAGMENT_LOCATION_TAG_KEY = "fragmentLocation";
    @VisibleForTesting
    static final String CONDITION_CLASSNAME_TAG_KEY = "conditionClassName";
    private static final Logger log = LoggerFactory.getLogger(DefaultWebInterfaceManager.class);
    private static final long CACHE_EXPIRY = Long.getLong("com.atlassian.plugin.web.interface.caches.timeout.sec", 3600L);
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<String, List<WebSectionModuleDescriptor>> sections = CacheBuilder.newBuilder().expireAfterAccess(CACHE_EXPIRY, TimeUnit.SECONDS).build((CacheLoader)new WebSectionCacheLoader());
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<String, List<WebItemModuleDescriptor>> items = CacheBuilder.newBuilder().expireAfterAccess(CACHE_EXPIRY, TimeUnit.SECONDS).build((CacheLoader)new WebItemCacheLoader());
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<String, List<WebPanelModuleDescriptor>> panels = CacheBuilder.newBuilder().expireAfterAccess(CACHE_EXPIRY, TimeUnit.SECONDS).build((CacheLoader)new WebPanelCacheLoader());
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<String, List<WebItemProviderModuleDescriptor>> itemProviders = CacheBuilder.newBuilder().expireAfterAccess(CACHE_EXPIRY, TimeUnit.SECONDS).build((CacheLoader)new WebItemProviderCacheLoader());
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LoadingCache<String, List<WebSectionProviderModuleDescriptor>> sectionProviders = CacheBuilder.newBuilder().expireAfterAccess(CACHE_EXPIRY, TimeUnit.SECONDS).build((CacheLoader)new WebSectionProviderCacheLoader());
    @Nullable
    private PluginAccessor pluginAccessor;
    @Nullable
    private WebFragmentHelper webFragmentHelper;

    public DefaultWebInterfaceManager() {
        this.refresh();
    }

    public DefaultWebInterfaceManager(PluginAccessor pluginAccessor, WebFragmentHelper webFragmentHelper) {
        this.pluginAccessor = pluginAccessor;
        this.webFragmentHelper = webFragmentHelper;
        this.refresh();
    }

    @Deprecated
    public DefaultWebInterfaceManager(@Nullable EventPublisher eventPublisher, @Nullable PluginAccessor pluginAccessor, @Nullable WebFragmentHelper webFragmentHelper) {
        this.pluginAccessor = pluginAccessor;
        this.webFragmentHelper = webFragmentHelper;
        this.refresh();
    }

    public boolean hasSectionsForLocation(String location) {
        return !Iterables.isEmpty(this.getWebSections(location, Collections.emptyMap()));
    }

    public List<WebSectionModuleDescriptor> getSections(String location) {
        return location == null ? Collections.emptyList() : (List)this.sections.getUnchecked((Object)location);
    }

    public List<WebSectionModuleDescriptor> getDisplayableSections(String location, Map<String, Object> context) {
        return this.filterFragmentsByCondition(location, this.getSections(location), context);
    }

    public List<WebItemModuleDescriptor> getItems(String section) {
        return section == null ? Collections.emptyList() : (List)this.items.getUnchecked((Object)section);
    }

    public List<WebItemModuleDescriptor> getDisplayableItems(String section, Map<String, Object> context) {
        return this.filterFragmentsByCondition(section, this.getItems(section), context);
    }

    public List<WebPanel> getDisplayableWebPanels(String location, Map<String, Object> context) {
        return this.toWebPanels(this.getDisplayableWebPanelDescriptors(location, context));
    }

    public List<WebPanelModuleDescriptor> getDisplayableWebPanelDescriptors(String location, Map<String, Object> context) {
        return this.filterFragmentsByCondition(location, this.getWebPanelDescriptors(location), context);
    }

    public List<WebPanel> getWebPanels(String location) {
        return this.toWebPanels(this.getWebPanelDescriptors(location));
    }

    private List<WebPanel> toWebPanels(List<WebPanelModuleDescriptor> descriptors) {
        return descriptors.stream().map(ModuleDescriptor::getModule).collect(Collectors.toList());
    }

    public List<WebPanelModuleDescriptor> getWebPanelDescriptors(String location) {
        return location == null ? Collections.emptyList() : (List)this.panels.getUnchecked((Object)location);
    }

    private <T extends WebFragmentModuleDescriptor<?>> List<T> filterFragmentsByCondition(String fragmentLocation, List<T> relevantItems, Map<String, Object> context) {
        if (relevantItems.isEmpty()) {
            return relevantItems;
        }
        ArrayList<T> result = new ArrayList<T>(relevantItems);
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            WebFragmentModuleDescriptor descriptor = (WebFragmentModuleDescriptor)iterator.next();
            try {
                if (descriptor.getCondition() == null) continue;
                String conditionClassName = descriptor.getCondition().getClass().getName();
                Ticker ignoredTicker = Metrics.metric((String)CONDITION_METRIC_KEY).fromPluginKey(descriptor.getPluginKey()).tag(CONDITION_CLASSNAME_TAG_KEY, conditionClassName).tag(FRAGMENT_LOCATION_TAG_KEY, fragmentLocation).withAnalytics().startTimer();
                Throwable throwable = null;
                try {
                    if (descriptor.getCondition().shouldDisplay(context)) continue;
                    iterator.remove();
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (ignoredTicker == null) continue;
                    if (throwable != null) {
                        try {
                            ignoredTicker.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    ignoredTicker.close();
                }
            }
            catch (Throwable t) {
                log.error("Could not evaluate condition '" + descriptor.getCondition() + "' for descriptor: " + descriptor, t);
                iterator.remove();
            }
        }
        return result;
    }

    public void refresh() {
        this.sections.invalidateAll();
        this.items.invalidateAll();
        this.panels.invalidateAll();
        this.itemProviders.invalidateAll();
        this.sectionProviders.invalidateAll();
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public WebFragmentHelper getWebFragmentHelper() {
        return this.webFragmentHelper;
    }

    public void setWebFragmentHelper(WebFragmentHelper webFragmentHelper) {
        this.webFragmentHelper = webFragmentHelper;
    }

    public Iterable<WebItem> getWebItems(String section, Map<String, Object> context) {
        return this.getDynamicWebItems(this.getItems(section), section, context);
    }

    public Iterable<WebItem> getDisplayableWebItems(String section, Map<String, Object> context) {
        return this.getDynamicWebItems(this.getDisplayableItems(section, context), section, context);
    }

    public Iterable<WebSection> getWebSections(String location, Map<String, Object> context) {
        return this.getDynamicWebSections(this.getSections(location), location, context);
    }

    public Iterable<WebSection> getDisplayableWebSections(String location, Map<String, Object> context) {
        return this.getDynamicWebSections(this.getDisplayableSections(location, context), location, context);
    }

    private Iterable<WebItem> getDynamicWebItems(List<WebItemModuleDescriptor> staticItems, String section, Map<String, Object> context) {
        ArrayList dynamicWebItems = Lists.newArrayList((Iterable)Iterables.transform(staticItems, (Function)new WebItemConverter(context)));
        for (WebItemProviderModuleDescriptor itemProvider : (List)this.itemProviders.getUnchecked((Object)section)) {
            try {
                Optional<Iterable> providedItems = Optional.ofNullable(itemProvider.getModule().getItems(context));
                providedItems.ifPresent(webItems -> Iterables.addAll((Collection)dynamicWebItems, (Iterable)webItems));
            }
            catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.error("WebItemProvider from module '" + itemProvider.getCompleteKey() + "' threw an error '" + e.getMessage() + "'. Web-items provided by this provider will be ignored.", (Throwable)e);
                    continue;
                }
                log.error("WebItemProvider from module '" + itemProvider.getCompleteKey() + "' threw an error '" + e.getMessage() + "'. Web-items provided by this provider will be ignored.");
            }
        }
        dynamicWebItems.sort(WeightedComparator.WEIGHTED_FRAGMENT_COMPARATOR);
        return dynamicWebItems;
    }

    private Iterable<WebSection> getDynamicWebSections(List<WebSectionModuleDescriptor> staticSections, String location, Map<String, Object> context) {
        ArrayList dynamicWebSections = Lists.newArrayList((Iterable)Iterables.transform(staticSections, (Function)new WebSectionConverter(context)));
        for (WebSectionProviderModuleDescriptor provider : (List)this.sectionProviders.getUnchecked((Object)location)) {
            try {
                Optional<Iterable> sections = Optional.ofNullable(provider.getModule().getSections(context));
                sections.ifPresent(webSections -> Iterables.addAll((Collection)dynamicWebSections, (Iterable)webSections));
            }
            catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.error("WebSectionProvider from module '" + provider.getCompleteKey() + "' threw an error '" + e.getMessage() + "'. Web-sections provided by this provider will be ignored.", (Throwable)e);
                    continue;
                }
                log.error("WebItemProvider from module '" + provider.getCompleteKey() + "' threw an error '" + e.getMessage() + "'. Web-sections provided by this provider will be ignored.");
            }
        }
        dynamicWebSections.sort(WeightedComparator.WEIGHTED_FRAGMENT_COMPARATOR);
        return dynamicWebSections;
    }

    private class WebSectionProviderCacheLoader
    extends CacheLoader<String, List<WebSectionProviderModuleDescriptor>> {
        private WebSectionProviderCacheLoader() {
        }

        public List<WebSectionProviderModuleDescriptor> load(String location) throws Exception {
            List webSectionProviderModuleDescriptors = DefaultWebInterfaceManager.this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebSectionProviderModuleDescriptor.class);
            return webSectionProviderModuleDescriptors.stream().filter(webSectionProviderModuleDescriptor -> Objects.equals(webSectionProviderModuleDescriptor.getLocation(), location)).collect(Collectors.toList());
        }
    }

    private class WebItemProviderCacheLoader
    extends CacheLoader<String, List<WebItemProviderModuleDescriptor>> {
        private WebItemProviderCacheLoader() {
        }

        public List<WebItemProviderModuleDescriptor> load(String section) throws Exception {
            List webItemProviderModuleDescriptors = DefaultWebInterfaceManager.this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebItemProviderModuleDescriptor.class);
            return webItemProviderModuleDescriptors.stream().filter(webItemProviderModuleDescriptor -> Objects.equals(webItemProviderModuleDescriptor.getSection(), section)).collect(Collectors.toList());
        }
    }

    private class WebPanelCacheLoader
    extends CacheLoader<String, List<WebPanelModuleDescriptor>> {
        private WebPanelCacheLoader() {
        }

        public List<WebPanelModuleDescriptor> load(String location) throws Exception {
            List webPanelModuleDescriptors = DefaultWebInterfaceManager.this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebPanelModuleDescriptor.class);
            return webPanelModuleDescriptors.stream().filter(webPanelModuleDescriptor -> Objects.equals(location, webPanelModuleDescriptor.getLocation())).sorted(WEIGHTED_DESCRIPTOR_COMPARATOR).collect(Collectors.toList());
        }
    }

    private class WebItemCacheLoader
    extends CacheLoader<String, List<WebItemModuleDescriptor>> {
        private WebItemCacheLoader() {
        }

        public List<WebItemModuleDescriptor> load(String section) throws Exception {
            List webItemModuleDescriptors = DefaultWebInterfaceManager.this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebItemModuleDescriptor.class);
            return webItemModuleDescriptors.stream().filter(webItemModuleDescriptor -> Objects.equals(section, webItemModuleDescriptor.getSection())).sorted(WEIGHTED_DESCRIPTOR_COMPARATOR).collect(Collectors.toList());
        }
    }

    private class WebSectionCacheLoader
    extends CacheLoader<String, List<WebSectionModuleDescriptor>> {
        private WebSectionCacheLoader() {
        }

        public List<WebSectionModuleDescriptor> load(String location) throws Exception {
            List webSectionModuleDescriptors = DefaultWebInterfaceManager.this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebSectionModuleDescriptor.class);
            return webSectionModuleDescriptors.stream().filter(webSectionModuleDescriptor -> Objects.equals(location, webSectionModuleDescriptor.getLocation())).sorted(WEIGHTED_DESCRIPTOR_COMPARATOR).collect(Collectors.toList());
        }
    }

    private static class WebSectionConverter
    implements Function<WebSectionModuleDescriptor, WebSection> {
        private final Map<String, Object> context;

        public WebSectionConverter(Map<String, Object> context) {
            this.context = context;
        }

        public WebSection apply(WebSectionModuleDescriptor input) {
            WebFragmentBuilder builder = new WebFragmentBuilder(input.getCompleteKey(), input.getWeight());
            builder.id(input.getKey());
            if (input.getWebLabel() != null) {
                builder.label(input.getWebLabel().getDisplayableLabel(null, this.context));
            }
            if (input.getTooltip() != null) {
                builder.title(input.getTooltip().getDisplayableLabel(null, this.context));
            }
            if (input.getWebParams() != null) {
                builder.params((Map)input.getWebParams().getParams());
            }
            return builder.webSection(input.getLocation()).build();
        }
    }

    private static class WebItemConverter
    implements Function<WebItemModuleDescriptor, WebItem> {
        private final Map<String, Object> context;

        public WebItemConverter(Map<String, Object> context) {
            this.context = context;
        }

        public WebItem apply(WebItemModuleDescriptor input) {
            WebFragmentBuilder builder = new WebFragmentBuilder(input.getCompleteKey(), input.getWeight());
            builder.styleClass(input.getStyleClass());
            if (input.getWebLabel() != null) {
                builder.label(input.getWebLabel().getDisplayableLabel(null, this.context));
            }
            if (input.getTooltip() != null) {
                builder.title(input.getTooltip().getDisplayableLabel(null, this.context));
            }
            if (input.getWebParams() != null) {
                builder.params((Map)input.getWebParams().getParams());
            }
            if (input.getIcon() != null && input.getIcon().getUrl() != null) {
                builder.addParam("iconUrl", input.getIcon().getUrl().getRenderedUrl(this.context));
            }
            WebFragmentBuilder.WebItemBuilder webItemBuilder = builder.webItem(input.getSection(), input.getEntryPoint());
            WebLink link = input.getLink();
            if (link != null) {
                Optional<HttpServletRequest> httpRequest;
                builder.id(link.getId());
                if (link.hasAccessKey()) {
                    webItemBuilder.accessKey(link.getAccessKey(this.context));
                }
                String url = (httpRequest = Optional.ofNullable((HttpServletRequest)this.context.get("request"))).isPresent() ? link.getDisplayableUrl(httpRequest.get(), this.context) : link.getRenderedUrl(this.context);
                webItemBuilder.url(url);
            }
            return webItemBuilder.build();
        }
    }
}

