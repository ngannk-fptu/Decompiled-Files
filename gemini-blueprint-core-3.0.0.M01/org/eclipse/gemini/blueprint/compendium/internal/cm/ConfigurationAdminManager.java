/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.service.cm.ConfigurationException
 *  org.osgi.service.cm.ManagedService
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.compendium.internal.cm.CMUtils;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ManagedServiceBeanManager;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;

class ConfigurationAdminManager
implements DisposableBean {
    private static final Log log = LogFactory.getLog(ConfigurationAdminManager.class);
    private final BundleContext bundleContext;
    private final String pid;
    private Map properties = null;
    private boolean initialized = false;
    private ManagedServiceBeanManager beanManager;
    private final Object monitor = new Object();
    private ServiceRegistration registration;

    public ConfigurationAdminManager(String pid, BundleContext bundleContext) {
        this.pid = pid;
        this.bundleContext = bundleContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBeanManager(ManagedServiceBeanManager beanManager) {
        Object object = this.monitor;
        synchronized (object) {
            this.beanManager = beanManager;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getConfiguration() {
        this.initialize();
        Object object = this.monitor;
        synchronized (object) {
            return this.properties;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialize() {
        Object object = this.monitor;
        synchronized (object) {
            if (this.initialized) {
                return;
            }
            this.initialized = true;
            this.initProperties();
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Initial properties for pid [" + this.pid + "] are " + this.properties));
        }
        ServiceRegistration reg = CMUtils.registerManagedService(this.bundleContext, new ConfigurationWatcher(), this.pid);
        Object object2 = this.monitor;
        synchronized (object2) {
            this.registration = reg;
        }
    }

    private void initProperties() {
        try {
            this.properties = CMUtils.getConfiguration(this.bundleContext, this.pid, 0L);
        }
        catch (IOException ioe) {
            throw new BeanInitializationException("Cannot retrieve configuration for pid=" + this.pid, (Throwable)ioe);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        ServiceRegistration reg = null;
        Object object = this.monitor;
        synchronized (object) {
            reg = this.registration;
            this.registration = null;
        }
        if (OsgiServiceUtils.unregisterService(reg)) {
            log.trace((Object)("Shutting down CM tracker for pid [" + this.pid + "]"));
        }
    }

    private class ConfigurationWatcher
    implements ManagedService {
        private ConfigurationWatcher() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void updated(Dictionary props) throws ConfigurationException {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Configuration [" + ConfigurationAdminManager.this.pid + "] has been updated with properties " + props));
            }
            Object object = ConfigurationAdminManager.this.monitor;
            synchronized (object) {
                ConfigurationAdminManager.this.properties = new MapBasedDictionary(props);
                if (ConfigurationAdminManager.this.beanManager != null) {
                    ConfigurationAdminManager.this.beanManager.updated(ConfigurationAdminManager.this.properties);
                }
            }
        }
    }
}

