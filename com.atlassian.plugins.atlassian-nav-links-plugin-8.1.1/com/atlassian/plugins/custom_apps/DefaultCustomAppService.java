/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.custom_apps;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.custom_apps.CustomAppComparator;
import com.atlassian.plugins.custom_apps.CustomAppPredicates;
import com.atlassian.plugins.custom_apps.CustomAppStore;
import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.CustomAppNotFoundException;
import com.atlassian.plugins.custom_apps.api.CustomAppService;
import com.atlassian.plugins.custom_apps.api.CustomAppsValidationException;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkAddedEvent;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkRemovedEvent;
import com.atlassian.plugins.custom_apps.api.events.NavigationLinkUpdatedEvent;
import com.atlassian.plugins.custom_apps.rest.data.validation.UrlFieldValidator;
import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinkComparator;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteNavigationLinkService;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkPredicates;
import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinkService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCustomAppService
implements CustomAppService {
    private static final Logger log = LoggerFactory.getLogger(DefaultCustomAppService.class);
    @VisibleForTesting
    static final Predicate<NavigationLink> ALL_MENU_ITEMS = link -> NavigationLink.MENU_ITEM_KEYS.contains(link.getKey());
    private final CustomAppStore customAppStore;
    private final EventPublisher eventPublisher;
    private final I18nResolver i18nResolver;
    private final LocalNavigationLinkService localNavigationLinkService;
    private final LocaleResolver localeResolver;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final RemoteNavigationLinkService remoteNavigationLinkService;

    public DefaultCustomAppService(@Nonnull I18nResolver i18nResolver, @Nonnull CustomAppStore customAppStore, @Nonnull RemoteNavigationLinkService remoteNavigationLinkService, @Nonnull LocalNavigationLinkService localNavigationLinkService, @Nonnull ReadOnlyApplicationLinkService applicationLinkService, @Nonnull LocaleResolver localeResolver, @Nonnull EventPublisher eventPublisher) {
        this.i18nResolver = i18nResolver;
        this.customAppStore = customAppStore;
        this.remoteNavigationLinkService = remoteNavigationLinkService;
        this.localNavigationLinkService = localNavigationLinkService;
        this.applicationLinkService = applicationLinkService;
        this.localeResolver = localeResolver;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Nonnull
    public List<CustomApp> getCustomApps() {
        return Lists.newArrayList((Iterable)Iterables.filter(this.customAppStore.getAll(), (Predicate)CustomAppPredicates.hasNoSourceApplicationUrl));
    }

    @Override
    @Nonnull
    public List<CustomApp> getLocalCustomAppsAndRemoteLinks() {
        return this.getRefreshedLinks();
    }

    private List<CustomApp> getRefreshedLinks() {
        Set<NavigationLink> remoteLinks = this.remoteNavigationLinkService.matching(this.localeResolver.getLocale(), ALL_MENU_ITEMS);
        Set<NavigationLink> localLinks = this.localNavigationLinkService.matching(this.localeResolver.getLocale(), ALL_MENU_ITEMS);
        ArrayList<NavigationLink> allLinks = new ArrayList<NavigationLink>();
        allLinks.addAll(remoteLinks);
        allLinks.addAll(localLinks);
        Collections.sort(allLinks, NavigationLinkComparator.INSTANCE);
        List<CustomApp> index = this.customAppStore.getAll();
        ArrayList<CustomApp> newList = new ArrayList<CustomApp>(index.size());
        List<CustomApp> indexedCustomApps = this.getLocallyIndexLinksAndCleanFromAllLinks(allLinks, index);
        newList.addAll(indexedCustomApps);
        List<CustomApp> newCustomApps = this.getUnindexedLinks(allLinks, this.maxId(newList) + 1);
        newList.addAll(newCustomApps);
        if (!this.customAppStore.isCustomOrder()) {
            Collections.sort(newList, CustomAppComparator.INSTANCE);
        }
        return newList;
    }

    private List<CustomApp> getUnindexedLinks(Collection<NavigationLink> allLinks, int startIndex) {
        return allLinks.stream().map(this.toUnindexed(startIndex)).collect(Collectors.toList());
    }

    private List<CustomApp> getLocallyIndexLinksAndCleanFromAllLinks(List<NavigationLink> allLinks, List<CustomApp> current) {
        ArrayList indexedLinks = Lists.newArrayList();
        for (CustomApp customApp : current) {
            NavigationLink navlink = this.findExactMatch(customApp, allLinks);
            if (navlink == null) continue;
            if (navlink.getSource().id() == null && navlink.getKey().equals("custom-apps")) {
                indexedLinks.add(new CustomApp(customApp.getId(), navlink, null, null, customApp.getHide(), customApp.getAllowedGroups(), true));
            } else if (navlink.getSource().id() == null && navlink.getKey().equals("home")) {
                ReadOnlyApplicationLink sourceAppLink = this.getSourceAppLink(navlink);
                indexedLinks.add(new CustomApp(customApp.getId(), navlink, this.resolveSourceApplicationUrl(sourceAppLink, navlink.getHref()), this.resolveSourceApplicationName(sourceAppLink, navlink.getLabel()), customApp.getHide(), customApp.getAllowedGroups(), customApp.getEditable()));
            } else if (navlink.getSource().id() != null) {
                ReadOnlyApplicationLink sourceAppLink = this.getSourceAppLink(navlink);
                indexedLinks.add(new CustomApp(customApp.getId(), navlink, this.resolveSourceApplicationUrl(sourceAppLink, navlink.getHref()), this.resolveSourceApplicationName(sourceAppLink, navlink.getLabel()), customApp.getHide(), customApp.getAllowedGroups(), customApp.getEditable()));
            } else {
                boolean editable = !navlink.isProductEntity() && customApp.getEditable();
                indexedLinks.add(new CustomApp(customApp.getId(), navlink, customApp.getSourceApplicationUrl(), customApp.getSourceApplicationName(), customApp.getHide(), customApp.getAllowedGroups(), editable));
            }
            allLinks.remove(navlink);
        }
        return indexedLinks;
    }

    private ReadOnlyApplicationLink getSourceAppLink(NavigationLink navLink) {
        if (navLink.getSource().id() == null) {
            return null;
        }
        try {
            return this.applicationLinkService.getApplicationLink(new ApplicationId(navLink.getSource().id()));
        }
        catch (Exception e) {
            log.error("Unable to find source ApplicationLink  for '" + navLink + "'", (Throwable)e);
            return null;
        }
    }

    private String resolveSourceApplicationUrl(ReadOnlyApplicationLink appLink, String defaultUrl) {
        if (appLink == null) {
            return defaultUrl;
        }
        if (appLink.getDisplayUrl() == null) {
            return defaultUrl;
        }
        return appLink.getDisplayUrl().toASCIIString();
    }

    private String resolveSourceApplicationName(ReadOnlyApplicationLink appLink, String defaultName) {
        if (appLink == null) {
            return defaultName;
        }
        return appLink.getName();
    }

    private int maxId(List<CustomApp> apps) {
        int maxId = 0;
        for (CustomApp ca : apps) {
            int id = Integer.parseInt(ca.getId());
            if (id <= maxId) continue;
            maxId = id;
        }
        return maxId;
    }

    private NavigationLink findExactMatch(CustomApp customApp, List<NavigationLink> links) {
        return links.stream().filter(NavigationLinkPredicates.filterCustomApp(customApp)).findFirst().orElse(null);
    }

    @Override
    public CustomApp get(String id) throws CustomAppNotFoundException {
        return this.getRefreshedLinks().stream().filter(app -> app.getId().equals(id)).findFirst().orElseThrow(() -> this.createNotFoundException(id));
    }

    @Override
    public synchronized void delete(String id) throws CustomAppNotFoundException {
        List<CustomApp> apps = this.getRefreshedLinks();
        for (CustomApp app : apps) {
            if (!app.getId().equals(id)) continue;
            apps.remove(app);
            this.eventPublisher.publish((Object)new NavigationLinkRemovedEvent(app));
            this.customAppStore.storeAll(apps);
            return;
        }
        throw this.createNotFoundException(id);
    }

    @Override
    public synchronized CustomApp create(String displayName, String url, String baseUrl, boolean hide, List<String> newAllowedGroups) throws CustomAppsValidationException {
        displayName = this.checkField("displayName", displayName);
        url = this.checkField("url", url);
        List<CustomApp> apps = this.getRefreshedLinks();
        CustomApp app = new CustomApp(this.nextId(apps), displayName, url, null, null, null, hide, newAllowedGroups, true);
        apps.add(app);
        this.eventPublisher.publish((Object)new NavigationLinkAddedEvent(app));
        this.customAppStore.storeAll(apps);
        return app;
    }

    @Override
    public synchronized CustomApp update(String id, String newDisplayName, String newUrl, boolean newHide, List<String> allowedGroups) throws CustomAppNotFoundException, CustomAppsValidationException {
        List<CustomApp> apps = this.getRefreshedLinks();
        for (int i = 0; i < apps.size(); ++i) {
            CustomApp app = apps.get(i);
            if (!app.getId().equals(id)) continue;
            newDisplayName = this.checkField("displayName", newDisplayName);
            if (app.getSourceApplicationUrl() == null) {
                newUrl = this.checkField("url", newUrl);
            }
            CustomApp updatedApp = new CustomApp(app.getId(), newDisplayName, newUrl, app.getSourceApplicationUrl(), app.getSourceApplicationName(), app.getSourceApplicationType(), newHide, allowedGroups, app.getEditable());
            apps.set(i, updatedApp);
            this.eventPublisher.publish((Object)new NavigationLinkUpdatedEvent(app, updatedApp));
            this.customAppStore.storeAll(apps);
            return updatedApp;
        }
        throw this.createNotFoundException(id);
    }

    @Override
    public synchronized void moveAfter(int idToMove, int idToMoveAfter) throws CustomAppNotFoundException {
        List<CustomApp> apps = this.getLocalCustomAppsAndRemoteLinks();
        int indexToMove = this.findIndexById(apps, idToMove);
        int indexToMoveAfter = this.findIndexById(apps, idToMoveAfter);
        ArrayList<CustomApp> newList = new ArrayList<CustomApp>(apps.size());
        for (int i = 0; i < apps.size(); ++i) {
            if (i == indexToMove) continue;
            newList.add(apps.get(i));
            if (i != indexToMoveAfter) continue;
            newList.add(apps.get(indexToMove));
        }
        this.customAppStore.storeAll(newList);
        this.customAppStore.setCustomOrder();
    }

    private int findIndexById(List<CustomApp> apps, int id) throws CustomAppNotFoundException {
        for (int i = 0; i < apps.size(); ++i) {
            if (id != Integer.parseInt(apps.get(i).getId())) continue;
            return i;
        }
        throw this.createNotFoundException(Integer.toString(id));
    }

    @Override
    public synchronized void moveToStart(int idToMove) throws CustomAppNotFoundException {
        List<CustomApp> apps = this.getLocalCustomAppsAndRemoteLinks();
        int indexToMove = this.findIndexById(apps, idToMove);
        ArrayList<CustomApp> newList = new ArrayList<CustomApp>(apps.size());
        newList.add(apps.get(indexToMove));
        for (int i = 0; i < apps.size(); ++i) {
            if (i == indexToMove) continue;
            newList.add(apps.get(i));
        }
        this.customAppStore.storeAll(newList);
        this.customAppStore.setCustomOrder();
    }

    private String checkField(String fieldKey, String value) throws CustomAppsValidationException {
        if (StringUtils.isBlank((CharSequence)value)) {
            throw new CustomAppsValidationException(fieldKey, this.i18nResolver.getText("must.not.be.empty"));
        }
        if (fieldKey.equals("url")) {
            value = this.fixUrl(value);
            if (!UrlFieldValidator.jira().isValid(value)) {
                throw new CustomAppsValidationException(fieldKey, this.i18nResolver.getText("custom-apps.manage.validation.errors.url"));
            }
        }
        return value;
    }

    private String fixUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }

    private String nextId(Iterable<CustomApp> apps) {
        int maxId = 0;
        for (CustomApp app : apps) {
            try {
                int id = Integer.parseInt(app.getId());
                if (id <= maxId) continue;
                maxId = id;
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return Integer.toString(maxId + 1);
    }

    private CustomAppNotFoundException createNotFoundException(String id) {
        return new CustomAppNotFoundException("No custom app found with id '" + id + "'");
    }

    private Function<NavigationLink, CustomApp> toUnindexed(int startIndex) {
        return new ToUnindexed(startIndex);
    }

    private class ToUnindexed
    implements Function<NavigationLink, CustomApp> {
        private int startIndex;

        private ToUnindexed(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        public CustomApp apply(NavigationLink navlink) {
            ReadOnlyApplicationLink sourceAppLink = DefaultCustomAppService.this.getSourceAppLink(navlink);
            boolean editable = false;
            boolean hidden = false;
            List<String> allowedGroups = Collections.emptyList();
            return new CustomApp(Integer.toString(this.startIndex++), navlink, DefaultCustomAppService.this.resolveSourceApplicationUrl(sourceAppLink, navlink.getHref()), DefaultCustomAppService.this.resolveSourceApplicationName(sourceAppLink, navlink.getLabel()), false, allowedGroups, false);
        }
    }
}

