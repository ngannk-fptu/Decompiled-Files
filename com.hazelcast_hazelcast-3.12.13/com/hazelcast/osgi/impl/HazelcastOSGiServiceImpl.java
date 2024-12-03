/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;
import com.hazelcast.osgi.impl.HazelcastInternalOSGiService;
import com.hazelcast.osgi.impl.HazelcastOSGiInstanceImpl;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

class HazelcastOSGiServiceImpl
implements HazelcastInternalOSGiService {
    private static final ILogger LOGGER = Logger.getLogger(HazelcastOSGiService.class);
    private final Object serviceMutex = new Object();
    private final Bundle ownerBundle;
    private final BundleContext ownerBundleContext;
    private final String id;
    private final ConcurrentMap<HazelcastOSGiInstance, ServiceRegistration> instanceServiceRegistrationMap = new ConcurrentHashMap<HazelcastOSGiInstance, ServiceRegistration>();
    private final ConcurrentMap<String, HazelcastOSGiInstance> instanceMap = new ConcurrentHashMap<String, HazelcastOSGiInstance>();
    private ServiceRegistration serviceRegistration;
    private volatile HazelcastOSGiInstance hazelcastInstance;

    public HazelcastOSGiServiceImpl(Bundle ownerBundle) {
        this(ownerBundle, DEFAULT_ID);
    }

    public HazelcastOSGiServiceImpl(Bundle ownerBundle, String id) {
        this.ownerBundle = ownerBundle;
        this.ownerBundleContext = ownerBundle.getBundleContext();
        this.id = id;
    }

    private void checkActive() {
        if (!this.isActive()) {
            throw new IllegalStateException("Hazelcast OSGI Service is not active!");
        }
    }

    private boolean shouldSetGroupName(GroupConfig groupConfig) {
        return (groupConfig == null || StringUtil.isNullOrEmpty(groupConfig.getName()) || "dev".equals(groupConfig.getName())) && !Boolean.getBoolean("hazelcast.osgi.grouping.disabled");
    }

    private Config getConfig(Config config) {
        GroupConfig groupConfig;
        if (config == null) {
            config = new XmlConfigBuilder().build();
        }
        if (this.shouldSetGroupName(groupConfig = config.getGroupConfig())) {
            String groupName = this.id;
            if (groupConfig == null) {
                config.setGroupConfig(new GroupConfig(groupName));
            } else {
                groupConfig.setName(groupName);
            }
        }
        return config;
    }

    private HazelcastInstance createHazelcastInstance(Config config) {
        return Hazelcast.newHazelcastInstance(this.getConfig(config));
    }

    private HazelcastOSGiInstance registerInstance(HazelcastInstance instance) {
        HazelcastOSGiInstance hazelcastOSGiInstance = instance instanceof HazelcastOSGiInstance ? (HazelcastOSGiInstance)instance : new HazelcastOSGiInstanceImpl(instance, this);
        if (!Boolean.getBoolean("hazelcast.osgi.register.disabled")) {
            ServiceRegistration serviceRegistration = this.ownerBundleContext.registerService(HazelcastInstance.class.getName(), (Object)hazelcastOSGiInstance, null);
            this.instanceServiceRegistrationMap.put(hazelcastOSGiInstance, serviceRegistration);
        }
        this.instanceMap.put(instance.getName(), hazelcastOSGiInstance);
        return hazelcastOSGiInstance;
    }

    private void deregisterInstance(HazelcastOSGiInstance hazelcastOSGiInstance) {
        this.instanceMap.remove(hazelcastOSGiInstance.getName());
        ServiceRegistration serviceRegistration = (ServiceRegistration)this.instanceServiceRegistrationMap.remove(hazelcastOSGiInstance);
        if (serviceRegistration != null) {
            this.ownerBundleContext.ungetService(serviceRegistration.getReference());
            serviceRegistration.unregister();
        }
    }

    private void shutdownDefaultHazelcastInstanceIfActive() {
        if (this.hazelcastInstance != null) {
            this.shutdownHazelcastInstanceInternalSafely(this.hazelcastInstance);
            this.hazelcastInstance = null;
        }
    }

    private void shutdownAllInternal() {
        for (HazelcastOSGiInstance instance : this.instanceMap.values()) {
            this.shutdownHazelcastInstanceInternalSafely(instance);
        }
        this.shutdownDefaultHazelcastInstanceIfActive();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Bundle getOwnerBundle() {
        return this.ownerBundle;
    }

    @Override
    public boolean isActive() {
        return this.ownerBundle.getState() == 32;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void activate() {
        Object object = this.serviceMutex;
        synchronized (object) {
            if (this.ownerBundle.getState() == 8) {
                try {
                    if (this.hazelcastInstance != null) {
                        LOGGER.warning("Default Hazelcast instance should be null while activating service!");
                        this.shutdownDefaultHazelcastInstanceIfActive();
                    }
                    if (Boolean.getBoolean("hazelcast.osgi.start")) {
                        this.hazelcastInstance = new HazelcastOSGiInstanceImpl(this.createHazelcastInstance(null), this);
                        LOGGER.info("Default Hazelcast instance has been created");
                    }
                    if (this.hazelcastInstance != null && !Boolean.getBoolean("hazelcast.osgi.register.disabled")) {
                        this.registerInstance(this.hazelcastInstance);
                        LOGGER.info("Default Hazelcast instance has been registered as OSGI service");
                    }
                    this.serviceRegistration = this.ownerBundleContext.registerService(HazelcastOSGiService.class.getName(), (Object)this, null);
                    LOGGER.info(this + " has been registered as OSGI service and activated now");
                }
                catch (Throwable t) {
                    this.shutdownDefaultHazelcastInstanceIfActive();
                    ExceptionUtil.rethrow(t);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deactivate() {
        Object object = this.serviceMutex;
        synchronized (object) {
            if (this.ownerBundle.getState() == 16) {
                try {
                    this.shutdownAllInternal();
                    try {
                        this.ownerBundleContext.ungetService(this.serviceRegistration.getReference());
                        this.serviceRegistration.unregister();
                    }
                    catch (Throwable t) {
                        LOGGER.finest("Error occurred while deregistering " + this, t);
                    }
                    LOGGER.info(this + " has been deregistered as OSGI service and deactivated");
                }
                finally {
                    this.serviceRegistration = null;
                }
            }
        }
    }

    @Override
    public HazelcastOSGiInstance getDefaultHazelcastInstance() {
        this.checkActive();
        return this.hazelcastInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HazelcastOSGiInstance newHazelcastInstance(Config config) {
        Object object = this.serviceMutex;
        synchronized (object) {
            this.checkActive();
            return this.registerInstance(this.createHazelcastInstance(config));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HazelcastOSGiInstance newHazelcastInstance() {
        Object object = this.serviceMutex;
        synchronized (object) {
            this.checkActive();
            return this.registerInstance(this.createHazelcastInstance(null));
        }
    }

    @Override
    public HazelcastOSGiInstance getHazelcastInstanceByName(String instanceName) {
        this.checkActive();
        return (HazelcastOSGiInstance)this.instanceMap.get(instanceName);
    }

    @Override
    public Set<HazelcastOSGiInstance> getAllHazelcastInstances() {
        this.checkActive();
        return new HashSet<HazelcastOSGiInstance>(this.instanceMap.values());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdownHazelcastInstance(HazelcastOSGiInstance instance) {
        Object object = this.serviceMutex;
        synchronized (object) {
            this.checkActive();
            this.shutdownHazelcastInstanceInternal(instance);
        }
    }

    private void shutdownHazelcastInstanceInternal(HazelcastOSGiInstance instance) {
        try {
            this.deregisterInstance(instance);
        }
        catch (Throwable t) {
            LOGGER.finest("Error occurred while deregistering " + instance, t);
        }
        instance.shutdown();
    }

    private void shutdownHazelcastInstanceInternalSafely(HazelcastOSGiInstance instance) {
        try {
            this.shutdownHazelcastInstanceInternal(instance);
        }
        catch (Throwable t) {
            LOGGER.finest("Error occurred while shutting down " + instance, t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdownAll() {
        Object object = this.serviceMutex;
        synchronized (object) {
            this.checkActive();
            this.shutdownAllInternal();
        }
    }

    public String toString() {
        return "HazelcastOSGiServiceImpl{ownerBundle=" + this.ownerBundle + ", hazelcastInstance=" + this.hazelcastInstance + ", active=" + this.isActive() + ", id=" + this.id + '}';
    }
}

