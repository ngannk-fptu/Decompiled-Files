/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.DefaultClock
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.util.Clock
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.extras.aggregation.impl;

import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.util.Clock;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregationWarningManager {
    private static final String ACKNOWLEDGED_KEY = "ack";
    private static final String HAD_HAD_A_SINGLE_DIRECTORY_KEY = "hadHadASingleDirectory";
    @VisibleForTesting
    static final String FIRST_CHECK_DATE_MS_KEY = "firstCheckDateMs";
    private static final String NAMESPACE_KEY = "c.a.c.plugins:confluence-user-rest:";
    private static final Logger log = LoggerFactory.getLogger(AggregationWarningManager.class);
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final CrowdDirectoryService crowdDirectoryService;
    private final DarkFeaturesManager darkFeaturesManager;
    private Clock clock = new DefaultClock();

    public AggregationWarningManager(UserAccessor userAccessor, PermissionManager permissionManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, CrowdDirectoryService crowdDirectoryService, DarkFeaturesManager darkFeaturesManager) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.crowdDirectoryService = crowdDirectoryService;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @VisibleForTesting
    void setClock(Clock clock) {
        this.clock = clock;
    }

    public boolean shouldShow(String username) {
        return (Boolean)this.transactionTemplate.execute(() -> this.executeShouldShow(username));
    }

    private boolean executeShouldShow(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            return false;
        }
        if (!this.permissionManager.isSystemAdministrator((User)user)) {
            return false;
        }
        if (this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("always-show-aggregation-warning")) {
            return true;
        }
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        if (this.isThirtyDaysAfterFirstCheck(pluginSettings)) {
            return false;
        }
        if (!this.hasMultipleEnabledDirectories(pluginSettings)) {
            return false;
        }
        return !this.isUserAcknowledged(user);
    }

    private boolean hasMultipleEnabledDirectories(PluginSettings pluginSettings) {
        String singleDirectoryStr = (String)pluginSettings.get(this.namespaceKey(HAD_HAD_A_SINGLE_DIRECTORY_KEY));
        if (singleDirectoryStr == null || !Boolean.parseBoolean(singleDirectoryStr)) {
            int activeDirectoryCount = 0;
            for (Directory directory : this.crowdDirectoryService.findAllDirectories()) {
                if (!directory.isActive() || ++activeDirectoryCount < 2) continue;
                return true;
            }
            if (activeDirectoryCount == 1) {
                pluginSettings.put(this.namespaceKey(HAD_HAD_A_SINGLE_DIRECTORY_KEY), (Object)Boolean.TRUE.toString());
            }
            return false;
        }
        return false;
    }

    private boolean isThirtyDaysAfterFirstCheck(PluginSettings pluginSettings) {
        DateTime now = new DateTime((Object)this.clock.getCurrentDate());
        String firstCheckStr = (String)pluginSettings.get(this.namespaceKey(FIRST_CHECK_DATE_MS_KEY));
        if (firstCheckStr == null) {
            pluginSettings.put(this.namespaceKey(FIRST_CHECK_DATE_MS_KEY), (Object)Long.toString(now.toDate().getTime()));
            return false;
        }
        long firstCheck = Long.parseLong(firstCheckStr);
        return firstCheck <= now.minusDays(30).toDate().getTime();
    }

    public ValidationResult setAcknowledged(ConfluenceUser user) {
        return (ValidationResult)this.transactionTemplate.execute(() -> this.executeSetAcknowledged(user));
    }

    private ValidationResult executeSetAcknowledged(ConfluenceUser user) {
        if (!this.permissionManager.isSystemAdministrator((User)user)) {
            return SimpleValidationResult.builder().authorized(false).build();
        }
        try {
            this.userAccessor.getUserPreferences((User)user).setLong(this.namespaceKey(ACKNOWLEDGED_KEY), System.currentTimeMillis());
            return SimpleValidationResult.builder().authorized(true).build();
        }
        catch (AtlassianCoreException ex) {
            log.error("Could not acknowledge directory warning notice, admins may be seeing excessive notifications about aggregation of user directories", (Throwable)ex);
            return SimpleValidationResult.builder().authorized(true).addError(ex.getMessage(), new Object[0]).build();
        }
    }

    private String namespaceKey(String key) {
        return NAMESPACE_KEY + key;
    }

    public boolean isUserAcknowledged(ConfluenceUser user) {
        return this.userAccessor.getUserPreferences((User)user).getLong(this.namespaceKey(ACKNOWLEDGED_KEY)) > 0L;
    }
}

