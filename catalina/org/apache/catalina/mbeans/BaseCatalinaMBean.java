/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.modeler.BaseModelMBean
 */
package org.apache.catalina.mbeans;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import org.apache.tomcat.util.modeler.BaseModelMBean;

public abstract class BaseCatalinaMBean<T>
extends BaseModelMBean {
    protected T doGetManagedResource() throws MBeanException {
        try {
            Object resource = this.getManagedResource();
            return (T)resource;
        }
        catch (InstanceNotFoundException | RuntimeOperationsException | InvalidTargetObjectTypeException e) {
            throw new MBeanException(e);
        }
    }

    protected static Object newInstance(String type) throws MBeanException {
        try {
            return Class.forName(type).getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new MBeanException(e);
        }
    }
}

