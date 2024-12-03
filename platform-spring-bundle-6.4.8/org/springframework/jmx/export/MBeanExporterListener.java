/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export;

import javax.management.ObjectName;

public interface MBeanExporterListener {
    public void mbeanRegistered(ObjectName var1);

    public void mbeanUnregistered(ObjectName var1);
}

