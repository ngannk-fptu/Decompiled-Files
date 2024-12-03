/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.springframework.core.NamedInheritableThreadLocal
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import org.osgi.framework.BundleContext;
import org.springframework.core.NamedInheritableThreadLocal;

public abstract class LocalBundleContext {
    private static final ThreadLocal<BundleContext> invokerBundleContext = new NamedInheritableThreadLocal("Current invoker bundle context");

    public static BundleContext getInvokerBundleContext() {
        return invokerBundleContext.get();
    }

    static BundleContext setInvokerBundleContext(BundleContext bundleContext) {
        BundleContext old = invokerBundleContext.get();
        invokerBundleContext.set(bundleContext);
        return old;
    }
}

