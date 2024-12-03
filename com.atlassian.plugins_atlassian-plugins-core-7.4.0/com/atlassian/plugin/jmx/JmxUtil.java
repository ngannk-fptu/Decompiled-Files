/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.jmx;

import com.google.common.collect.ImmutableMap;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxUtil {
    private static final Logger log = LoggerFactory.getLogger(JmxUtil.class);
    private static final String DOMAIN = "com.atlassian.plugin";

    public static ObjectName objectName(AtomicInteger counter, String type) {
        try {
            String instance = Integer.toString(counter.getAndIncrement());
            return new ObjectName(DOMAIN, new Hashtable<String, String>((Map<String, String>)ImmutableMap.of((Object)"instance", (Object)instance, (Object)"type", (Object)type)));
        }
        catch (MalformedObjectNameException emon) {
            log.warn("Failed to create ObjectName: {}", (Object)emon.getMessage());
            return null;
        }
    }

    public static ObjectInstance register(Object object, ObjectName objectName) {
        try {
            if (null != objectName) {
                return ManagementFactory.getPlatformMBeanServer().registerMBean(object, objectName);
            }
            log.warn("Failed to register, objectName null");
        }
        catch (InstanceAlreadyExistsException eiae) {
            log.warn("Failed to register, instance already exists: {}", (Object)eiae.getMessage());
        }
        catch (MBeanRegistrationException emr) {
            log.warn("Failed to register, registration exception: {}", (Object)emr.getMessage());
        }
        catch (NotCompliantMBeanException encm) {
            log.warn("Failed to register, not compliant: {}", (Object)encm.getMessage());
        }
        return null;
    }

    public static boolean unregister(ObjectName objectName) {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
            return true;
        }
        catch (InstanceNotFoundException einf) {
            log.warn("Failed to unregister, instance not found: {}", (Object)einf.getMessage());
        }
        catch (MBeanRegistrationException emr) {
            log.warn("Failed to unregister, registration exception: {}", (Object)emr.getMessage());
        }
        return false;
    }
}

