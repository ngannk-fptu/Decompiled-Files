/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.management.jmx.WSDDServiceWrapper;
import org.apache.axis.management.jmx.WSDDTransportWrapper;

public interface DeploymentAdministratorMBean {
    public void saveConfiguration();

    public void configureGlobalConfig(WSDDGlobalConfiguration var1);

    public void deployHandler(WSDDHandler var1);

    public void deployService(WSDDServiceWrapper var1);

    public void deployTransport(WSDDTransportWrapper var1);

    public void undeployHandler(String var1);

    public void undeployService(String var1);

    public void undeployTransport(String var1);
}

