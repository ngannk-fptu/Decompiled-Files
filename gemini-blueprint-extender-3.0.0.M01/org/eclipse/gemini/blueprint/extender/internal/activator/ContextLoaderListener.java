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
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.Version
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.internal.activator.ApplicationContextConfigurationFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.DefaultApplicationContextConfigurationFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.DefaultVersionMatcher;
import org.eclipse.gemini.blueprint.extender.internal.activator.LifecycleManager;
import org.eclipse.gemini.blueprint.extender.internal.activator.NoOpOsgiContextProcessor;
import org.eclipse.gemini.blueprint.extender.internal.activator.OsgiContextProcessor;
import org.eclipse.gemini.blueprint.extender.internal.activator.TypeCompatibilityChecker;
import org.eclipse.gemini.blueprint.extender.internal.activator.VersionMatcher;
import org.eclipse.gemini.blueprint.extender.internal.activator.listeners.BaseListener;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Version;

public class ContextLoaderListener
implements BundleActivator {
    protected final Log log = LogFactory.getLog(this.getClass());
    private ExtenderConfiguration extenderConfiguration;
    private VersionMatcher versionMatcher;
    private Version extenderVersion;
    private long bundleId;
    private BundleContext bundleContext;
    private BaseListener contextListener;
    private final Object monitor = new Object();
    private volatile boolean isClosed = false;
    private volatile LifecycleManager lifecycleManager;
    private volatile OsgiContextProcessor processor;

    public ContextLoaderListener(ExtenderConfiguration extenderConfiguration) {
        this.extenderConfiguration = extenderConfiguration;
    }

    public void start(BundleContext extenderBundleContext) throws Exception {
        this.bundleContext = extenderBundleContext;
        this.bundleId = extenderBundleContext.getBundle().getBundleId();
        this.extenderVersion = OsgiBundleUtils.getBundleVersion((Bundle)extenderBundleContext.getBundle());
        this.versionMatcher = new DefaultVersionMatcher(this.getManagedBundleExtenderVersionHeader(), this.extenderVersion);
        this.processor = this.createContextProcessor();
        this.lifecycleManager = new LifecycleManager(this.extenderConfiguration, this.getVersionMatcher(), this.createContextConfigFactory(), this.getOsgiApplicationContextCreator(), this.processor, this.getTypeCompatibilityChecker(), this.bundleContext);
        this.initStartedBundles(this.bundleContext);
    }

    protected OsgiContextProcessor createContextProcessor() {
        return new NoOpOsgiContextProcessor();
    }

    protected TypeCompatibilityChecker getTypeCompatibilityChecker() {
        return null;
    }

    protected void initStartedBundles(BundleContext bundleContext) {
        this.contextListener = new ContextBundleListener();
        bundleContext.addBundleListener((BundleListener)this.contextListener);
        Bundle[] previousBundles = bundleContext.getBundles();
        for (int i = 0; i < previousBundles.length; ++i) {
            if (!OsgiBundleUtils.isBundleActive((Bundle)previousBundles[i])) continue;
            try {
                this.lifecycleManager.maybeCreateApplicationContextFor(previousBundles[i]);
                continue;
            }
            catch (Throwable e) {
                this.log.warn((Object)("Cannot start bundle " + OsgiStringUtils.nullSafeSymbolicName((Bundle)previousBundles[i]) + " due to"), e);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        this.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void shutdown() {
        Object object = this.monitor;
        synchronized (object) {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
        }
        this.contextListener.close();
        if (this.contextListener != null) {
            this.bundleContext.removeBundleListener((BundleListener)this.contextListener);
            this.contextListener = null;
        }
        this.lifecycleManager.destroy();
    }

    protected ApplicationContextConfigurationFactory createContextConfigFactory() {
        return new DefaultApplicationContextConfigurationFactory();
    }

    public VersionMatcher getVersionMatcher() {
        return this.versionMatcher;
    }

    protected String getManagedBundleExtenderVersionHeader() {
        return "SpringExtender-Version";
    }

    protected OsgiApplicationContextCreator getOsgiApplicationContextCreator() {
        OsgiApplicationContextCreator creator = this.extenderConfiguration.getContextCreator();
        if (creator == null) {
            creator = this.createDefaultOsgiApplicationContextCreator();
        }
        return creator;
    }

    protected OsgiApplicationContextCreator createDefaultOsgiApplicationContextCreator() {
        return new DefaultOsgiApplicationContextCreator();
    }

    private class ContextBundleListener
    extends BaseListener {
        private ContextBundleListener() {
        }

        @Override
        protected void handleEvent(BundleEvent event) {
            Bundle bundle = event.getBundle();
            if (bundle.getBundleId() == ContextLoaderListener.this.bundleId) {
                return;
            }
            switch (event.getType()) {
                case 2: {
                    ContextLoaderListener.this.lifecycleManager.maybeCreateApplicationContextFor(bundle);
                    break;
                }
                case 256: {
                    if (OsgiBundleUtils.isSystemBundle((Bundle)bundle)) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)"System bundle stopping");
                        }
                        ContextLoaderListener.this.shutdown();
                        break;
                    }
                    ContextLoaderListener.this.lifecycleManager.maybeCloseApplicationContextFor(bundle);
                    break;
                }
            }
        }
    }
}

