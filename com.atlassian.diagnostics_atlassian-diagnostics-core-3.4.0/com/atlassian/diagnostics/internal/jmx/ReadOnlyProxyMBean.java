/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.jmx;

import java.util.concurrent.Callable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ReadOnlyProxyMBean
implements DynamicMBean {
    private final ObjectName sourceObjectName;
    private final MBeanServer mBeanServer;

    public ReadOnlyProxyMBean(ObjectName sourceObjectName, MBeanServer mBeanServer) {
        this.sourceObjectName = sourceObjectName;
        this.mBeanServer = mBeanServer;
    }

    @Override
    public Object getAttribute(String attribute) {
        return this.returnNullOnInstanceNotFoundException(() -> this.mBeanServer.getAttribute(this.sourceObjectName, attribute));
    }

    @Override
    public void setAttribute(Attribute attribute) {
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return this.returnNullOnInstanceNotFoundException(() -> this.mBeanServer.getAttributes(this.sourceObjectName, attributes));
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return new AttributeList();
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanAttributeInfo[] sourceObjectAttributes = this.getAttributesInfo();
        MBeanOperationInfo[] operations = new MBeanOperationInfo[]{};
        return new MBeanInfo(ReadOnlyProxyMBean.class.getName(), "Dynamic MBean", sourceObjectAttributes, null, operations, null);
    }

    private MBeanAttributeInfo[] getAttributesInfo() {
        try {
            return this.mBeanServer.getMBeanInfo(this.sourceObjectName).getAttributes();
        }
        catch (Exception e) {
            return new MBeanAttributeInfo[0];
        }
    }

    private <T> T returnNullOnInstanceNotFoundException(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (InstanceNotFoundException instanceNotFoundException) {
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

