/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export;

import javax.management.ObjectName;
import org.springframework.jmx.export.MBeanExportException;

public interface MBeanExportOperations {
    public ObjectName registerManagedResource(Object var1) throws MBeanExportException;

    public void registerManagedResource(Object var1, ObjectName var2) throws MBeanExportException;

    public void unregisterManagedResource(ObjectName var1);
}

