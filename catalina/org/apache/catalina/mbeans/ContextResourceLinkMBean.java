/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ContextResourceLink
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
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import org.apache.tomcat.util.res.StringManager;

public class ContextResourceLinkMBean
extends BaseCatalinaMBean<ContextResourceLink> {
    private static final StringManager sm = StringManager.getManager(ContextResourceLinkMBean.class);

    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("mBean.nullName")), sm.getString("mBean.nullName"));
        }
        ContextResourceLink cl = (ContextResourceLink)this.doGetManagedResource();
        String value = null;
        if ("global".equals(name)) {
            return cl.getGlobal();
        }
        if ("description".equals(name)) {
            return cl.getDescription();
        }
        if ("name".equals(name)) {
            return cl.getName();
        }
        if ("type".equals(name)) {
            return cl.getType();
        }
        value = (String)cl.getProperty(name);
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
        ContextResourceLink crl = (ContextResourceLink)this.doGetManagedResource();
        if ("global".equals(name)) {
            crl.setGlobal((String)value);
        } else if ("description".equals(name)) {
            crl.setDescription((String)value);
        } else if ("name".equals(name)) {
            crl.setName((String)value);
        } else if ("type".equals(name)) {
            crl.setType((String)value);
        } else {
            crl.setProperty(name, (Object)("" + value));
        }
        NamingResources nr = crl.getNamingResources();
        nr.removeResourceLink(crl.getName());
        nr.addResourceLink(crl);
    }
}

