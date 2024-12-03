/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.cluster.ClusterEventService;
import com.atlassian.ratelimiting.cluster.RateLimitClusterEvent;
import com.atlassian.ratelimiting.cluster.RateLimitClusterUserEvent;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.DefaultUserRateLimitSettings;
import com.atlassian.ratelimiting.dao.UserRateLimitSettingsDao;
import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchRequest;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchResult;
import com.atlassian.ratelimiting.events.RateLimitingDisabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingDryRunEnabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingEnabledEvent;
import com.atlassian.ratelimiting.events.RateLimitingSettingsReloadedEvent;
import com.atlassian.ratelimiting.events.SystemRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsCreatedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsDeletedEvent;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsModifiedEvent;
import com.atlassian.ratelimiting.exceptions.ExemptionsLimitExceededException;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.settings.RateLimitSettingsUtil;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RateLimitModificationSettingsService
implements DmzRateLimitSettingsModificationService,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(RateLimitModificationSettingsService.class);
    private final UserRateLimitSettingsDao userSettingsDao;
    private final UserService userService;
    private final EventPublisher eventPublisher;
    private final ClusterEventService clusterEventService;
    private final SystemPropertiesService systemPropertiesService;
    private final LicenseChecker licenseChecker;
    private final RateLimitingFeatureFlagService rateLimitingFeatureFlagService;
    private final long exemptionsLimit;

    public RateLimitModificationSettingsService(UserRateLimitSettingsDao userSettingsDao, UserService userService, SystemPropertiesService systemPropertiesService, EventPublisher eventPublisher, ClusterEventService clusterEventService, LicenseChecker licenseChecker, RateLimitingFeatureFlagService rateLimitingFeatureFlagService, long exemptionsLimit) {
        this.userSettingsDao = userSettingsDao;
        this.userService = userService;
        this.systemPropertiesService = systemPropertiesService;
        this.eventPublisher = eventPublisher;
        this.clusterEventService = clusterEventService;
        this.licenseChecker = licenseChecker;
        this.rateLimitingFeatureFlagService = rateLimitingFeatureFlagService;
        this.exemptionsLimit = exemptionsLimit;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void onStart() {
        this.systemPropertiesService.initializeData();
        this.eventPublisher.publish((Object)new RateLimitingSettingsReloadedEvent());
    }

    public void onStop() {
    }

    @Override
    public boolean delete(@Nonnull UserKey userKey) {
        Optional<UserRateLimitSettings> existingUserSettings = this.getUserSettings(userKey);
        if (!existingUserSettings.isPresent()) {
            log.warn("No RL settings found for user: [{}] - can't delete!!", (Object)userKey);
            return false;
        }
        this.userSettingsDao.delete(userKey);
        this.eventPublisher.publish((Object)new UserRateLimitSettingsDeletedEvent(existingUserSettings.get()));
        this.notifyClusterNodesOfUserSettingsChange(userKey);
        return true;
    }

    @Override
    @Nonnull
    public Optional<UserRateLimitSettings> getUserSettings(@Nonnull UserKey userKey) {
        log.debug("Retrieving individual RL settings for user: [{}]", (Object)userKey);
        return this.userSettingsDao.get(Objects.requireNonNull(userKey, "userKey"));
    }

    @Override
    @Nonnull
    public TokenBucketSettings getSystemDefaultSettings() {
        return this.systemPropertiesService.getDefaultRateLimitSettings();
    }

    @Override
    public RateLimitingMode getRateLimitingMode() {
        return RateLimitSettingsUtil.determineRateLimitingMode(this.systemPropertiesService.getRateLimitingMode(), this.licenseChecker, this.rateLimitingFeatureFlagService);
    }

    @Override
    @Nonnull
    public Page<UserRateLimitSettingsSearchResult> searchUserSettings(@Nonnull UserRateLimitSettingsSearchRequest request, @Nonnull PageRequest pageRequest) {
        return this.userSettingsDao.search(Objects.requireNonNull(request, "request"), Objects.requireNonNull(pageRequest, "pageRequest")).map(this::mapSearchResult).filter(Optional::isPresent).map(Optional::get);
    }

    private Optional<UserRateLimitSettingsSearchResult> mapSearchResult(UserRateLimitSettings userRateLimitSettings) {
        Optional<UserProfile> userProfile = this.userService.getUser(userRateLimitSettings.getUserKey());
        if (!userProfile.isPresent()) {
            this.delete(userRateLimitSettings.getUserKey());
        }
        return userProfile.map(up -> new UserRateLimitSettingsSearchResult((UserProfile)up, userRateLimitSettings));
    }

    @Override
    public UserRateLimitSettings whitelistUser(@Nonnull UserKey userKey) {
        Optional<UserRateLimitSettings> oldSettings = this.userSettingsDao.get(userKey);
        if (oldSettings.isPresent() && oldSettings.get().isWhitelisted()) {
            log.debug("User with key [{}] already whitelisted", (Object)userKey);
            return oldSettings.get();
        }
        DefaultUserRateLimitSettings newSettings = DefaultUserRateLimitSettings.builder(userKey).whitelisted().build();
        if (!oldSettings.isPresent()) {
            this.ensureNewExemptionCanBeAdded();
        }
        return this.saveAndPublishUpdatedUserSettings(userKey, oldSettings, newSettings);
    }

    @Override
    public UserRateLimitSettings blacklistUser(@Nonnull UserKey userKey) {
        Optional<UserRateLimitSettings> oldSettings = this.userSettingsDao.get(userKey);
        if (oldSettings.isPresent() && oldSettings.get().isBlacklisted()) {
            log.debug("User with key [{}] already blacklisted", (Object)userKey);
            return oldSettings.get();
        }
        DefaultUserRateLimitSettings newSettings = DefaultUserRateLimitSettings.builder(userKey).blacklisted().build();
        if (!oldSettings.isPresent()) {
            this.ensureNewExemptionCanBeAdded();
        }
        return this.saveAndPublishUpdatedUserSettings(userKey, oldSettings, newSettings);
    }

    private UserRateLimitSettings saveAndPublishUpdatedUserSettings(UserKey userKey, Optional<UserRateLimitSettings> existing, UserRateLimitSettings newSettings) {
        UserRateLimitSettings persisted = this.userSettingsDao.saveOrUpdate(newSettings);
        this.logAuditEntry(existing, persisted);
        this.notifyClusterNodesOfUserSettingsChange(userKey);
        return persisted;
    }

    private void logAuditEntry(Optional<UserRateLimitSettings> origSettings, UserRateLimitSettings newSettings) {
        if (origSettings.isPresent()) {
            DefaultUserRateLimitSettings oldSettings = ((DefaultUserRateLimitSettings)origSettings.get()).copy().build();
            if (!Objects.equals(oldSettings, newSettings)) {
                this.eventPublisher.publish((Object)new UserRateLimitSettingsModifiedEvent(newSettings, oldSettings));
            }
        } else {
            this.eventPublisher.publish((Object)new UserRateLimitSettingsCreatedEvent(newSettings));
        }
    }

    @Override
    public UserRateLimitSettings updateUserSettings(@Nonnull UserKey userKey, @Nonnull TokenBucketSettings bucketSettings) {
        Optional<UserRateLimitSettings> existing = this.getUserSettings(userKey);
        if (!this.userSettingsHaveChanged(existing, bucketSettings)) {
            log.debug("User settings have not changed: (userKey=[{}], settings=[{}])", (Object)userKey, (Object)bucketSettings);
            return existing.orElse(null);
        }
        if (!existing.isPresent()) {
            this.ensureNewExemptionCanBeAdded();
        }
        DefaultUserRateLimitSettings newSettings = DefaultUserRateLimitSettings.builder(userKey).withSettings(bucketSettings).build();
        return this.saveAndPublishUpdatedUserSettings(userKey, existing, newSettings);
    }

    private void ensureNewExemptionCanBeAdded() {
        if (this.atOrOverExemptionsLimit()) {
            throw new ExemptionsLimitExceededException(String.format("Cannot add another user exemption. Already reached the exemptions limit (%d).", this.getExemptionsLimit()));
        }
    }

    @Override
    public long getExemptionsLimit() {
        return this.exemptionsLimit;
    }

    @Override
    public long getExemptionsCount() {
        return this.userSettingsDao.getExemptionsCount();
    }

    private boolean atOrOverExemptionsLimit() {
        return this.getExemptionsCount() >= this.getExemptionsLimit();
    }

    private boolean userSettingsHaveChanged(Optional<UserRateLimitSettings> existing, TokenBucketSettings bucketSettings) {
        if (existing.isPresent()) {
            DefaultUserRateLimitSettings oldSettings = (DefaultUserRateLimitSettings)existing.get();
            Optional<TokenBucketSettings> oldTokenBucketSettings = oldSettings.getSettings();
            return !oldTokenBucketSettings.isPresent() || !Objects.equals(bucketSettings, oldTokenBucketSettings.get());
        }
        return true;
    }

    @Override
    public void updateSystemDefaultSettings(SystemRateLimitingSettings newSystemRateLimitingSettings) {
        SystemRateLimitingSettings existingSystemSettings = this.systemPropertiesService.getSystemSettings();
        boolean rateLimitingModeChanged = this.hasRateLimitingModeChanged(newSystemRateLimitingSettings, existingSystemSettings);
        boolean systemBucketSettingsChanged = this.hasSystemBucketSettingsChanged(newSystemRateLimitingSettings, existingSystemSettings);
        if (rateLimitingModeChanged || systemBucketSettingsChanged) {
            this.systemPropertiesService.updateSystemRateLimitSettings(newSystemRateLimitingSettings);
            this.notifyClusterNodesOfGlobalChange();
        }
    }

    private boolean hasSystemBucketSettingsChanged(SystemRateLimitingSettings newSystemSettings, SystemRateLimitingSettings existingSystemSettings) {
        if (!Objects.equals(newSystemSettings.getBucketSettings(), existingSystemSettings.getBucketSettings())) {
            log.warn("Default rate limit settings updated to: [{}]", (Object)newSystemSettings);
            this.eventPublisher.publish((Object)new SystemRateLimitSettingsModifiedEvent(newSystemSettings.getBucketSettings(), existingSystemSettings.getBucketSettings()));
            return true;
        }
        return false;
    }

    private boolean hasRateLimitingModeChanged(SystemRateLimitingSettings newSystemSettings, SystemRateLimitingSettings existingSystemSettings) {
        RateLimitingMode newMode = newSystemSettings.getMode();
        if (!newMode.equals((Object)existingSystemSettings.getMode())) {
            log.warn("Switched rate limiting to: [{}]!", (Object)newMode);
            this.eventPublisher.publish(this.createModeChangeEvent(newMode));
            return true;
        }
        return false;
    }

    private Object createModeChangeEvent(RateLimitingMode newMode) {
        if (newMode.isDryRun()) {
            return new RateLimitingDryRunEnabledEvent();
        }
        return newMode.isEnabled() ? new RateLimitingEnabledEvent() : new RateLimitingDisabledEvent();
    }

    @Override
    public SystemJobControlSettings updateJobControlSettings(SystemJobControlSettings systemJobControlSettings) {
        log.warn("Switched rate limiting job controls to: [{}]!", (Object)systemJobControlSettings);
        return this.systemPropertiesService.updateSystemJobControlSettings(systemJobControlSettings);
    }

    private void notifyClusterNodesOfUserSettingsChange(UserKey userKey) {
        log.info("Notifying cluster of changes to user RL settings: [{}]", (Object)userKey);
        this.clusterEventService.publishRateLimitingClusterEvent(new RateLimitClusterUserEvent(userKey));
    }

    private void notifyClusterNodesOfGlobalChange() {
        log.info("Notifying cluster of global RL settings");
        this.clusterEventService.publishRateLimitingClusterEvent(new RateLimitClusterEvent());
    }
}

