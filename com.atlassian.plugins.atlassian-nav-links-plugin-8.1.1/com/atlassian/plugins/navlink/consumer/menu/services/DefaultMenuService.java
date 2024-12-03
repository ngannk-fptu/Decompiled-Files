/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.CustomAppService;
import com.atlassian.plugins.navlink.consumer.menu.services.DuplicateLinkFilter;
import com.atlassian.plugins.navlink.consumer.menu.services.MaskBitbucketNavigationLinkMapper;
import com.atlassian.plugins.navlink.consumer.menu.services.MenuService;
import com.atlassian.plugins.navlink.consumer.menu.services.NavigationLinkComparator;
import com.atlassian.plugins.navlink.consumer.menu.services.RemoteNavigationLinkService;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkBuilder;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinkPredicates;
import com.atlassian.plugins.navlink.producer.navigation.NavigationLinks;
import com.atlassian.plugins.navlink.producer.navigation.services.LocalNavigationLinkService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import io.atlassian.fugue.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class DefaultMenuService
implements MenuService {
    private final LocalNavigationLinkService localNavigationLinkService;
    private final RemoteNavigationLinkService remoteNavigationLinkService;
    private final CustomAppService customAppService;
    private final UserManager userManager;
    private final UserSettingsService userSettingsService;
    private final DuplicateLinkFilter duplicateLinkFilter = new DuplicateLinkFilter();

    public DefaultMenuService(LocalNavigationLinkService localNavigationLinkService, RemoteNavigationLinkService remoteNavigationLinkService, CustomAppService customAppService, UserManager userManager, UserSettingsService userSettingsService) {
        this.localNavigationLinkService = localNavigationLinkService;
        this.remoteNavigationLinkService = remoteNavigationLinkService;
        this.customAppService = customAppService;
        this.userManager = userManager;
        this.userSettingsService = userSettingsService;
    }

    @Override
    @Nonnull
    public Iterable<NavigationLink> getMenuItems(@Nonnull String key, String userName, @Nonnull Locale locale) {
        Set<NavigationLink> localNavigationLinks = this.getLocalNavigationLinks(key, locale);
        Set<NavigationLink> remoteNavigationLinks = this.getRemoteNavigationLinks(key, locale);
        return this.mergeNavigationLinks(localNavigationLinks, remoteNavigationLinks);
    }

    @Override
    @Nonnull
    public Iterable<NavigationLink> getAppSwitcherItems(String userName) {
        List<CustomApp> apps = this.getVisibleLocalCustomAppsAndRemoteLinks(userName);
        ArrayList<NavigationLink> menu = new ArrayList<NavigationLink>(apps.size());
        for (CustomApp customApp : apps) {
            menu.add(((NavigationLinkBuilder)NavigationLinks.copyOf(customApp).key("home")).build());
        }
        return menu;
    }

    @Nonnull
    private List<CustomApp> getVisibleLocalCustomAppsAndRemoteLinks(@Nullable String userName) {
        List<CustomApp> localAndRemoteLinksVisibleToUser = this.customAppService.getLocalCustomAppsAndRemoteLinks().stream().filter(this.isVisibleForUser(userName)).collect(Collectors.toList());
        return this.duplicateLinkFilter.filter(localAndRemoteLinksVisibleToUser);
    }

    @Nonnull
    private Predicate<CustomApp> isVisibleForUser(String userName) {
        return input -> input != null && !input.getHide() && this.visibleToCurrentUser((CustomApp)input, userName);
    }

    public boolean visibleToCurrentUser(CustomApp customApp, @Nullable String userName) {
        if (customApp.getAllowedGroups().isEmpty()) {
            return true;
        }
        if (StringUtils.isBlank((CharSequence)userName)) {
            return false;
        }
        for (String groupName : customApp.getAllowedGroups()) {
            if (!this.userManager.isUserInGroup(userName, groupName)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAppSwitcherVisibleForUser(@Nullable String userName) {
        return this.customAppService.getLocalCustomAppsAndRemoteLinks().stream().anyMatch(this.isSelfLink().negate().and(this.isVisibleForUser(userName)));
    }

    @Override
    public void setUserData(String key, String value) {
        this.userSettingsService.updateUserSettings(this.userManager.getRemoteUserKey(), userSettingsBuilder -> {
            userSettingsBuilder.put(key, value);
            return userSettingsBuilder.build();
        });
    }

    @Override
    public String getUserData(String key) {
        UserSettings userSettings = this.userSettingsService.getUserSettings(this.userManager.getRemoteUserKey());
        if (userSettings != null) {
            Option value = userSettings.getString(key);
            return value.isEmpty() ? "" : (String)value.get();
        }
        return "";
    }

    @Nonnull
    private Predicate<CustomApp> isSelfLink() {
        return customApp -> customApp != null && customApp.isSelf();
    }

    @Nonnull
    private Set<NavigationLink> getLocalNavigationLinks(@Nonnull String key, @Nonnull Locale locale) {
        return this.localNavigationLinkService.matching(locale, NavigationLinkPredicates.equalsKey(key));
    }

    @Nonnull
    private Set<NavigationLink> getRemoteNavigationLinks(@Nonnull String key, @Nonnull Locale locale) {
        Set<NavigationLink> matches = this.remoteNavigationLinkService.matching(locale, NavigationLinkPredicates.equalsKey(key));
        return matches.stream().map(remoteLink -> remoteLink != null ? NavigationLinkBuilder.copyOf(remoteLink).build() : null).collect(Collectors.toSet());
    }

    @Nonnull
    private List<NavigationLink> mergeNavigationLinks(@Nonnull Iterable<NavigationLink> localNavigationLinks, @Nonnull Iterable<NavigationLink> remoteNavigationLinks) {
        return Stream.concat(StreamSupport.stream(localNavigationLinks.spliterator(), false), StreamSupport.stream(remoteNavigationLinks.spliterator(), false)).map(MaskBitbucketNavigationLinkMapper.INSTANCE).sorted(NavigationLinkComparator.INSTANCE).collect(Collectors.toList());
    }
}

