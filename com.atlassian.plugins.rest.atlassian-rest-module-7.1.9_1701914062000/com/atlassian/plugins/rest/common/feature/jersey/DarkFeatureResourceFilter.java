/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.feature.jersey;

import com.atlassian.plugins.rest.common.feature.RequiresDarkFeature;
import com.atlassian.plugins.rest.common.util.ReflectionUtils;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkFeatureResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(DarkFeatureResourceFilter.class);
    private final DarkFeatureManager darkFeatureManager;
    private final AbstractMethod abstractMethod;

    public DarkFeatureResourceFilter(@Nonnull AbstractMethod method, @Nonnull DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager, "darkFeatureManager can't be null");
        this.abstractMethod = Objects.requireNonNull(method, "method can't be null");
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
        log.debug("Applying dark feature filter to request {} {}", (Object)request.getMethod(), (Object)request.getRequestUri());
        if (this.accessIsAllowed(this.abstractMethod) && this.accessIsAllowed(this.abstractMethod.getResource())) {
            log.debug("Dark feature check OK");
            return request;
        }
        log.debug("Dark feature check failed. Refusing access to the resource.");
        throw new NotFoundException(request.getRequestUri());
    }

    private boolean accessIsAllowed(AnnotatedElement e) {
        if (e == null) {
            return true;
        }
        RequiresDarkFeature annotation = ReflectionUtils.getAnnotation(RequiresDarkFeature.class, e);
        return annotation == null || this.allFeaturesAreEnabled(annotation.value());
    }

    private boolean allFeaturesAreEnabled(String[] featureKeys) {
        for (String featureKey : featureKeys) {
            if (this.darkFeatureManager.isFeatureEnabledForCurrentUser(featureKey)) continue;
            return false;
        }
        return true;
    }
}

