/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import javax.management.JMX;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class MBeanUtil {
    private static Object synchThis = new Object();
    private static MBeanServer mbeanServer;

    public static Object getMBean(Class c, String name) throws Throwable {
        MBeanServer server = MBeanUtil.getMbeanServer();
        return JMX.newMBeanProxy(server, new ObjectName(name), c);
    }

    public static Object invoke(String serviceName, String operationName, Object[] params, String[] signature) throws Throwable {
        MBeanServer server = MBeanUtil.getMbeanServer();
        return server.invoke(new ObjectName(serviceName), operationName, params, signature);
    }

    public static Object getAttribute(String name, String attribute) throws Throwable {
        MBeanServer server = MBeanUtil.getMbeanServer();
        return server.getAttribute(new ObjectName(name), attribute);
    }

    public static MBeanAttributeInfo stringAttrInfo(String name, String desc) {
        return new MBeanAttributeInfo(name, "java.lang.String", desc, true, true, false);
    }

    public static MBeanAttributeInfo boolAttrInfo(String name, String desc) {
        return new MBeanAttributeInfo(name, "boolean", desc, true, true, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static MBeanServer getMbeanServer() {
        Object object = synchThis;
        synchronized (object) {
            if (mbeanServer != null) {
                return mbeanServer;
            }
            mbeanServer = MBeanServerFactory.findMBeanServer(null).iterator().next();
        }
        return mbeanServer;
    }
}

