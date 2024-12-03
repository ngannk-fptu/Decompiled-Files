/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api.features;

import com.atlassian.sal.api.features.EnabledDarkFeatures;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DarkFeatureManager {
    public static final String ATLASSIAN_DARKFEATURE_PREFIX = "atlassian.darkfeature.";
    public static final String DISABLE_ALL_DARKFEATURES_PROPERTY = "atlassian.darkfeature.disabled";
    public static final String DARKFEATURES_PROPERTIES_FILE_PROPERTY = "darkfeatures.properties.file";
    public static final String DARKFEATURES_PROPERTIES_FILE_PROPERTY_DEFAULT = "atlassian-darkfeatures.properties";

    @Nonnull
    public Optional<Boolean> isEnabledForAllUsers(@Nonnull String var1);

    @Nonnull
    public Optional<Boolean> isEnabledForCurrentUser(@Nonnull String var1);

    @Nonnull
    public Optional<Boolean> isEnabledForUser(@Nullable UserKey var1, @Nonnull String var2);

    @Deprecated
    public boolean isFeatureEnabledForAllUsers(String var1);

    @Deprecated
    public boolean isFeatureEnabledForCurrentUser(String var1);

    @Deprecated
    public boolean isFeatureEnabledForUser(@Nullable UserKey var1, String var2);

    public boolean canManageFeaturesForAllUsers();

    public void enableFeatureForAllUsers(String var1);

    public void disableFeatureForAllUsers(String var1);

    public void enableFeatureForCurrentUser(String var1);

    public void enableFeatureForUser(UserKey var1, String var2);

    public void disableFeatureForCurrentUser(String var1);

    public void disableFeatureForUser(UserKey var1, String var2);

    public EnabledDarkFeatures getFeaturesEnabledForAllUsers();

    public EnabledDarkFeatures getFeaturesEnabledForCurrentUser();

    public EnabledDarkFeatures getFeaturesEnabledForUser(@Nullable UserKey var1);
}

