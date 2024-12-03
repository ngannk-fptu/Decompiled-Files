/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.InstanceMBean;
import com.hazelcast.internal.jmx.MBeans;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.properties.GroupProperty;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ManagementService
implements DistributedObjectListener {
    static final String DOMAIN = "com.hazelcast";
    private static final int INITIAL_CAPACITY = 5;
    final HazelcastInstanceImpl instance;
    private final boolean enabled;
    private final ILogger logger;
    private final String registrationId;
    private final InstanceMBean instanceMBean;

    public ManagementService(HazelcastInstanceImpl instance) {
        InstanceMBean instanceMBean;
        this.instance = instance;
        this.logger = instance.getLoggingService().getLogger(this.getClass());
        this.enabled = instance.node.getProperties().getBoolean(GroupProperty.ENABLE_JMX);
        if (!this.enabled) {
            this.instanceMBean = null;
            this.registrationId = null;
            return;
        }
        this.logger.info("Hazelcast JMX agent enabled.");
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            instanceMBean = this.createInstanceMBean(instance);
            mbs.registerMBean(instanceMBean, instanceMBean.objectName);
        }
        catch (Exception e) {
            instanceMBean = null;
            this.logger.warning("Unable to start JMX service", e);
        }
        this.instanceMBean = instanceMBean;
        this.registrationId = instance.addDistributedObjectListener(this);
        for (DistributedObject distributedObject : instance.getDistributedObjects()) {
            this.registerDistributedObject(distributedObject);
        }
    }

    protected InstanceMBean createInstanceMBean(HazelcastInstanceImpl instance) {
        return new InstanceMBean(instance, this);
    }

    public InstanceMBean getInstanceMBean() {
        return this.instanceMBean;
    }

    public void destroy() {
        if (!this.enabled) {
            return;
        }
        this.instance.removeDistributedObjectListener(this.registrationId);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            Set<ObjectName> entries = mbs.queryNames(new ObjectName("com.hazelcast:instance=" + ManagementService.quote(this.instance.getName()) + ",*"), null);
            for (ObjectName name : entries) {
                if (!mbs.isRegistered(name)) continue;
                mbs.unregisterMBean(name);
            }
        }
        catch (Exception e) {
            this.logger.warning("Error while un-registering MBeans", e);
        }
    }

    public static void shutdownAll(List<HazelcastInstanceProxy> instances) {
        for (HazelcastInstanceProxy instance : instances) {
            ManagementService.shutdown(instance.getName());
        }
    }

    public static void shutdown(String instanceName) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            Set<ObjectName> entries = mbs.queryNames(new ObjectName("com.hazelcast:instance=" + ManagementService.quote(instanceName) + ",*"), null);
            for (ObjectName name : entries) {
                if (!mbs.isRegistered(name)) continue;
                mbs.unregisterMBean(name);
            }
        }
        catch (Exception e) {
            Logger.getLogger(ManagementService.class.getName()).log(Level.WARNING, "Error while shutting down all jmx services...", e);
        }
    }

    @Override
    public void distributedObjectCreated(DistributedObjectEvent event) {
        this.registerDistributedObject(event.getDistributedObject());
    }

    @Override
    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        this.unregisterDistributedObject(event.getServiceName(), (String)event.getObjectName());
    }

    private void registerDistributedObject(DistributedObject distributedObject) {
        HazelcastMBean bean = MBeans.createHazelcastMBeanOrNull(distributedObject, this);
        if (bean == null) {
            return;
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (!mbs.isRegistered(bean.objectName)) {
            try {
                mbs.registerMBean(bean, bean.objectName);
            }
            catch (Exception e) {
                this.logger.warning("Error while registering " + bean.objectName, e);
            }
        } else {
            try {
                bean.preDeregister();
                bean.postDeregister();
            }
            catch (Exception e) {
                this.logger.finest(e);
            }
        }
    }

    private void unregisterDistributedObject(String serviceName, String objectName) {
        String objectType = MBeans.getObjectTypeOrNull(serviceName);
        if (objectType == null) {
            return;
        }
        ObjectName beanName = this.createObjectName(objectType, objectName);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (mbs.isRegistered(beanName)) {
            try {
                mbs.unregisterMBean(beanName);
            }
            catch (Exception e) {
                this.logger.warning("Error while un-registering " + objectName, e);
            }
        }
    }

    protected ObjectName createObjectName(String type, String name) {
        Hashtable<String, String> properties = new Hashtable<String, String>(5);
        properties.put("instance", ManagementService.quote(this.instance.getName()));
        if (type != null) {
            properties.put("type", ManagementService.quote(type));
        }
        if (name != null) {
            properties.put("name", ManagementService.quote(name));
        }
        try {
            return new ObjectName(DOMAIN, properties);
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String quote(String text) {
        return Pattern.compile("[:\",=*?]").matcher(text).find() || text.indexOf(10) >= 0 ? ObjectName.quote(text) : text;
    }
}

