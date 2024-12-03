/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.NamingResources
 */
package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import org.apache.catalina.mbeans.BaseCatalinaMBean;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.NamingResources;

public class ContextEnvironmentMBean
extends BaseCatalinaMBean<ContextEnvironment> {
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        super.setAttribute(attribute);
        ContextEnvironment ce = (ContextEnvironment)this.doGetManagedResource();
        NamingResources nr = ce.getNamingResources();
        nr.removeEnvironment(ce.getName());
        nr.addEnvironment(ce);
    }
}

