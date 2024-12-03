/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.BooleanUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.feature;

import com.atlassian.applinks.internal.common.exception.DefaultServiceExceptionFactory;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.exception.SystemFeatureException;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultApplinksFeatureService
implements ApplinksFeatureService {
    private final DarkFeatureManager darkFeatureManager;
    private final PermissionValidationService permissionValidationService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final UserManager userManager;

    @Autowired
    public DefaultApplinksFeatureService(DarkFeatureManager darkFeatureManager, DefaultServiceExceptionFactory serviceExceptionFactory, PermissionValidationService permissionValidationService, UserManager userManager) {
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.permissionValidationService = permissionValidationService;
        this.darkFeatureManager = darkFeatureManager;
        this.userManager = userManager;
    }

    @Override
    public boolean isEnabled(@Nonnull ApplinksFeatures feature) {
        Objects.requireNonNull(feature, "feature");
        if (this.userManager.getRemoteUserKey() == null) {
            return false;
        }
        if (feature.isSystem()) {
            Boolean b = BooleanUtils.toBooleanObject((String)System.getProperty("atlassian.darkfeature." + feature.featureKey));
            return b == null ? feature.getDefaultValue() : b.booleanValue();
        }
        return this.darkFeatureManager.isFeatureEnabledForCurrentUser(feature.featureKey);
    }

    @Override
    public void enable(@Nonnull ApplinksFeatures feature, ApplinksFeatures ... moreFeatures) throws NoAccessException, SystemFeatureException {
        Objects.requireNonNull(feature, "feature");
        Objects.requireNonNull(moreFeatures, "moreFeatures");
        this.validateNoSystemFeatures(feature, moreFeatures);
        this.permissionValidationService.validateSysadmin();
        this.enableFeature(feature);
        for (ApplinksFeatures more : moreFeatures) {
            this.enableFeature(more);
        }
    }

    @Override
    public void disable(@Nonnull ApplinksFeatures feature, ApplinksFeatures ... moreFeatures) throws NoAccessException, SystemFeatureException {
        Objects.requireNonNull(feature, "feature");
        Objects.requireNonNull(moreFeatures, "moreFeatures");
        this.validateNoSystemFeatures(feature, moreFeatures);
        this.permissionValidationService.validateSysadmin();
        this.disableFeature(feature);
        for (ApplinksFeatures more : moreFeatures) {
            this.disableFeature(more);
        }
    }

    private void enableFeature(@Nonnull ApplinksFeatures feature) {
        this.darkFeatureManager.enableFeatureForAllUsers(feature.featureKey);
    }

    private void disableFeature(@Nonnull ApplinksFeatures feature) {
        this.darkFeatureManager.disableFeatureForAllUsers(feature.featureKey);
    }

    private void validateNoSystemFeatures(ApplinksFeatures feature, ApplinksFeatures ... moreFeatures) throws SystemFeatureException {
        EnumSet<ApplinksFeatures> systemFeatures = EnumSet.noneOf(ApplinksFeatures.class);
        if (feature.isSystem()) {
            systemFeatures.add(feature);
        }
        for (ApplinksFeatures more : moreFeatures) {
            if (!more.isSystem()) continue;
            systemFeatures.add(more);
        }
        if (!systemFeatures.isEmpty()) {
            throw this.serviceExceptionFactory.create(SystemFeatureException.class, new Serializable[]{((Object)systemFeatures).toString()});
        }
    }
}

