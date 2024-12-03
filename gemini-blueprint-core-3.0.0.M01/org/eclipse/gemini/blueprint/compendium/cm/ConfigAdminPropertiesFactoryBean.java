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
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.compendium.cm;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.compendium.internal.cm.CMUtils;
import org.eclipse.gemini.blueprint.compendium.internal.cm.util.ChangeableProperties;
import org.eclipse.gemini.blueprint.compendium.internal.cm.util.PropertiesUtil;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ConfigAdminPropertiesFactoryBean
implements BundleContextAware,
InitializingBean,
DisposableBean,
FactoryBean<Properties> {
    private static final Log log = LogFactory.getLog(ConfigAdminPropertiesFactoryBean.class);
    private volatile String persistentId;
    private volatile Properties properties;
    private BundleContext bundleContext;
    private boolean localOverride = false;
    private Properties localProperties;
    private volatile boolean dynamic = false;
    private volatile ServiceRegistration registration;
    private boolean initLazy = true;
    private long initTimeout = 0L;
    private final Object monitor = new Object();

    public void afterPropertiesSet() throws Exception {
        Assert.hasText((String)this.persistentId, (String)"persistentId property is required");
        Assert.notNull((Object)this.bundleContext, (String)"bundleContext property is required");
        Assert.isTrue((this.initTimeout >= 0L ? 1 : 0) != 0, (String)"a positive initTimeout is required");
        if (!this.initLazy) {
            this.createProperties();
        }
    }

    public void destroy() throws Exception {
        OsgiServiceUtils.unregisterService(this.registration);
        this.registration = null;
    }

    private void createProperties() {
        if (this.properties == null) {
            this.properties = this.dynamic ? new ChangeableProperties() : new Properties();
            try {
                PropertiesUtil.initProperties(this.localProperties, this.localOverride, CMUtils.getConfiguration(this.bundleContext, this.persistentId, this.initTimeout), this.properties);
            }
            catch (IOException ioe) {
                throw new BeanInitializationException("Cannot retrieve configuration for pid=" + this.persistentId, (Throwable)ioe);
            }
            if (this.dynamic) {
                this.registration = CMUtils.registerManagedService(this.bundleContext, new ConfigurationWatcher(), this.persistentId);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Properties getObject() throws Exception {
        if (this.properties == null) {
            Object object = this.monitor;
            synchronized (object) {
                if (this.properties == null) {
                    this.createProperties();
                }
            }
        }
        return this.properties;
    }

    public Class<? extends Properties> getObjectType() {
        return this.dynamic ? ChangeableProperties.class : Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public String getPersistentId() {
        return this.persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public void setProperties(Properties properties) {
        this.localProperties = properties;
    }

    public void setLocalOverride(boolean localOverride) {
        this.localOverride = localOverride;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setInitLazy(boolean initLazy) {
        this.initLazy = initLazy;
    }

    public void setInitTimeout(long initTimeout) {
        this.initTimeout = initTimeout;
    }

    private class ConfigurationWatcher
    implements ManagedService {
        private ConfigurationWatcher() {
        }

        public void updated(Dictionary props) throws ConfigurationException {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Configuration [" + ConfigAdminPropertiesFactoryBean.this.persistentId + "] has been updated with properties " + props));
            }
            PropertiesUtil.initProperties(ConfigAdminPropertiesFactoryBean.this.localProperties, ConfigAdminPropertiesFactoryBean.this.localOverride, props, ConfigAdminPropertiesFactoryBean.this.properties);
            if (ConfigAdminPropertiesFactoryBean.this.dynamic) {
                ((ChangeableProperties)ConfigAdminPropertiesFactoryBean.this.properties).notifyListeners();
            }
        }
    }
}

