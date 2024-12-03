/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jmx.export.MBeanExporter
 *  org.springframework.jmx.export.UnableToRegisterMBeanException
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.jmx.MBeanExporterWithUnregister;
import com.atlassian.spring.container.ContainerManager;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.UnableToRegisterMBeanException;

public class JmxUtil {
    private static final Logger log = LoggerFactory.getLogger(JmxUtil.class);

    public static void registerBean(String name, Object value) {
        block3: {
            MBeanExporter exporter = (MBeanExporter)ContainerManager.getComponent((String)"exporter");
            try {
                exporter.registerManagedResource(value, new ObjectName(name));
            }
            catch (MalformedObjectNameException e) {
                throw new IllegalArgumentException("Unable to register object with name : " + name);
            }
            catch (UnableToRegisterMBeanException e1) {
                log.warn("Unable to register bean with name : " + name + " , already exists");
                if (!log.isDebugEnabled()) break block3;
                log.debug("Stacktrace : ", (Throwable)e1);
            }
        }
    }

    public static boolean isPossibleToExposeBeans() {
        if (ContainerManager.isContainerSetup()) {
            MBeanExporterWithUnregister exporter = (MBeanExporterWithUnregister)ContainerManager.getComponent((String)"exporter");
            return exporter.isEnabled();
        }
        return false;
    }

    public static void unregisterBean(String name) {
        block2: {
            MBeanExporterWithUnregister exporter = (MBeanExporterWithUnregister)ContainerManager.getComponent((String)"exporter");
            try {
                exporter.unregisterManagedResource(new ObjectName(name));
            }
            catch (Exception e) {
                log.error("Unable to unregister object with name [ " + name + " ]  due to : " + e.getMessage());
                if (!log.isDebugEnabled()) break block2;
                log.debug("Stacktrace : ", (Throwable)e);
            }
        }
    }
}

