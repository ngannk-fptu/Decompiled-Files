/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.extender.internal.activator.ListListenerAdapter;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ListenerServiceActivator
implements BundleActivator {
    private final Object monitor = new Object();
    private boolean stopped = false;
    private OsgiBundleApplicationContextEventMulticaster multicaster;
    private volatile ListListenerAdapter osgiListeners;
    private final Log log = LogFactory.getLog(this.getClass());
    private final ExtenderConfiguration extenderConfiguration;
    private BundleContext extenderBundleContext;

    public ListenerServiceActivator(ExtenderConfiguration extenderConfiguration) {
        this.extenderConfiguration = extenderConfiguration;
    }

    public void start(BundleContext extenderBundleContext) throws Exception {
        this.extenderBundleContext = extenderBundleContext;
        this.initListenerService();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(BundleContext extenderBundleContext) throws Exception {
        Object object = this.monitor;
        synchronized (object) {
            if (this.stopped) {
                return;
            }
            this.stopped = true;
        }
        if (this.multicaster != null) {
            this.multicaster.removeAllListeners();
            this.multicaster = null;
        }
        this.osgiListeners.destroy();
        this.osgiListeners = null;
    }

    protected void initListenerService() {
        this.multicaster = this.extenderConfiguration.getEventMulticaster();
        this.addApplicationListener(this.multicaster);
        this.multicaster.addApplicationListener(this.extenderConfiguration.getContextEventListener());
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Initialization of OSGi listeners service completed...");
        }
    }

    protected void addApplicationListener(OsgiBundleApplicationContextEventMulticaster multicaster) {
        this.osgiListeners = new ListListenerAdapter(this.extenderBundleContext);
        this.osgiListeners.afterPropertiesSet();
        multicaster.addApplicationListener((OsgiBundleApplicationContextListener)this.osgiListeners);
    }

    public OsgiBundleApplicationContextEventMulticaster getMulticaster() {
        return this.multicaster;
    }
}

