/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.LifecycleEvent
 *  org.apache.catalina.LifecycleListener
 *  org.apache.catalina.Server
 *  org.apache.catalina.mbeans.MBeanUtils
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.modeler.ManagedBean
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.storeconfig;

import javax.management.DynamicMBean;
import javax.management.ObjectName;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.storeconfig.IStoreConfig;
import org.apache.catalina.storeconfig.StoreLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class StoreConfigLifecycleListener
implements LifecycleListener {
    private static Log log = LogFactory.getLog(StoreConfigLifecycleListener.class);
    private static StringManager sm = StringManager.getManager(StoreConfigLifecycleListener.class);
    protected final Registry registry = MBeanUtils.createRegistry();
    IStoreConfig storeConfig;
    private String storeConfigClass = "org.apache.catalina.storeconfig.StoreConfig";
    private String storeRegistry = null;
    private ObjectName oname = null;

    public void lifecycleEvent(LifecycleEvent event) {
        if ("after_start".equals(event.getType())) {
            if (event.getSource() instanceof Server) {
                this.createMBean((Server)event.getSource());
            } else {
                log.warn((Object)sm.getString("storeConfigListener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
            }
        } else if ("after_stop".equals(event.getType()) && this.oname != null) {
            this.registry.unregisterComponent(this.oname);
            this.oname = null;
        }
    }

    protected void createMBean(Server server) {
        StoreLoader loader = new StoreLoader();
        try {
            Class<?> clazz = Class.forName(this.getStoreConfigClass(), true, this.getClass().getClassLoader());
            this.storeConfig = (IStoreConfig)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            loader.load(this.getStoreRegistry());
            this.storeConfig.setRegistry(loader.getRegistry());
            this.storeConfig.setServer(server);
        }
        catch (Exception e) {
            log.error((Object)sm.getString("storeConfigListener.loadError"), (Throwable)e);
            return;
        }
        try {
            this.oname = new ObjectName("Catalina:type=StoreConfig");
            this.registry.registerComponent((Object)this.storeConfig, this.oname, "StoreConfig");
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("storeConfigListener.registerError"), (Throwable)ex);
        }
    }

    protected DynamicMBean getManagedBean(Object object) throws Exception {
        ManagedBean managedBean = this.registry.findManagedBean("StoreConfig");
        return managedBean.createMBean(object);
    }

    public IStoreConfig getStoreConfig() {
        return this.storeConfig;
    }

    public void setStoreConfig(IStoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }

    public String getStoreConfigClass() {
        return this.storeConfigClass;
    }

    public void setStoreConfigClass(String storeConfigClass) {
        this.storeConfigClass = storeConfigClass;
    }

    public String getStoreRegistry() {
        return this.storeRegistry;
    }

    public void setStoreRegistry(String storeRegistry) {
        this.storeRegistry = storeRegistry;
    }
}

