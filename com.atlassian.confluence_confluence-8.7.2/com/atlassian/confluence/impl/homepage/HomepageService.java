/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.homepage;

import com.atlassian.confluence.impl.homepage.Homepage;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HomepageService {
    @VisibleForTesting
    static final List<String> REQUIRED_SPACE_PERMISSIONS = ImmutableList.of((Object)"VIEWSPACE", (Object)"USECONFLUENCE");
    private final SpaceManager spaceManager;
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;
    private final SpacePermissionManager spacePermissionManager;

    public HomepageService(SpaceManager spaceManager, SettingsManager settingsManager, UserAccessor userAccessor, SpacePermissionManager spacePermissionManager) {
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.spacePermissionManager = Objects.requireNonNull(spacePermissionManager);
    }

    public @NonNull Homepage getHomepage(@Nullable ConfluenceUser user) {
        if (user == null) {
            return this.defaultSiteHome(null);
        }
        String userHomepageSetting = StringUtils.trimToNull((String)this.userAccessor.getPropertySet(user).getString("confluence.user.site.homepage"));
        if (userHomepageSetting == null) {
            return this.defaultSiteHome(user);
        }
        if ("dashboard".equals(userHomepageSetting)) {
            return Homepage.dashboardHomepage();
        }
        if ("profile".equals(userHomepageSetting)) {
            return Homepage.userProfileHomepage(user);
        }
        if ("siteHomepage".equals(userHomepageSetting)) {
            return this.defaultSiteHome(user);
        }
        if (Space.isValidPersonalSpaceKey(userHomepageSetting)) {
            Space personalSpace = this.spaceManager.getPersonalSpace(user);
            if (personalSpace != null && userHomepageSetting.equals(personalSpace.getKey())) {
                return Homepage.spaceHomepage(personalSpace);
            }
            return this.defaultSiteHome(user);
        }
        Space space = this.spaceManager.getSpace(userHomepageSetting);
        if (space != null && this.userCanSee(user, space)) {
            return Homepage.spaceHomepage(space);
        }
        return this.defaultSiteHome(user);
    }

    private Homepage defaultSiteHome(@Nullable ConfluenceUser user) {
        String siteHomePageSpaceKey = StringUtils.trimToNull((String)this.settingsManager.getGlobalSettings().getSiteHomePage());
        if (siteHomePageSpaceKey != null) {
            Space siteHomeSpace = this.spaceManager.getSpace(siteHomePageSpaceKey);
            if (siteHomeSpace != null && this.userCanSee(user, siteHomeSpace)) {
                return Homepage.spaceHomepage(siteHomeSpace);
            }
            return Homepage.dashboardHomepage();
        }
        return Homepage.dashboardHomepage();
    }

    private boolean userCanSee(@Nullable ConfluenceUser user, Space space) {
        return this.spacePermissionManager.hasAllPermissions(REQUIRED_SPACE_PERMISSIONS, space, user);
    }
}

