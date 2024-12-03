/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.management.jmx.DeploymentQueryMBean;

public class DeploymentQuery
implements DeploymentQueryMBean {
    public WSDDGlobalConfiguration findGlobalConfig() {
        return ServiceAdmin.getGlobalConfig();
    }

    public WSDDHandler findHandler(String qname) {
        return ServiceAdmin.getHandler(new QName(qname));
    }

    public WSDDHandler[] findHandlers() {
        return ServiceAdmin.getHandlers();
    }

    public WSDDService findService(String qname) {
        return ServiceAdmin.getService(new QName(qname));
    }

    public WSDDService[] findServices() {
        return ServiceAdmin.getServices();
    }

    public WSDDTransport findTransport(String qname) {
        return ServiceAdmin.getTransport(new QName(qname));
    }

    public WSDDTransport[] findTransports() {
        return ServiceAdmin.getTransports();
    }

    public String[] listServices() throws AxisFault, ConfigurationException {
        return ServiceAdmin.listServices();
    }
}

