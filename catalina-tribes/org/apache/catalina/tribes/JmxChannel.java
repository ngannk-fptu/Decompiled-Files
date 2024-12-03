/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import javax.management.MBeanRegistration;

public interface JmxChannel
extends MBeanRegistration {
    public boolean isJmxEnabled();

    public void setJmxEnabled(boolean var1);

    public String getJmxDomain();

    public void setJmxDomain(String var1);

    public String getJmxPrefix();

    public void setJmxPrefix(String var1);
}

