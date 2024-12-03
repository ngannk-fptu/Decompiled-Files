/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import org.bedework.util.jmx.MBeanInfo;

public interface BaseMBean {
    @MBeanInfo(value="Service name: used to register this service")
    public String getServiceName();

    @MBeanInfo(value="Current status code")
    public String getStatus();

    @MBeanInfo(value="Start the service")
    public void start();

    @MBeanInfo(value="Stop the service")
    public void stop();

    @MBeanInfo(value="Show if service is running")
    public boolean isRunning();
}

