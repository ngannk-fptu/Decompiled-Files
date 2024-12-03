/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jmx.export.MBeanExportOperations
 */
package com.atlassian.confluence.jmx;

import javax.management.ObjectName;
import org.springframework.jmx.export.MBeanExportOperations;

public interface MBeanExporterWithUnregister
extends MBeanExportOperations {
    public static final String PROPERTY_NAME_JMX_DISABLED = "confluence.jmx.disabled";

    public boolean isEnabled();

    @Deprecated
    public void unregisterBean(ObjectName var1);

    public boolean isRegistered(ObjectName var1);

    public void safeRegisterManagedResource(Object var1, ObjectName var2);
}

