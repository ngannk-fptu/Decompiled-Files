/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.NamingResources
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.mbeans.BaseCatalinaMBean;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.res.StringManager;

public class ContextResourceMBean
extends BaseCatalinaMBean<ContextResource> {
    private static final StringManager sm = StringManager.getManager(ContextResourceMBean.class);

    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        ContextResource cr = (ContextResource)this.doGetManagedResource();
        String value = null;
        if ("auth".equals(name)) {
            return cr.getAuth();
        }
        if ("description".equals(name)) {
            return cr.getDescription();
        }
        if ("name".equals(name)) {
            return cr.getName();
        }
        if ("scope".equals(name)) {
            return cr.getScope();
        }
        if ("type".equals(name)) {
            return cr.getType();
        }
        value = (String)cr.getProperty(name);
        if (value == null) {
            throw new AttributeNotFoundException(sm.getString("mBean.attributeNotFound", new Object[]{name}));
        }
        return value;
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
        ContextResource cr = (ContextResource)this.doGetManagedResource();
        if ("auth".equals(name)) {
            cr.setAuth((String)value);
        } else if ("description".equals(name)) {
            cr.setDescription((String)value);
        } else if ("name".equals(name)) {
            cr.setName((String)value);
        } else if ("scope".equals(name)) {
            cr.setScope((String)value);
        } else if ("type".equals(name)) {
            cr.setType((String)value);
        } else {
            cr.setProperty(name, (Object)("" + value));
        }
        NamingResources nr = cr.getNamingResources();
        nr.removeResource(cr.getName());
        nr.addResource(cr);
    }
}

