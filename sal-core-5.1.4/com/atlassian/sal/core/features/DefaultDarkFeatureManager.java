/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.features.EnabledDarkFeatures
 *  com.atlassian.sal.api.features.EnabledDarkFeaturesBuilder
 *  com.atlassian.sal.api.features.MissingPermissionException
 *  com.atlassian.sal.api.features.SiteDarkFeaturesStorage
 *  com.atlassian.sal.api.features.ValidFeatureKeyPredicate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.core.features;

import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.features.EnabledDarkFeaturesBuilder;
import com.atlassian.sal.api.features.MissingPermissionException;
import com.atlassian.sal.api.features.SiteDarkFeaturesStorage;
import com.atlassian.sal.api.features.ValidFeatureKeyPredicate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.core.features.SystemDarkFeatureInitializer;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultDarkFeatureManager
implements DarkFeatureManager {
    private final UserManager userManager;
    private final SiteDarkFeaturesStorage siteDarkFeaturesStorage;
    private final SystemDarkFeatureInitializer.SystemDarkFeatures systemDarkFeatures;

    public DefaultDarkFeatureManager(UserManager userManager, SiteDarkFeaturesStorage siteDarkFeaturesStorage) {
        this.userManager = userManager;
        this.siteDarkFeaturesStorage = siteDarkFeaturesStorage;
        this.systemDarkFeatures = SystemDarkFeatureInitializer.getSystemStartupDarkFeatures();
    }

    @Nonnull
    public Optional<Boolean> isEnabledForAllUsers(@Nonnull String featureKey) {
        if (!ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey)) {
            return Optional.empty();
        }
        if (this.systemDarkFeatures.getEnabled().contains(featureKey) || this.siteDarkFeaturesStorage.contains(featureKey)) {
            return Optional.of(Boolean.TRUE);
        }
        if (this.systemDarkFeatures.getDisabled().contains(featureKey)) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    @Nonnull
    public Optional<Boolean> isEnabledForCurrentUser(@Nonnull String featureKey) {
        return this.isEnabledForAllUsers(featureKey);
    }

    @Nonnull
    public Optional<Boolean> isEnabledForUser(@Nullable UserKey userKey, @Nonnull String featureKey) {
        if (this.isUserAnonymous(userKey) || this.isUserExisting(userKey)) {
            return this.isEnabledForAllUsers(featureKey);
        }
        throw new IllegalArgumentException("The user does not exist");
    }

    @Deprecated
    public boolean isFeatureEnabledForAllUsers(String featureKey) {
        return ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey) && this.getFeaturesEnabledForAllUsers().isFeatureEnabled(featureKey);
    }

    @Deprecated
    public boolean isFeatureEnabledForCurrentUser(String featureKey) {
        return this.isFeatureEnabledForAllUsers(featureKey);
    }

    @Deprecated
    public boolean isFeatureEnabledForUser(@Nullable UserKey userKey, String featureKey) {
        if (this.isUserAnonymous(userKey) || this.isUserExisting(userKey)) {
            return this.isFeatureEnabledForAllUsers(featureKey);
        }
        throw new IllegalArgumentException("The user does not exist");
    }

    public boolean canManageFeaturesForAllUsers() {
        try {
            UserKey remoteUserKey = this.userManager.getRemoteUserKey();
            return this.userManager.isSystemAdmin(remoteUserKey);
        }
        catch (RuntimeException e) {
            return false;
        }
    }

    public void enableFeatureForAllUsers(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        this.checkCurrentUserCanManageFeaturesForAllUsers();
        this.siteDarkFeaturesStorage.enable(featureKey);
    }

    public void disableFeatureForAllUsers(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        this.checkCurrentUserCanManageFeaturesForAllUsers();
        this.siteDarkFeaturesStorage.disable(featureKey);
    }

    public void enableFeatureForCurrentUser(String featureKey) {
        this.throwUnsupportedPerUserOperationException();
    }

    public void enableFeatureForUser(UserKey userKey, String featureKey) {
        this.throwUnsupportedPerUserOperationException();
    }

    public void disableFeatureForCurrentUser(String featureKey) {
        this.throwUnsupportedPerUserOperationException();
    }

    public void disableFeatureForUser(UserKey userKey, String featureKey) {
        this.throwUnsupportedPerUserOperationException();
    }

    public EnabledDarkFeatures getFeaturesEnabledForAllUsers() {
        if (this.systemDarkFeatures.isDisableAll()) {
            return EnabledDarkFeatures.NONE;
        }
        return new EnabledDarkFeaturesBuilder().unmodifiableFeaturesEnabledForAllUsers(this.systemDarkFeatures.getEnabled()).featuresEnabledForAllUsers(this.siteDarkFeaturesStorage.getEnabledDarkFeatureSet()).build();
    }

    public EnabledDarkFeatures getFeaturesEnabledForCurrentUser() {
        return this.getFeaturesEnabledForAllUsers();
    }

    public EnabledDarkFeatures getFeaturesEnabledForUser(@Nullable UserKey userKey) {
        if (this.isUserAnonymous(userKey) || this.isUserExisting(userKey)) {
            return this.getFeaturesEnabledForAllUsers();
        }
        throw new IllegalArgumentException("The user does not exist");
    }

    private boolean isUserExisting(@Nullable UserKey userKey) {
        return userKey != null && this.userManager.getUserProfile(userKey) != null;
    }

    private boolean isUserAnonymous(@Nullable UserKey userKey) {
        return userKey == null;
    }

    private void checkCurrentUserCanManageFeaturesForAllUsers() {
        if (!this.canManageFeaturesForAllUsers()) {
            throw new MissingPermissionException("The current user is not allowed to change dark features affecting all users.");
        }
    }

    private void throwUnsupportedPerUserOperationException() {
        throw new UnsupportedOperationException("The default implementation doesn't support per-user dark features.");
    }
}

