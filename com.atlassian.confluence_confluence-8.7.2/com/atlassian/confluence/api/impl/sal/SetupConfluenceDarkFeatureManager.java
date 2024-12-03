/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.features.EnabledDarkFeatures
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SetupConfluenceDarkFeatureManager
implements DarkFeatureManager {
    public @NonNull Optional<Boolean> isEnabledForAllUsers(@NonNull String featureKey) {
        return Optional.of(false);
    }

    public @NonNull Optional<Boolean> isEnabledForCurrentUser(@NonNull String featureKey) {
        return Optional.of(false);
    }

    public @NonNull Optional<Boolean> isEnabledForUser(@Nullable UserKey userKey, @NonNull String featureKey) {
        return Optional.of(false);
    }

    public boolean isFeatureEnabledForAllUsers(String s) {
        return false;
    }

    public boolean isFeatureEnabledForCurrentUser(String s) {
        return false;
    }

    public boolean isFeatureEnabledForUser(UserKey userKey, String s) {
        return false;
    }

    public boolean canManageFeaturesForAllUsers() {
        return false;
    }

    public void enableFeatureForAllUsers(String s) {
    }

    public void disableFeatureForAllUsers(String s) {
    }

    public void enableFeatureForCurrentUser(String s) {
    }

    public void enableFeatureForUser(UserKey userKey, String s) {
    }

    public void disableFeatureForCurrentUser(String s) {
    }

    public void disableFeatureForUser(UserKey userKey, String s) {
    }

    public EnabledDarkFeatures getFeaturesEnabledForAllUsers() {
        return new EnabledDarkFeatures(ImmutableMap.of());
    }

    public EnabledDarkFeatures getFeaturesEnabledForCurrentUser() {
        return new EnabledDarkFeatures(ImmutableMap.of());
    }

    public EnabledDarkFeatures getFeaturesEnabledForUser(UserKey userKey) {
        return new EnabledDarkFeatures(ImmutableMap.of());
    }
}

