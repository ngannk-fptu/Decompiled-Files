/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.rest.interceptor;

import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V3UiInterceptor
implements ResourceInterceptor {
    private static final Logger log = LoggerFactory.getLogger(V3UiInterceptor.class);
    private final ApplinksFeatureService applinksFeatureService;

    public V3UiInterceptor(@Nonnull ApplinksFeatureService applinksFeatureService) {
        this.applinksFeatureService = Objects.requireNonNull(applinksFeatureService, "applinksFeatureService");
    }

    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        if (this.applinksFeatureService.isEnabled(ApplinksFeatures.V3_UI) || this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI)) {
            invocation.invoke();
        } else {
            log.debug("Attempt to access resource while the New UI feature is disabled");
            invocation.getHttpContext().getResponse().setResponse(Response.status((Response.Status)Response.Status.NOT_FOUND).build());
        }
    }
}

