/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.support;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class MBeanRegistrationSupport {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    protected MBeanServer server;
    private final Set<ObjectName> registeredBeans = new LinkedHashSet<ObjectName>();
    private RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;

    public void setServer(@Nullable MBeanServer server) {
        this.server = server;
    }

    @Nullable
    public final MBeanServer getServer() {
        return this.server;
    }

    public void setRegistrationPolicy(RegistrationPolicy registrationPolicy) {
        Assert.notNull((Object)registrationPolicy, "RegistrationPolicy must not be null");
        this.registrationPolicy = registrationPolicy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doRegister(Object mbean, ObjectName objectName) throws JMException {
        ObjectName actualObjectName;
        Assert.state(this.server != null, "No MBeanServer set");
        Set<ObjectName> set = this.registeredBeans;
        synchronized (set) {
            ObjectInstance registeredBean = null;
            try {
                registeredBean = this.server.registerMBean(mbean, objectName);
            }
            catch (InstanceAlreadyExistsException ex) {
                if (this.registrationPolicy == RegistrationPolicy.IGNORE_EXISTING) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Ignoring existing MBean at [" + objectName + "]");
                    }
                }
                if (this.registrationPolicy == RegistrationPolicy.REPLACE_EXISTING) {
                    try {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Replacing existing MBean at [" + objectName + "]");
                        }
                        this.server.unregisterMBean(objectName);
                        registeredBean = this.server.registerMBean(mbean, objectName);
                    }
                    catch (InstanceNotFoundException ex2) {
                        if (this.logger.isErrorEnabled()) {
                            this.logger.error("Unable to replace existing MBean at [" + objectName + "]", ex2);
                        }
                        throw ex;
                    }
                }
                throw ex;
            }
            ObjectName objectName2 = actualObjectName = registeredBean != null ? registeredBean.getObjectName() : null;
            if (actualObjectName == null) {
                actualObjectName = objectName;
            }
            this.registeredBeans.add(actualObjectName);
        }
        this.onRegister(actualObjectName, mbean);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void unregisterBeans() {
        LinkedHashSet<ObjectName> snapshot;
        Set<ObjectName> set = this.registeredBeans;
        synchronized (set) {
            snapshot = new LinkedHashSet<ObjectName>(this.registeredBeans);
        }
        if (!snapshot.isEmpty()) {
            this.logger.info("Unregistering JMX-exposed beans");
            for (ObjectName objectName : snapshot) {
                this.doUnregister(objectName);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doUnregister(ObjectName objectName) {
        Assert.state(this.server != null, "No MBeanServer set");
        boolean actuallyUnregistered = false;
        Set<ObjectName> set = this.registeredBeans;
        synchronized (set) {
            block10: {
                if (this.registeredBeans.remove(objectName)) {
                    try {
                        if (this.server.isRegistered(objectName)) {
                            this.server.unregisterMBean(objectName);
                            actuallyUnregistered = true;
                        } else if (this.logger.isWarnEnabled()) {
                            this.logger.warn("Could not unregister MBean [" + objectName + "] as said MBean is not registered (perhaps already unregistered by an external process)");
                        }
                    }
                    catch (JMException ex) {
                        if (!this.logger.isErrorEnabled()) break block10;
                        this.logger.error("Could not unregister MBean [" + objectName + "]", ex);
                    }
                }
            }
        }
        if (actuallyUnregistered) {
            this.onUnregister(objectName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final ObjectName[] getRegisteredObjectNames() {
        Set<ObjectName> set = this.registeredBeans;
        synchronized (set) {
            return this.registeredBeans.toArray(new ObjectName[0]);
        }
    }

    protected void onRegister(ObjectName objectName, Object mbean) {
        this.onRegister(objectName);
    }

    protected void onRegister(ObjectName objectName) {
    }

    protected void onUnregister(ObjectName objectName) {
    }
}

