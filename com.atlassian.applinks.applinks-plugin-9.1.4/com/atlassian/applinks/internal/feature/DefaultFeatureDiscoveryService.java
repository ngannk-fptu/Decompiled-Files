/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsBuilder
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.feature;

import com.atlassian.applinks.internal.common.exception.InvalidFeatureKeyException;
import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.feature.FeatureDiscoveryService;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultFeatureDiscoveryService
implements FeatureDiscoveryService {
    private static final int FEATURE_KEY_MAX_LENGTH = 100;
    private static final Pattern VALID_FEATURE_KEY = Pattern.compile("[\\w\\.\\-]+");
    private static final String FEATURE_DISCOVERY_PREFIX = "applinks.featurediscovery.";
    private static final Predicate<String> IS_FEATURE_DISCOVERY_KEY = key -> key.startsWith(FEATURE_DISCOVERY_PREFIX);
    private static final Function<String, String> REMOVE_FEATURE_DISCOVERY_PREFIX = key -> key.substring(FEATURE_DISCOVERY_PREFIX.length());
    private final PermissionValidationService permissionValidationService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final UserManager userManager;
    private final UserSettingsService userSettingsService;

    @Autowired
    public DefaultFeatureDiscoveryService(PermissionValidationService permissionValidationService, ServiceExceptionFactory serviceExceptionFactory, UserManager userManager, UserSettingsService userSettingsService) {
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.userManager = userManager;
        this.userSettingsService = userSettingsService;
        this.permissionValidationService = permissionValidationService;
    }

    @Override
    public boolean isDiscovered(@Nonnull String featureKey) throws NotAuthenticatedException, InvalidFeatureKeyException {
        Objects.requireNonNull(featureKey, "featureKey");
        this.permissionValidationService.validateAuthenticated();
        this.validateFeatureKey(featureKey);
        return this.getAllKeys().contains(FEATURE_DISCOVERY_PREFIX + featureKey.toLowerCase());
    }

    @Override
    public Set<String> getAllDiscoveredFeatureKeys() throws NotAuthenticatedException {
        this.permissionValidationService.validateAuthenticated();
        return this.getDiscoveredFeatureKeys();
    }

    @Override
    public void discover(@Nonnull String featureKey) throws NotAuthenticatedException, InvalidFeatureKeyException {
        Objects.requireNonNull(featureKey, "featureKey");
        this.permissionValidationService.validateAuthenticated();
        this.validateFeatureKey(featureKey);
        this.userSettingsService.updateUserSettings(this.userManager.getRemoteUserKey(), DefaultFeatureDiscoveryService.addDiscoveredFeature(featureKey));
    }

    private void validateFeatureKey(String featureKey) throws InvalidFeatureKeyException {
        if (StringUtils.isBlank((CharSequence)featureKey) || featureKey.length() > 100 || !DefaultFeatureDiscoveryService.hasLegalChars(featureKey)) {
            throw this.serviceExceptionFactory.raise(InvalidFeatureKeyException.class, new Serializable[]{featureKey});
        }
    }

    private Set<String> getDiscoveredFeatureKeys() {
        Set applinksFeatureKeys = Sets.filter(this.getAllKeys(), IS_FEATURE_DISCOVERY_KEY);
        return ImmutableSet.copyOf((Iterable)Iterables.transform((Iterable)applinksFeatureKeys, REMOVE_FEATURE_DISCOVERY_PREFIX));
    }

    private Set<String> getAllKeys() {
        return this.userSettingsService.getUserSettings(this.userManager.getRemoteUserKey()).getKeys();
    }

    private static boolean hasLegalChars(String featureKey) {
        return VALID_FEATURE_KEY.matcher(featureKey).matches();
    }

    private static Function<UserSettingsBuilder, UserSettings> addDiscoveredFeature(String featureKey) {
        return settingsBuilder -> settingsBuilder.put(FEATURE_DISCOVERY_PREFIX + featureKey.toLowerCase(), true).build();
    }
}

