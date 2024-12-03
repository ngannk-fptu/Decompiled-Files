/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Version;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.management.jmx.ServiceAdministratorMBean;

public class ServiceAdministrator
implements ServiceAdministratorMBean {
    public void start() {
        ServiceAdmin.start();
    }

    public void stop() {
        ServiceAdmin.stop();
    }

    public void restart() {
        ServiceAdmin.restart();
    }

    public void startService(String serviceName) throws AxisFault, ConfigurationException {
        ServiceAdmin.startService(serviceName);
    }

    public void stopService(String serviceName) throws AxisFault, ConfigurationException {
        ServiceAdmin.stopService(serviceName);
    }

    public String getVersion() {
        return Version.getVersionText();
    }
}

