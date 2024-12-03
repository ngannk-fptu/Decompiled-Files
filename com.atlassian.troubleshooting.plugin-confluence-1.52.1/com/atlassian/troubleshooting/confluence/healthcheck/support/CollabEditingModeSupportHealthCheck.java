/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.support;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.annotation.Autowired;

public class CollabEditingModeSupportHealthCheck
implements SupportHealthCheck {
    private final BundleContext bundleContext;
    private final SupportHealthStatusBuilder healthStatusBuilder;

    @Autowired
    public CollabEditingModeSupportHealthCheck(BundleContext bundleContext, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.bundleContext = bundleContext;
        this.healthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        try (SynchronyConfigManagerWrapper wrapper = new SynchronyConfigManagerWrapper(this.bundleContext);){
            ServiceReference ref = wrapper.getServiceRef();
            if (ref != null) {
                SynchronyConfigurationManager manager = wrapper.getService();
                boolean isSynchronyEnabled = manager.isSynchronyEnabled();
                boolean isSharedDraftEnabled = manager.isSharedDraftsEnabled();
                if (!isSynchronyEnabled) {
                    if (isSharedDraftEnabled) {
                        SupportHealthStatus supportHealthStatus = this.healthStatusBuilder.warning(this, "confluence.healthcheck.collab.editing.mode.limited.fail", new Serializable[0]);
                        return supportHealthStatus;
                    }
                    SupportHealthStatus supportHealthStatus = this.healthStatusBuilder.ok(this, "confluence.healthcheck.collab.editing.mode.disabled.healthy", new Serializable[0]);
                    return supportHealthStatus;
                }
                SupportHealthStatus supportHealthStatus = this.healthStatusBuilder.ok(this, "confluence.healthcheck.collab.editing.mode.enabled.healthy", new Serializable[0]);
                return supportHealthStatus;
            }
        }
        return this.healthStatusBuilder.ok(this, "confluence.healthcheck.collab.editing.mode.skipped.healthy", new Serializable[0]);
    }

    private final class SynchronyConfigManagerWrapper
    implements AutoCloseable {
        private final BundleContext bundleContext;
        private final ServiceReference sr;

        SynchronyConfigManagerWrapper(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
            this.sr = bundleContext.getServiceReference("com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager");
        }

        @Override
        public void close() {
            if (this.getServiceRef() != null) {
                this.bundleContext.ungetService(this.sr);
            }
        }

        private ServiceReference getServiceRef() {
            return this.sr;
        }

        private SynchronyConfigurationManager getService() {
            return (SynchronyConfigurationManager)this.bundleContext.getService(this.sr);
        }
    }
}

