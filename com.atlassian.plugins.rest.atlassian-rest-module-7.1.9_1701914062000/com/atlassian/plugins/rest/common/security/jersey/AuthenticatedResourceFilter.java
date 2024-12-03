/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugins.rest.common.security.AdminOnly;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.plugins.rest.common.security.UnrestrictedAccess;
import com.atlassian.plugins.rest.common.util.AnnotationUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.lang.annotation.Annotation;
import java.util.Objects;

class AuthenticatedResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    @VisibleForTesting
    static final String DEFAULT_TO_LICENSED_ACCESS_FEATURE_KEY = "atlassian.rest.default.to.licensed.access.enabled";
    private final AnnotationUtils annotationUtils;
    private final UserManager userManager;
    private final DarkFeatureManager darkFeatureManager;

    public AuthenticatedResourceFilter(AnnotationUtils annotationUtils, UserManager userManager, DarkFeatureManager darkFeatureManager) {
        this.annotationUtils = Objects.requireNonNull(annotationUtils, "annotationUtils can't be null");
        this.userManager = Objects.requireNonNull(userManager, "userManager can't be null");
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager, "featureFlagManager can't be null");
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        Class<Annotation> annotation = this.annotationUtils.getAnnotation();
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (annotation == null) {
            boolean defaultToLicensedResourceAccessFlag = this.darkFeatureManager.isFeatureEnabledForAllUsers(DEFAULT_TO_LICENSED_ACCESS_FEATURE_KEY);
            if (defaultToLicensedResourceAccessFlag && userKey != null && this.userManager.isLicensed(userKey) || !defaultToLicensedResourceAccessFlag && userKey != null) {
                return request;
            }
        } else if (SystemAdminOnly.class.equals(annotation)) {
            if (userKey != null && this.userManager.isSystemAdmin(userKey)) {
                return request;
            }
        } else if (AdminOnly.class.equals(annotation)) {
            if (userKey != null && (this.userManager.isSystemAdmin(userKey) || this.userManager.isAdmin(userKey))) {
                return request;
            }
        } else if (LicensedOnly.class.equals(annotation)) {
            if (userKey != null && this.userManager.isLicensed(userKey)) {
                return request;
            }
        } else if (UnlicensedSiteAccess.class.equals(annotation)) {
            if (userKey != null && (this.userManager.isLicensed(userKey) || this.userManager.isLimitedUnlicensedUser(userKey))) {
                return request;
            }
        } else if (AnonymousSiteAccess.class.equals(annotation)) {
            if (userKey == null && this.userManager.isAnonymousAccessEnabled() || userKey != null && (this.userManager.isLicensed(userKey) || this.userManager.isLimitedUnlicensedUser(userKey))) {
                return request;
            }
        } else {
            if (UnrestrictedAccess.class.equals(annotation)) {
                return request;
            }
            if (AnonymousAllowed.class.equals(annotation)) {
                return request;
            }
        }
        throw new AuthenticationRequiredException();
    }
}

