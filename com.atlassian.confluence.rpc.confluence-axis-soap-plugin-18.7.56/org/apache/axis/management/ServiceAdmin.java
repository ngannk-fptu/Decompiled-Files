/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.management;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.management.Registrar;
import org.apache.axis.management.jmx.DeploymentAdministrator;
import org.apache.axis.management.jmx.DeploymentQuery;
import org.apache.axis.management.jmx.ServiceAdministrator;
import org.apache.axis.server.AxisServer;

public class ServiceAdmin {
    private static AxisServer axisServer = null;

    public static void startService(String serviceName) throws AxisFault, ConfigurationException {
        AxisServer server = ServiceAdmin.getEngine();
        try {
            SOAPService service = server.getConfig().getService(new QName("", serviceName));
            service.start();
        }
        catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault)configException.getContainedException();
            }
            throw configException;
        }
    }

    public static void stopService(String serviceName) throws AxisFault, ConfigurationException {
        AxisServer server = ServiceAdmin.getEngine();
        try {
            SOAPService service = server.getConfig().getService(new QName("", serviceName));
            service.stop();
        }
        catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault)configException.getContainedException();
            }
            throw configException;
        }
    }

    /*
     * WARNING - void declaration
     */
    public static String[] listServices() throws AxisFault, ConfigurationException {
        void var2_2;
        ArrayList<String> list = new ArrayList<String>();
        AxisServer server = ServiceAdmin.getEngine();
        try {
            Iterator iter = server.getConfig().getDeployedServices();
        }
        catch (ConfigurationException configException) {
            if (configException.getContainedException() instanceof AxisFault) {
                throw (AxisFault)configException.getContainedException();
            }
            throw configException;
        }
        while (var2_2.hasNext()) {
            ServiceDesc sd = (ServiceDesc)var2_2.next();
            String name = sd.getName();
            list.add(name);
        }
        return list.toArray(new String[list.size()]);
    }

    public static AxisServer getEngine() throws AxisFault {
        if (axisServer == null) {
            throw new AxisFault("Unable to locate AxisEngine for ServiceAdmin Object");
        }
        return axisServer;
    }

    public static void setEngine(AxisServer axisSrv, String name) {
        axisServer = axisSrv;
        Registrar.register(new ServiceAdministrator(), "axis:type=server", "ServiceAdministrator");
        Registrar.register(new DeploymentAdministrator(), "axis:type=deploy", "DeploymentAdministrator");
        Registrar.register(new DeploymentQuery(), "axis:type=query", "DeploymentQuery");
    }

    public static void start() {
        if (axisServer != null) {
            axisServer.start();
        }
    }

    public static void stop() {
        if (axisServer != null) {
            axisServer.stop();
        }
    }

    public static void restart() {
        if (axisServer != null) {
            axisServer.stop();
            axisServer.start();
        }
    }

    public static void saveConfiguration() {
        if (axisServer != null) {
            axisServer.saveConfiguration();
        }
    }

    private static WSDDEngineConfiguration getWSDDEngineConfiguration() {
        if (axisServer != null) {
            EngineConfiguration config = axisServer.getConfig();
            if (config instanceof WSDDEngineConfiguration) {
                return (WSDDEngineConfiguration)config;
            }
            throw new RuntimeException("WSDDDeploymentHelper.getWSDDEngineConfiguration(): EngineConguration not of type WSDDEngineConfiguration");
        }
        return null;
    }

    public static void setGlobalConfig(WSDDGlobalConfiguration globalConfig) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().setGlobalConfiguration(globalConfig);
    }

    public static WSDDGlobalConfiguration getGlobalConfig() {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getGlobalConfiguration();
    }

    public static WSDDHandler getHandler(QName qname) {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getWSDDHandler(qname);
    }

    public static WSDDHandler[] getHandlers() {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getHandlers();
    }

    public static WSDDService getService(QName qname) {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getWSDDService(qname);
    }

    public static WSDDService[] getServices() {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getServices();
    }

    public static WSDDTransport getTransport(QName qname) {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getWSDDTransport(qname);
    }

    public static WSDDTransport[] getTransports() {
        return ServiceAdmin.getWSDDEngineConfiguration().getDeployment().getTransports();
    }

    public static void deployHandler(WSDDHandler handler) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().deployHandler(handler);
    }

    public static void deployService(WSDDService service) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().deployService(service);
    }

    public static void deployTransport(WSDDTransport transport) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().deployTransport(transport);
    }

    public static void undeployHandler(QName qname) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().undeployHandler(qname);
    }

    public static void undeployService(QName qname) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().undeployService(qname);
    }

    public static void undeployTransport(QName qname) {
        ServiceAdmin.getWSDDEngineConfiguration().getDeployment().undeployTransport(qname);
    }
}

