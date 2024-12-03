/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiBundleUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.Version
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.DefaultVersionMatcher;
import org.eclipse.gemini.blueprint.extender.internal.activator.listeners.BaseListener;
import org.eclipse.gemini.blueprint.extender.internal.activator.listeners.NamespaceBundleLister;
import org.eclipse.gemini.blueprint.extender.internal.support.NamespaceManager;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Version;

public class NamespaceHandlerActivator
implements BundleActivator {
    private final Object monitor = new Object();
    private boolean stopped = false;
    private final Log log = LogFactory.getLog(this.getClass());
    private NamespaceManager nsManager;
    private BaseListener nsListener;
    private long bundleId;
    private BundleContext extenderBundleContext;
    private DefaultVersionMatcher versionMatcher;

    public void start(BundleContext extenderBundleContext) {
        this.extenderBundleContext = extenderBundleContext;
        this.nsManager = new NamespaceManager(extenderBundleContext);
        this.bundleId = extenderBundleContext.getBundle().getBundleId();
        Version extenderVersion = OsgiBundleUtils.getBundleVersion((Bundle)extenderBundleContext.getBundle());
        this.versionMatcher = new DefaultVersionMatcher(this.getManagedBundleExtenderVersionHeader(), extenderVersion);
        this.initNamespaceHandlers(extenderBundleContext);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(BundleContext context) throws Exception {
        Object object = this.monitor;
        synchronized (object) {
            if (this.stopped) {
                return;
            }
            this.stopped = true;
        }
        this.nsListener.close();
        this.extenderBundleContext.removeBundleListener((BundleListener)this.nsListener);
        this.nsListener = null;
        this.nsManager.destroy();
    }

    protected String getManagedBundleExtenderVersionHeader() {
        return "SpringExtender-Version";
    }

    protected void initNamespaceHandlers(BundleContext extenderBundleContext) {
        Bundle[] previousBundles;
        this.nsManager = new NamespaceManager(extenderBundleContext);
        boolean nsResolved = !Boolean.getBoolean("org.eclipse.gemini.blueprint.ns.bundles.started");
        this.nsListener = new NamespaceBundleLister(nsResolved, this);
        extenderBundleContext.addBundleListener((BundleListener)this.nsListener);
        for (Bundle bundle : previousBundles = extenderBundleContext.getBundles()) {
            if (nsResolved && OsgiBundleUtils.isBundleResolved((Bundle)bundle) || !nsResolved && OsgiBundleUtils.isBundleActive((Bundle)bundle) || this.bundleId == bundle.getBundleId()) {
                this.maybeAddNamespaceHandlerFor(bundle, false);
                continue;
            }
            if (!OsgiBundleUtils.isBundleLazyActivated((Bundle)bundle)) continue;
            this.maybeAddNamespaceHandlerFor(bundle, true);
        }
        this.nsManager.afterPropertiesSet();
    }

    public void maybeAddNamespaceHandlerFor(Bundle bundle, boolean isLazy) {
        if (this.handlerBundleMatchesExtenderVersion(bundle)) {
            this.nsManager.maybeAddNamespaceHandlerFor(bundle, isLazy);
        }
    }

    public void maybeRemoveNameSpaceHandlerFor(Bundle bundle) {
        if (this.handlerBundleMatchesExtenderVersion(bundle)) {
            this.nsManager.maybeRemoveNameSpaceHandlerFor(bundle);
        }
    }

    protected boolean handlerBundleMatchesExtenderVersion(Bundle bundle) {
        if (!this.versionMatcher.matchVersion(bundle)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Ignoring handler bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "] due to mismatch in expected extender version"));
            }
            return false;
        }
        return true;
    }
}

