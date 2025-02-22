/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.mbeans;

import javax.management.MBeanException;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mbeans.BaseCatalinaMBean;

public class ServiceMBean
extends BaseCatalinaMBean<Service> {
    public void addConnector(String address, int port, boolean isAjp, boolean isSSL) throws MBeanException {
        Service service = (Service)this.doGetManagedResource();
        String protocol = isAjp ? "AJP/1.3" : "HTTP/1.1";
        Connector connector = new Connector(protocol);
        if (address != null && address.length() > 0) {
            connector.setProperty("address", address);
        }
        connector.setPort(port);
        connector.setSecure(isSSL);
        connector.setScheme(isSSL ? "https" : "http");
        service.addConnector(connector);
    }

    public void addExecutor(String type) throws MBeanException {
        Service service = (Service)this.doGetManagedResource();
        Executor executor = (Executor)ServiceMBean.newInstance(type);
        service.addExecutor(executor);
    }

    public String[] findConnectors() throws MBeanException {
        Service service = (Service)this.doGetManagedResource();
        Connector[] connectors = service.findConnectors();
        String[] str = new String[connectors.length];
        for (int i = 0; i < connectors.length; ++i) {
            str[i] = connectors[i].toString();
        }
        return str;
    }

    public String[] findExecutors() throws MBeanException {
        Service service = (Service)this.doGetManagedResource();
        Executor[] executors = service.findExecutors();
        String[] str = new String[executors.length];
        for (int i = 0; i < executors.length; ++i) {
            str[i] = executors[i].toString();
        }
        return str;
    }

    public String getExecutor(String name) throws MBeanException {
        Service service = (Service)this.doGetManagedResource();
        Executor executor = service.getExecutor(name);
        if (executor != null) {
            return executor.toString();
        }
        return null;
    }
}

