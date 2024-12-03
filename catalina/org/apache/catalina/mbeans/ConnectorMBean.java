/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mbeans.ClassNameMBean;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

public class ConnectorMBean
extends ClassNameMBean<Connector> {
    private static final StringManager sm = StringManager.getManager(ConnectorMBean.class);

    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        Connector connector = (Connector)this.doGetManagedResource();
        return IntrospectionUtils.getProperty((Object)connector, (String)name);
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullAttribute")), sm.getString("mBean.nullAttribute"));
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        Connector connector = (Connector)this.doGetManagedResource();
        IntrospectionUtils.setProperty((Object)connector, (String)name, (String)String.valueOf(value));
    }
}

