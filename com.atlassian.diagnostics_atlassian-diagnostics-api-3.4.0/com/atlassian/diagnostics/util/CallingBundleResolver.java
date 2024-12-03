/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.CallingBundleAccessor
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContext
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.util;

import com.atlassian.plugin.osgi.hostcomponents.CallingBundleAccessor;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallingBundleResolver {
    private static final Logger log = LoggerFactory.getLogger(CallingBundleResolver.class);
    private final boolean geminiSupported;

    @VisibleForTesting
    CallingBundleResolver(boolean geminiSupported) {
        this.geminiSupported = geminiSupported;
    }

    public CallingBundleResolver() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContext");
        }
        catch (Exception e) {
            log.debug("OSGI Gemini Blueprints are not available. Capability to determine calling OSGI bundle will be limited.");
        }
        this.geminiSupported = clazz != null;
    }

    @Nonnull
    public Optional<Bundle> getCallingBundle() {
        BundleContext bundleContext;
        Bundle bundle = null;
        if (this.geminiSupported && (bundleContext = LocalBundleContext.getInvokerBundleContext()) != null) {
            bundle = bundleContext.getBundle();
        }
        if (bundle == null) {
            bundle = CallingBundleAccessor.getCallingBundle();
        }
        return Optional.ofNullable(bundle);
    }
}

