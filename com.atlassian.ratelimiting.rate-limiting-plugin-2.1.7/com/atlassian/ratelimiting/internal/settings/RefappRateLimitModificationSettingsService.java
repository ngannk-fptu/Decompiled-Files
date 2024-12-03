/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent;
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

@RefappComponent
@ExportAsService(value={LifecycleAware.class})
public class RefappRateLimitModificationSettingsService
extends RateLimitModificationSettingsService {
    public RefappRateLimitModificationSettingsService(UserRateLimitSettingsDao userSettingsDao, UserService userService, SystemPropertiesService systemPropertiesService, EventPublisher eventPublisher, ClusterEventService clusterEventService, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService, ConfigurationConstants configurationConstants) {
        super(userSettingsDao, userService, systemPropertiesService, eventPublisher, clusterEventService, licenseChecker, rateLimitingFeatureFlagService, configurationConstants.exemptionsLimit);
    }
}

