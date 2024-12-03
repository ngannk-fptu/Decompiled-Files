/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.rest.common.security.CorsAllowed;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaultsModuleDescriptor;
import com.atlassian.plugins.rest.common.security.jersey.CorsResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.Provider;
import org.springframework.beans.factory.DisposableBean;

@Provider
public class CorsResourceFilterFactory
implements ResourceFilterFactory,
DisposableBean {
    private final PluginModuleTracker<CorsDefaults, CorsDefaultsModuleDescriptor> tracker;

    public CorsResourceFilterFactory(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.tracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, CorsDefaultsModuleDescriptor.class);
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod method) {
        if (CorsResourceFilterFactory.annotationIsPresent(method, CorsAllowed.class)) {
            String targetMethod = "GET";
            for (Annotation ann : method.getAnnotations()) {
                HttpMethod m = ann.annotationType().getAnnotation(HttpMethod.class);
                if (m == null) continue;
                targetMethod = m.value();
                break;
            }
            CorsResourceFilter resourceFilter = new CorsResourceFilter(this.tracker, targetMethod);
            return Collections.singletonList(resourceFilter);
        }
        return Collections.emptyList();
    }

    private static boolean annotationIsPresent(AbstractMethod method, Class<? extends Annotation> annotationType) {
        return method.isAnnotationPresent(annotationType) || method.getResource().isAnnotationPresent(annotationType) || CorsResourceFilterFactory.packageHasAnnotation(annotationType, method.getResource().getResourceClass().getPackage());
    }

    private static boolean packageHasAnnotation(Class<? extends Annotation> annotationClass, Package resourcePackage) {
        return resourcePackage != null && resourcePackage.isAnnotationPresent(annotationClass);
    }

    public void destroy() {
        this.tracker.close();
    }
}

