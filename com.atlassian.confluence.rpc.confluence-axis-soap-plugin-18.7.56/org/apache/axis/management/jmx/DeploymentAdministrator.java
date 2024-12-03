/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management.jmx;

import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.management.jmx.DeploymentAdministratorMBean;
import org.apache.axis.management.jmx.WSDDServiceWrapper;
import org.apache.axis.management.jmx.WSDDTransportWrapper;

public class DeploymentAdministrator
implements DeploymentAdministratorMBean {
    public void saveConfiguration() {
        ServiceAdmin.saveConfiguration();
    }

    public void configureGlobalConfig(WSDDGlobalConfiguration config) {
        ServiceAdmin.setGlobalConfig(config);
    }

    public void deployHandler(WSDDHandler handler) {
        ServiceAdmin.deployHandler(handler);
    }

    public void deployService(WSDDServiceWrapper service) {
        ServiceAdmin.deployService(service.getWSDDService());
    }

    public void deployTransport(WSDDTransportWrapper transport) {
        ServiceAdmin.deployTransport(transport.getWSDDTransport());
    }

    public void undeployHandler(String qname) {
        ServiceAdmin.undeployHandler(new QName(qname));
    }

    public void undeployService(String qname) {
        ServiceAdmin.undeployService(new QName(qname));
    }

    public void undeployTransport(String qname) {
        ServiceAdmin.undeployTransport(new QName(qname));
    }
}

