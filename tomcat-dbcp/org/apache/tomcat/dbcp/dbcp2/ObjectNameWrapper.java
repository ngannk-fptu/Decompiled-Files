/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.lang.management.ManagementFactory;
import java.util.Objects;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

final class ObjectNameWrapper {
    private static final Log log = LogFactory.getLog(ObjectNameWrapper.class);
    private static final MBeanServer MBEAN_SERVER = ObjectNameWrapper.getPlatformMBeanServer();
    private final ObjectName objectName;

    private static MBeanServer getPlatformMBeanServer() {
        try {
            return ManagementFactory.getPlatformMBeanServer();
        }
        catch (Exception | LinkageError e) {
            log.debug((Object)"Failed to get platform MBeanServer", e);
            return null;
        }
    }

    public static ObjectName unwrap(ObjectNameWrapper wrapper) {
        return wrapper == null ? null : wrapper.unwrap();
    }

    public static ObjectNameWrapper wrap(ObjectName objectName) {
        return new ObjectNameWrapper(objectName);
    }

    public static ObjectNameWrapper wrap(String name) throws MalformedObjectNameException {
        return ObjectNameWrapper.wrap(new ObjectName(name));
    }

    ObjectNameWrapper(ObjectName objectName) {
        this.objectName = objectName;
    }

    public void registerMBean(Object object) {
        if (MBEAN_SERVER == null || this.objectName == null) {
            return;
        }
        try {
            MBEAN_SERVER.registerMBean(object, this.objectName);
        }
        catch (Exception | LinkageError e) {
            log.warn((Object)("Failed to complete JMX registration for " + this.objectName), e);
        }
    }

    public String toString() {
        return Objects.toString(this.objectName);
    }

    public void unregisterMBean() {
        if (MBEAN_SERVER == null || this.objectName == null) {
            return;
        }
        if (MBEAN_SERVER.isRegistered(this.objectName)) {
            try {
                MBEAN_SERVER.unregisterMBean(this.objectName);
            }
            catch (Exception | LinkageError e) {
                log.warn((Object)("Failed to complete JMX unregistration for " + this.objectName), e);
            }
        }
    }

    public ObjectName unwrap() {
        return this.objectName;
    }
}

