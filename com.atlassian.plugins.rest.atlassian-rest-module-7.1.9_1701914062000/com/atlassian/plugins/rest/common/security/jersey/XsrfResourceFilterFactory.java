/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.xsrf.XsrfRequestValidator
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaultsModuleDescriptor;
import com.atlassian.plugins.rest.common.security.jersey.OriginBasedXsrfResourceFilter;
import com.atlassian.plugins.rest.common.security.jersey.XsrfResourceFilter;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.xsrf.XsrfRequestValidator;
import com.google.common.annotations.VisibleForTesting;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class XsrfResourceFilterFactory
implements ResourceFilterFactory {
    private static final Logger log = LoggerFactory.getLogger(XsrfResourceFilterFactory.class);
    private static final String LEGACY_FEATURE_KEY = "atlassian.rest.xsrf.legacy.enabled";
    private final HttpContext httpContext;
    private final XsrfRequestValidator xsrfRequestValidator;
    private final PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> pluginModuleTracker;
    private final DarkFeatureManager darkFeatureManager;

    public XsrfResourceFilterFactory(HttpContext httpContext, XsrfRequestValidator xsrfRequestValidator, PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, DarkFeatureManager darkFeatureManager) {
        this.httpContext = Objects.requireNonNull(httpContext, "httpContext can't be null");
        this.xsrfRequestValidator = Objects.requireNonNull(xsrfRequestValidator, "xsrfRequestValidator can't be null");
        this.pluginModuleTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, CorsDefaultsModuleDescriptor.class);
        this.darkFeatureManager = darkFeatureManager;
    }

    @VisibleForTesting
    boolean hasRequiresXsrfCheckAnnotation(AnnotatedElement annotatedElement) {
        if (annotatedElement.isAnnotationPresent(RequiresXsrfCheck.class)) {
            return true;
        }
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            if (!annotation.annotationType().getSimpleName().equals(RequiresXsrfCheck.class.getSimpleName())) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    @VisibleForTesting
    boolean inLegacyXsrfMode() {
        return this.darkFeatureManager.isFeatureEnabledForAllUsers(LEGACY_FEATURE_KEY);
    }

    private static boolean isXsrfProtectionExcludedAnnotationPresent(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType().getCanonicalName().equals(XsrfProtectionExcluded.class.getCanonicalName())) continue;
            if (!annotation.annotationType().equals(XsrfProtectionExcluded.class)) {
                log.warn("Detected usage of the com.atlassian.annotations.security.XsrfProtectionExcluded annotation loaded from elsewhere. " + XsrfProtectionExcluded.class.getClassLoader() + " != " + annotation.annotationType().getClassLoader());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod method) {
        boolean hasEnforceAnnotation = this.hasRequiresXsrfCheckAnnotation(method) || this.hasRequiresXsrfCheckAnnotation(method.getResource());
        boolean hasExcludeAnnotation = XsrfResourceFilterFactory.isXsrfProtectionExcludedAnnotationPresent(method.getAnnotations());
        XsrfResourceFilter xsrfResourceFilter = null;
        if (this.inLegacyXsrfMode()) {
            if (!hasExcludeAnnotation) {
                if (hasEnforceAnnotation) {
                    xsrfResourceFilter = new XsrfResourceFilter();
                } else if (method.getAnnotations().length != 0) {
                    xsrfResourceFilter = new OriginBasedXsrfResourceFilter();
                }
                if (xsrfResourceFilter != null) {
                    xsrfResourceFilter.setFailureStatus(Response.Status.NOT_FOUND);
                }
            }
        } else if (method.isAnnotationPresent(GET.class) && hasEnforceAnnotation || !method.isAnnotationPresent(GET.class) && !hasExcludeAnnotation) {
            xsrfResourceFilter = new XsrfResourceFilter();
        }
        if (xsrfResourceFilter != null) {
            xsrfResourceFilter.setHttpContext(this.httpContext);
            xsrfResourceFilter.setXsrfRequestValidator(this.xsrfRequestValidator);
            xsrfResourceFilter.setPluginModuleTracker(this.pluginModuleTracker);
            return Collections.singletonList(xsrfResourceFilter);
        }
        return Collections.emptyList();
    }
}

