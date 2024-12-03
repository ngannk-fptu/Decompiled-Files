/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.handlers.soap.SOAPService;

public class SimpleProvider
implements EngineConfiguration {
    HashMap handlers = new HashMap();
    HashMap transports = new HashMap();
    HashMap services = new HashMap();
    Hashtable globalOptions = null;
    Handler globalRequest = null;
    Handler globalResponse = null;
    List roles = new ArrayList();
    TypeMappingRegistry tmr = null;
    EngineConfiguration defaultConfiguration = null;
    private AxisEngine engine;

    public SimpleProvider() {
    }

    public SimpleProvider(EngineConfiguration defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
    }

    public SimpleProvider(TypeMappingRegistry typeMappingRegistry) {
        this.tmr = typeMappingRegistry;
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        this.engine = engine;
        if (this.defaultConfiguration != null) {
            this.defaultConfiguration.configureEngine(engine);
        }
        Iterator i = this.services.values().iterator();
        while (i.hasNext()) {
            ((SOAPService)i.next()).setEngine(engine);
        }
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        if (this.globalOptions != null) {
            return this.globalOptions;
        }
        if (this.defaultConfiguration != null) {
            return this.defaultConfiguration.getGlobalOptions();
        }
        return null;
    }

    public void setGlobalOptions(Hashtable options) {
        this.globalOptions = options;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        if (this.globalRequest != null) {
            return this.globalRequest;
        }
        if (this.defaultConfiguration != null) {
            return this.defaultConfiguration.getGlobalRequest();
        }
        return null;
    }

    public void setGlobalRequest(Handler globalRequest) {
        this.globalRequest = globalRequest;
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        if (this.globalResponse != null) {
            return this.globalResponse;
        }
        if (this.defaultConfiguration != null) {
            return this.defaultConfiguration.getGlobalResponse();
        }
        return null;
    }

    public void setGlobalResponse(Handler globalResponse) {
        this.globalResponse = globalResponse;
    }

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        if (this.tmr != null) {
            return this.tmr;
        }
        if (this.defaultConfiguration != null) {
            return this.defaultConfiguration.getTypeMappingRegistry();
        }
        this.tmr = new TypeMappingRegistryImpl();
        return this.tmr;
    }

    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return (TypeMapping)this.getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    public Handler getTransport(QName qname) throws ConfigurationException {
        Handler transport = (Handler)this.transports.get(qname);
        if (this.defaultConfiguration != null && transport == null) {
            transport = this.defaultConfiguration.getTransport(qname);
        }
        return transport;
    }

    public SOAPService getService(QName qname) throws ConfigurationException {
        SOAPService service = (SOAPService)this.services.get(qname);
        if (this.defaultConfiguration != null && service == null) {
            service = this.defaultConfiguration.getService(qname);
        }
        return service;
    }

    public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException {
        SOAPService service = (SOAPService)this.services.get(new QName("", namespace));
        if (service == null && this.defaultConfiguration != null) {
            service = this.defaultConfiguration.getServiceByNamespaceURI(namespace);
        }
        return service;
    }

    public Handler getHandler(QName qname) throws ConfigurationException {
        Handler handler = (Handler)this.handlers.get(qname);
        if (this.defaultConfiguration != null && handler == null) {
            handler = this.defaultConfiguration.getHandler(qname);
        }
        return handler;
    }

    public void deployService(QName qname, SOAPService service) {
        this.services.put(qname, service);
        if (this.engine != null) {
            service.setEngine(this.engine);
        }
    }

    public void deployService(String name, SOAPService service) {
        this.deployService(new QName(null, name), service);
    }

    public void deployTransport(QName qname, Handler transport) {
        this.transports.put(qname, transport);
    }

    public void deployTransport(String name, Handler transport) {
        this.deployTransport(new QName(null, name), transport);
    }

    public Iterator getDeployedServices() throws ConfigurationException {
        ArrayList<ServiceDesc> serviceDescs = new ArrayList<ServiceDesc>();
        Iterator i = this.services.values().iterator();
        while (i.hasNext()) {
            SOAPService service = (SOAPService)i.next();
            serviceDescs.add(service.getServiceDescription());
        }
        return serviceDescs.iterator();
    }

    public void setRoles(List roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public List getRoles() {
        return this.roles;
    }
}

