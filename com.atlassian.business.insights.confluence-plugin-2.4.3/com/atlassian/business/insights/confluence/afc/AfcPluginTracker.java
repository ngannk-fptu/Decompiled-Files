/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.core.service.track.ReflectionServiceTracker
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.business.insights.confluence.afc;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.service.track.ReflectionServiceTracker;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class AfcPluginTracker
implements DisposableBean {
    public static final String AFC_EVENT_SERVICE_CLASS_NAME = "com.addonengine.addons.analytics.service.EventService";
    public static final String METHOD_NAME = "streamUnsecured";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ReflectionServiceTracker<Object> eventServiceTracker;
    private final AtomicReference<Object> eventServiceRef = new AtomicReference();

    public AfcPluginTracker(@Nonnull BundleContext bundleContext) {
        this.eventServiceTracker = new ReflectionServiceTracker(bundleContext, AFC_EVENT_SERVICE_CLASS_NAME, this::afcEnabled, this::afcDisabled);
    }

    @VisibleForTesting
    ReflectionServiceTracker<Object> getEventServiceTracker() {
        return this.eventServiceTracker;
    }

    private void afcEnabled(Object service) {
        try {
            if (Arrays.stream(service.getClass().getMethods()).anyMatch(method -> method.getName().equals(METHOD_NAME))) {
                this.eventServiceRef.set(service);
            } else {
                this.log.warn("No {} method found in {}, AFC exporting will NOT be enabled.", (Object)METHOD_NAME, (Object)AFC_EVENT_SERVICE_CLASS_NAME);
            }
        }
        catch (NoClassDefFoundError e) {
            this.log.warn("{} class is not resolved by data-pipeline plugin, AFC exporting will NOT be enabled", (Object)AFC_EVENT_SERVICE_CLASS_NAME);
        }
        catch (Exception e) {
            this.log.error("Unexpected exception occurred, AFC exporting will NOT be enabled", (Throwable)e);
        }
    }

    private void afcDisabled(Object service) {
        this.eventServiceRef.set(null);
    }

    public void destroy() throws Exception {
        this.eventServiceTracker.close();
    }

    public boolean isAfcEnabled() {
        return this.eventServiceRef.get() != null;
    }

    @Nullable
    public Object getAfcEventService() {
        return this.eventServiceRef.get();
    }
}

