/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import javax.management.MBeanRegistration;
import javax.management.ObjectName;

public interface JmxEnabled
extends MBeanRegistration {
    public String getDomain();

    public void setDomain(String var1);

    public ObjectName getObjectName();
}

