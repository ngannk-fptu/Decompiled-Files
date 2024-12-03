/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.exception.FlagDisabledException;
import com.atlassian.business.insights.core.rest.validation.FeatureFlagRequired;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import javax.ws.rs.ext.Provider;

@Provider
public class FeatureFlagGuard
implements ResourceInterceptor {
    private final DarkFeatureManager darkFeatureManager;

    public FeatureFlagGuard(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public void intercept(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        this.getRequiredFlag(methodInvocation).ifPresent(flagName -> {
            if (!this.isFeatureEnabled((String)flagName)) {
                throw new FlagDisabledException();
            }
        });
        methodInvocation.invoke();
    }

    private Optional<String> getRequiredFlag(MethodInvocation methodInvocation) {
        return Optional.ofNullable(methodInvocation.getMethod().getAnnotation(FeatureFlagRequired.class)).map(FeatureFlagRequired::value);
    }

    private boolean isFeatureEnabled(String featureFlag) {
        return this.darkFeatureManager.isEnabledForCurrentUser(featureFlag).orElse(false);
    }
}

