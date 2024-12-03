/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;

public interface DeploymentQueryMBean {
    public WSDDGlobalConfiguration findGlobalConfig();

    public WSDDHandler findHandler(String var1);

    public WSDDHandler[] findHandlers();

    public WSDDService findService(String var1);

    public WSDDService[] findServices();

    public WSDDTransport findTransport(String var1);

    public WSDDTransport[] findTransports();

    public String[] listServices() throws AxisFault, ConfigurationException;
}

