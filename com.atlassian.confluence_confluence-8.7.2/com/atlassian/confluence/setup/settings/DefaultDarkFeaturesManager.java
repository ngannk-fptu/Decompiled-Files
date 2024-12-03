/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.impl.feature.SiteDarkFeaturesDao;
import com.atlassian.confluence.setup.settings.BuildNumberActivatedDarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.FeatureService;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.confluence.setup.settings.VacantDarkFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public class DefaultDarkFeaturesManager
implements DarkFeaturesManager {
    private static final String USER_DARKFEATURE_PREFERENCE_KEY = "user.features.enabled";
    private static final boolean DARK_FEATURES_DISABLED_SYSTEM_WIDE = Boolean.getBoolean("atlassian.darkfeature.disabled");
    private final EventPublisher eventPublisher;
    private final SiteDarkFeaturesDao siteDarkFeaturesDao;
    private final BuildNumberActivatedDarkFeatures buildNumberActivatedDarkFeatures;
    private final Function<ConfluenceUser, UserPreferences> userPrefsLookup;

    DefaultDarkFeaturesManager(UserPreferencesAccessor userPreferencesAccessor, EventPublisher eventPublisher, SiteDarkFeaturesDao siteDarkFeaturesDao, BuildNumberActivatedDarkFeatures buildNumberActivatedDarkFeatures) {
        this.eventPublisher = eventPublisher;
        this.siteDarkFeaturesDao = siteDarkFeaturesDao;
        this.buildNumberActivatedDarkFeatures = buildNumberActivatedDarkFeatures;
        this.userPrefsLookup = user -> DefaultDarkFeaturesManager.getUserPreferences(userPreferencesAccessor, user);
    }

    public DefaultDarkFeaturesManager(UserPreferencesAccessor userPreferencesAccessor, FeatureService ignored, EventPublisher eventPublisher, SiteDarkFeaturesDao siteDarkFeaturesDao, BuildNumberActivatedDarkFeatures buildNumberActivatedDarkFeatures) {
        this(userPreferencesAccessor, eventPublisher, siteDarkFeaturesDao, buildNumberActivatedDarkFeatures);
    }

    @Deprecated
    public DefaultDarkFeaturesManager(UserAccessor userAccessor, FeatureService ignored, EventPublisher eventPublisher, SiteDarkFeaturesDao siteDarkFeaturesDao, BuildNumberActivatedDarkFeatures buildNumberActivatedDarkFeatures) {
        this.eventPublisher = eventPublisher;
        this.siteDarkFeaturesDao = siteDarkFeaturesDao;
        this.buildNumberActivatedDarkFeatures = buildNumberActivatedDarkFeatures;
        this.userPrefsLookup = user -> DefaultDarkFeaturesManager.getUserPreferences(userAccessor, user);
    }

    private Set<String> getUserEnabledFeatures(ConfluenceUser user) {
        String features;
        try {
            features = this.getUserPreferences(user).getString(USER_DARKFEATURE_PREFERENCE_KEY);
        }
        catch (NullPointerException e) {
            features = "";
        }
        return DefaultDarkFeaturesManager.deserialize(features);
    }

    private UserPreferences getUserPreferences(ConfluenceUser user) {
        return this.userPrefsLookup.apply(user);
    }

    private static UserPreferences getUserPreferences(UserPreferencesAccessor userPreferencesAccessor, ConfluenceUser user) {
        return userPreferencesAccessor.getConfluenceUserPreferences(user).getWrappedPreferences();
    }

    private static UserPreferences getUserPreferences(UserAccessor userAccessor, ConfluenceUser user) {
        PropertySet properties = userAccessor.getPropertySet(user);
        return new UserPreferences(properties);
    }

    private Set<String> getSystemEnabledFeatures() {
        return this.getSystemEnabledFeatures(VacantDarkFeaturesManager.ONLY_SYSTEM_FEATURES.getSystemEnabledFeatures(), VacantDarkFeaturesManager.SYSTEM_DISABLED_FEATURES);
    }

    @VisibleForTesting
    Set<String> getSystemEnabledFeatures(Set<String> systemEnabledFeatures, Set<String> systemDisabledFeatures) {
        return Sets.union(systemEnabledFeatures, (Set)Sets.difference(this.buildNumberActivatedDarkFeatures.getActivatedDarkFeatures(), systemDisabledFeatures).immutableCopy());
    }

    private Set<String> getSiteEnabledFeatures() {
        return this.siteDarkFeaturesDao.getSiteEnabledFeatures();
    }

    @Override
    public DarkFeatures getDarkFeatures() {
        return this.getDarkFeatures(this.getCurrentUser());
    }

    @Override
    public DarkFeatures getDarkFeaturesAllUsers() {
        if (DARK_FEATURES_DISABLED_SYSTEM_WIDE) {
            return VacantDarkFeaturesManager.NO_FEATURES;
        }
        return new DarkFeatures(this.getSystemEnabledFeatures(), this.getSiteEnabledFeatures(), Collections.emptySet());
    }

    @Override
    public DarkFeatures getSiteDarkFeatures() {
        return this.getDarkFeatures(null);
    }

    @Override
    public DarkFeatures getDarkFeatures(ConfluenceUser user) {
        if (DARK_FEATURES_DISABLED_SYSTEM_WIDE) {
            return VacantDarkFeaturesManager.NO_FEATURES;
        }
        Set<String> systemEnabledFeatures = this.getSystemEnabledFeatures();
        Set<String> siteEnabledFeatures = this.getSiteEnabledFeatures();
        Set<String> userEnabledFeatures = this.getUserEnabledFeatures(user);
        return new DarkFeatures(systemEnabledFeatures, siteEnabledFeatures, userEnabledFeatures);
    }

    private ConfluenceUser getCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    @Override
    public synchronized void enableUserFeature(String featureKey) throws UnknownFeatureException {
        ConfluenceUser user = this.getCurrentUser();
        this.enableUserFeature(user, featureKey);
    }

    @Override
    public synchronized void enableUserFeature(ConfluenceUser user, String featureKey) {
        UserPreferences prefs = this.getUserPreferences(user);
        HashSet enabledFeatures = Sets.newHashSet(this.getUserEnabledFeatures(user));
        if (enabledFeatures.contains(featureKey)) {
            return;
        }
        enabledFeatures.add(featureKey);
        try {
            prefs.setString(USER_DARKFEATURE_PREFERENCE_KEY, DefaultDarkFeaturesManager.serialize(enabledFeatures));
        }
        catch (AtlassianCoreException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void disableUserFeature(String featureKey) throws UnknownFeatureException {
        ConfluenceUser user = this.getCurrentUser();
        this.disableUserFeature(user, featureKey);
    }

    @Override
    public synchronized void disableUserFeature(ConfluenceUser user, String featureKey) {
        UserPreferences prefs = this.getUserPreferences(user);
        HashSet enabledFeatures = Sets.newHashSet(this.getUserEnabledFeatures(user));
        if (!enabledFeatures.contains(featureKey)) {
            return;
        }
        enabledFeatures.remove(featureKey);
        try {
            prefs.setString(USER_DARKFEATURE_PREFERENCE_KEY, DefaultDarkFeaturesManager.serialize(enabledFeatures));
        }
        catch (AtlassianCoreException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void enableSiteFeature(String featureKey) throws UnknownFeatureException {
        boolean enabled = this.siteDarkFeaturesDao.enableSiteFeature(featureKey);
        if (enabled) {
            this.eventPublisher.publish((Object)new SiteDarkFeatureEnabledEvent(this, featureKey));
        }
    }

    @Override
    public synchronized void disableSiteFeature(String featureKey) throws UnknownFeatureException {
        boolean disabled = this.siteDarkFeaturesDao.disableSiteFeature(featureKey);
        if (disabled) {
            this.eventPublisher.publish((Object)new SiteDarkFeatureDisabledEvent(this, featureKey));
        }
    }

    private static String serialize(Set<String> features) {
        return StringUtils.join(features, (String)",");
    }

    private static Set<String> deserialize(String features) {
        if (StringUtils.isBlank((CharSequence)features)) {
            return new HashSet<String>();
        }
        return Sets.newHashSet((Object[])features.split(","));
    }
}

