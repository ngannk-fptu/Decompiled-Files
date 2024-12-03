/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.user.UserUpdatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.crowd.event.user.UserUpdatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.ratelimiting.cluster.ClusterEventService;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.UserRateLimitSettingsDao;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.ConfigurationConstants;
import com.atlassian.ratelimiting.internal.settings.RateLimitModificationSettingsService;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

@BitbucketComponent
@ConfluenceComponent
@JiraComponent
@BambooComponent
@ExportAsService(value={LifecycleAware.class})
public class DefaultRateLimitModificationSettingsService
extends RateLimitModificationSettingsService {
    private final UserService userService;

    public DefaultRateLimitModificationSettingsService(UserRateLimitSettingsDao userSettingsDao, UserService userService, SystemPropertiesService systemPropertiesService, EventPublisher eventPublisher, ClusterEventService clusterEventService, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService, ConfigurationConstants configurationConstants) {
        super(userSettingsDao, userService, systemPropertiesService, eventPublisher, clusterEventService, licenseChecker, rateLimitingFeatureFlagService, configurationConstants.exemptionsLimit);
        this.userService = userService;
    }

    @EventListener
    public void onUserUpdated(UserUpdatedEvent event) {
        if (!event.getUser().isActive()) {
            this.userService.getUser(event.getUser().getName()).ifPresent(userProfile -> this.delete(userProfile.getUserKey()));
        }
    }
}

