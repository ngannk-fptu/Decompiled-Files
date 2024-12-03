/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;

public interface ServiceAdministratorMBean {
    public String getVersion();

    public void start();

    public void stop();

    public void restart();

    public void startService(String var1) throws AxisFault, ConfigurationException;

    public void stopService(String var1) throws AxisFault, ConfigurationException;
}

