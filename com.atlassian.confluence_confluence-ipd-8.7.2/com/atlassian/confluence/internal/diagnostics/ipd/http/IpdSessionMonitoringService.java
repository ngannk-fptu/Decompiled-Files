/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.confluence.core.ContextPathHolder;
import java.lang.management.ManagementFactory;
import java.util.Optional;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdSessionMonitoringService {
    public static final long NO_VALUE = -1L;
    static final String TOMCAT_MANAGER_OBJECT_NAME = "*:type=Manager,*";
    private static final Logger log = LoggerFactory.getLogger(IpdSessionMonitoringService.class);
    private final ContextPathHolder contextPathHolder;
    private ObjectName tomcatManagerObjectName;

    public MBeanServer getPlatformMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public IpdSessionMonitoringService(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    public Optional<ObjectName> findTomcatManagerObjectName() {
        if (this.tomcatManagerObjectName != null) {
            return Optional.of(this.tomcatManagerObjectName);
        }
        try {
            ObjectName objectNameQuery = new ObjectName(TOMCAT_MANAGER_OBJECT_NAME);
            this.tomcatManagerObjectName = this.getPlatformMBeanServer().queryNames(objectNameQuery, null).stream().filter(this::hasConfluenceContextPath).findFirst().orElse(null);
            return Optional.ofNullable(this.tomcatManagerObjectName);
        }
        catch (MalformedObjectNameException e) {
            log.error("Can't find objectName", (Throwable)e);
            return Optional.empty();
        }
    }

    private boolean hasConfluenceContextPath(ObjectName objectName) {
        return objectName.getKeyProperty("context").replace("/", "").equals(this.contextPathHolder.getContextPath().replace("/", ""));
    }

    public Optional<Object> getMbeanAttribute(ObjectName name, String attribute) {
        try {
            return Optional.of(this.getPlatformMBeanServer().getAttribute(name, attribute));
        }
        catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            log.error("Can't get attribute", (Throwable)e);
            return Optional.empty();
        }
    }

    public Optional<Object> invokeMbeanOperation(ObjectName name, String operationName, String param) {
        try {
            String[] stringArray;
            Object[] objectArray;
            if (param == null) {
                objectArray = null;
            } else {
                Object[] objectArray2 = new Object[1];
                objectArray = objectArray2;
                objectArray2[0] = param;
            }
            Object[] params = objectArray;
            if (param == null) {
                stringArray = null;
            } else {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = String.class.getName();
            }
            String[] signature = stringArray;
            return Optional.of(this.getPlatformMBeanServer().invoke(name, operationName, params, signature));
        }
        catch (Exception e) {
            log.error("Can't invoke operation", (Throwable)e);
            return Optional.empty();
        }
    }
}

