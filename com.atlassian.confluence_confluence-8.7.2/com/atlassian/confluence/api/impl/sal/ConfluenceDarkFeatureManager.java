/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.features.EnabledDarkFeatures
 *  com.atlassian.sal.api.features.EnabledDarkFeaturesBuilder
 *  com.atlassian.sal.api.features.InvalidFeatureKeyException
 *  com.atlassian.sal.api.features.MissingPermissionException
 *  com.atlassian.sal.api.features.ValidFeatureKeyPredicate
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.features.EnabledDarkFeaturesBuilder;
import com.atlassian.sal.api.features.InvalidFeatureKeyException;
import com.atlassian.sal.api.features.MissingPermissionException;
import com.atlassian.sal.api.features.ValidFeatureKeyPredicate;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceDarkFeatureManager
implements DarkFeatureManager {
    private final DarkFeaturesManager darkFeaturesManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PermissionManager permissionManager;

    public ConfluenceDarkFeatureManager(DarkFeaturesManager darkFeaturesManager, ConfluenceUserResolver confluenceUserResolver, PermissionManager permissionManager) {
        this.darkFeaturesManager = darkFeaturesManager;
        this.confluenceUserResolver = confluenceUserResolver;
        this.permissionManager = permissionManager;
    }

    public @NonNull Optional<Boolean> isEnabledForAllUsers(@NonNull String featureKey) {
        if (!ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey)) {
            return Optional.empty();
        }
        return Optional.of(this.darkFeaturesManager.getDarkFeaturesAllUsers().isFeatureEnabled(featureKey));
    }

    public @NonNull Optional<Boolean> isEnabledForCurrentUser(@NonNull String featureKey) {
        if (!ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey)) {
            return Optional.empty();
        }
        return Optional.of(this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(featureKey));
    }

    public @NonNull Optional<Boolean> isEnabledForUser(@Nullable UserKey userKey, @NonNull String featureKey) {
        if (userKey == null) {
            return this.isEnabledForAllUsers(featureKey);
        }
        if (!ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey)) {
            return Optional.empty();
        }
        ConfluenceUser user = this.getUserByUserKey(userKey);
        return Optional.of(this.darkFeaturesManager.getDarkFeatures(user).isFeatureEnabled(featureKey));
    }

    public boolean isFeatureEnabledForAllUsers(String featureKey) {
        return ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey) && this.darkFeaturesManager.getDarkFeaturesAllUsers().isFeatureEnabled(featureKey);
    }

    public boolean isFeatureEnabledForCurrentUser(String featureKey) {
        return ValidFeatureKeyPredicate.isValidFeatureKey((String)featureKey) && this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(featureKey);
    }

    public boolean isFeatureEnabledForUser(@Nullable UserKey userKey, String featureKey) {
        return this.isEnabledForUser(userKey, featureKey).orElse(false);
    }

    public boolean canManageFeaturesForAllUsers() {
        return this.permissionManager.isConfluenceAdministrator(AuthenticatedUserThreadLocal.get());
    }

    public void enableFeatureForAllUsers(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        if (!this.canManageFeaturesForAllUsers()) {
            throw new MissingPermissionException("Current User does not have permission to enable feature for all users.");
        }
        try {
            this.darkFeaturesManager.enableSiteFeature(featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public void disableFeatureForAllUsers(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        if (!this.canManageFeaturesForAllUsers()) {
            throw new MissingPermissionException("Current User does not have permission to disable feature for all users.");
        }
        try {
            this.darkFeaturesManager.disableSiteFeature(featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public void enableFeatureForCurrentUser(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            throw new IllegalStateException("Unable to enable Feature for anonymous User");
        }
        try {
            this.darkFeaturesManager.enableUserFeature(user, featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public void enableFeatureForUser(UserKey userKey, String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        ConfluenceUser user = this.getUserByUserKey(userKey);
        try {
            this.darkFeaturesManager.enableUserFeature(user, featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public void disableFeatureForCurrentUser(String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            throw new IllegalStateException("Unable to disable Feature for anonymous User");
        }
        try {
            this.darkFeaturesManager.disableUserFeature(user, featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public void disableFeatureForUser(UserKey userKey, String featureKey) {
        ValidFeatureKeyPredicate.checkFeatureKey((String)featureKey);
        ConfluenceUser user = this.getUserByUserKey(userKey);
        try {
            this.darkFeaturesManager.disableUserFeature(user, featureKey);
        }
        catch (UnknownFeatureException e) {
            throw new InvalidFeatureKeyException(e.getMessage());
        }
    }

    public EnabledDarkFeatures getFeaturesEnabledForAllUsers() {
        return this.convertToEnabledDarkFeatures(this.darkFeaturesManager.getDarkFeaturesAllUsers());
    }

    private EnabledDarkFeatures convertToEnabledDarkFeatures(DarkFeatures darkFeatures) {
        return new EnabledDarkFeaturesBuilder().unmodifiableFeaturesEnabledForAllUsers(darkFeatures.getSystemEnabledFeatures()).featuresEnabledForAllUsers(darkFeatures.getSiteEnabledFeatures()).featuresEnabledForCurrentUser(darkFeatures.getUserEnabledFeatures()).build();
    }

    public EnabledDarkFeatures getFeaturesEnabledForCurrentUser() {
        return this.convertToEnabledDarkFeatures(this.darkFeaturesManager.getDarkFeatures());
    }

    public EnabledDarkFeatures getFeaturesEnabledForUser(@Nullable UserKey userKey) {
        ConfluenceUser user = this.getUserByUserKey(userKey);
        return this.convertToEnabledDarkFeatures(this.darkFeaturesManager.getDarkFeatures(user));
    }

    private ConfluenceUser getUserByUserKey(UserKey userKey) {
        if (userKey == null) {
            return null;
        }
        ConfluenceUser user = this.confluenceUserResolver.getExistingUserByKey(userKey);
        if (user == null) {
            throw new IllegalArgumentException(String.format("Unable to find User for User Key %s", userKey));
        }
        return user;
    }
}

