/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.commons.logging.LogFactory;

public enum MBeans {


    public static <T> boolean registerMBean(String objectName, T mbean) throws MBeanRegistrationException {
        MBeanServer server = MBeans.getMBeanServer();
        try {
            server.registerMBean(mbean, new ObjectName(objectName));
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        catch (NotCompliantMBeanException e) {
            throw new IllegalArgumentException(e);
        }
        catch (InstanceAlreadyExistsException e) {
            LogFactory.getLog(MBeans.class).debug((Object)("Failed to register mbean " + objectName), (Throwable)e);
            return false;
        }
        return true;
    }

    public static <T> boolean unregisterMBean(String objectName) throws MBeanRegistrationException {
        MBeanServer server = MBeans.getMBeanServer();
        try {
            server.unregisterMBean(new ObjectName(objectName));
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        catch (InstanceNotFoundException e) {
            LogFactory.getLog(MBeans.class).debug((Object)("Failed to unregister mbean " + objectName), (Throwable)e);
            return false;
        }
        return true;
    }

    public static boolean isRegistered(String objectName) {
        MBeanServer server = MBeans.getMBeanServer();
        try {
            return server.isRegistered(new ObjectName(objectName));
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static MBeanServer getMBeanServer() {
        ArrayList<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        MBeanServer server = servers.size() > 0 ? (MBeanServer)servers.get(0) : ManagementFactory.getPlatformMBeanServer();
        return server;
    }
}

