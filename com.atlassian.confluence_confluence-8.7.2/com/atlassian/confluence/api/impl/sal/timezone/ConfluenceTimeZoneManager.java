/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.sal.timezone;

import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.TimeZone;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceTimeZoneManager
implements TimeZoneManager {
    private final ConfluenceUserResolver confluenceUserResolver;
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final GlobalSettingsManager settingsManager;

    public ConfluenceTimeZoneManager(ConfluenceUserResolver confluenceUserResolver, UserPreferencesAccessor userPreferencesAccessor, GlobalSettingsManager settingsManager) {
        this.confluenceUserResolver = confluenceUserResolver;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.settingsManager = settingsManager;
    }

    @Deprecated(forRemoval=true)
    public ConfluenceTimeZoneManager(ConfluenceUserResolver confluenceUserResolver, UserPreferencesAccessor userPreferencesAccessor, GlobalSettingsManager settingsManager, TenantRegistry tenantRegistry) {
        this.confluenceUserResolver = confluenceUserResolver;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.settingsManager = settingsManager;
    }

    public @NonNull TimeZone getUserTimeZone() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.getUserTimeZone(user);
    }

    public @NonNull TimeZone getUserTimeZone(@NonNull UserKey userKey) {
        Objects.requireNonNull(userKey);
        ConfluenceUser user = this.confluenceUserResolver.getUserByKey(userKey);
        return this.getUserTimeZone(user);
    }

    private @NonNull TimeZone getUserTimeZone(@Nullable User user) {
        ConfluenceUserPreferences userPreferences = this.userPreferencesAccessor.getConfluenceUserPreferences(user);
        return userPreferences.getTimeZone().getWrappedTimeZone();
    }

    public @NonNull TimeZone getDefaultTimeZone() {
        Settings settings = this.settingsManager.getGlobalSettings();
        String defaultTimeZoneId = settings.getDefaultTimezoneId();
        if (defaultTimeZoneId == null) {
            return TimeZone.getDefault();
        }
        return TimeZone.getTimeZone(defaultTimeZoneId);
    }
}

