/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDArrayMapping;
import org.apache.axis.deployment.wsdd.WSDDBeanMapping;
import org.apache.axis.deployment.wsdd.WSDDChain;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDGlobalConfiguration;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.deployment.wsdd.WSDDNonFatalException;
import org.apache.axis.deployment.wsdd.WSDDRequestFlow;
import org.apache.axis.deployment.wsdd.WSDDResponseFlow;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDTransport;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.deployment.wsdd.WSDDTypeMappingContainer;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BaseSerializerFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

public class WSDDDeployment
extends WSDDElement
implements WSDDTypeMappingContainer,
WSDDEngineConfiguration {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$deployment$wsdd$WSDDDeployment == null ? (class$org$apache$axis$deployment$wsdd$WSDDDeployment = WSDDDeployment.class$("org.apache.axis.deployment.wsdd.WSDDDeployment")) : class$org$apache$axis$deployment$wsdd$WSDDDeployment).getName());
    private HashMap handlers = new HashMap();
    private HashMap services = new HashMap();
    private HashMap transports = new HashMap();
    private HashMap typeMappings = new HashMap();
    private WSDDGlobalConfiguration globalConfig = null;
    private HashMap namespaceToServices = new HashMap();
    private AxisEngine engine;
    TypeMappingRegistry tmr = new TypeMappingRegistryImpl();
    private boolean tmrDeployed = false;
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDDeployment;

    protected void addHandler(WSDDHandler handler) {
        this.handlers.put(handler.getQName(), handler);
    }

    protected void addService(WSDDService service) {
        WSDDService oldService = (WSDDService)this.services.get(service.getQName());
        if (oldService != null) {
            oldService.removeNamespaceMappings(this);
        }
        this.services.put(service.getQName(), service);
    }

    protected void addTransport(WSDDTransport transport) {
        this.transports.put(transport.getQName(), transport);
    }

    public void deployHandler(WSDDHandler handler) {
        handler.deployToRegistry(this);
    }

    public void deployTransport(WSDDTransport transport) {
        transport.deployToRegistry(this);
    }

    public void deployService(WSDDService service) {
        service.deployToRegistry(this);
    }

    public void undeployHandler(QName qname) {
        this.handlers.remove(qname);
    }

    public void undeployService(QName qname) {
        WSDDService service = (WSDDService)this.services.get(qname);
        if (service != null) {
            service.removeNamespaceMappings(this);
            this.services.remove(qname);
        }
    }

    public void undeployTransport(QName qname) {
        this.transports.remove(qname);
    }

    public void deployTypeMapping(WSDDTypeMapping typeMapping) throws WSDDException {
        QName qname = typeMapping.getQName();
        String encoding = typeMapping.getEncodingStyle();
        this.typeMappings.put(qname + encoding, typeMapping);
        if (this.tmrDeployed) {
            this.deployMapping(typeMapping);
        }
    }

    public WSDDDeployment() {
    }

    public WSDDDeployment(Element e) throws WSDDException {
        super(e);
        WSDDTypeMapping mapping;
        int i;
        Element[] elements = this.getChildElements(e, "handler");
        for (i = 0; i < elements.length; ++i) {
            WSDDHandler handler = new WSDDHandler(elements[i]);
            this.deployHandler(handler);
        }
        elements = this.getChildElements(e, "chain");
        for (i = 0; i < elements.length; ++i) {
            WSDDChain chain = new WSDDChain(elements[i]);
            this.deployHandler(chain);
        }
        elements = this.getChildElements(e, "transport");
        for (i = 0; i < elements.length; ++i) {
            WSDDTransport transport = new WSDDTransport(elements[i]);
            this.deployTransport(transport);
        }
        elements = this.getChildElements(e, "service");
        for (i = 0; i < elements.length; ++i) {
            try {
                WSDDService service = new WSDDService(elements[i]);
                this.deployService(service);
                continue;
            }
            catch (WSDDNonFatalException ex) {
                log.info((Object)Messages.getMessage("ignoringNonFatalException00"), (Throwable)ex);
                continue;
            }
            catch (WSDDException ex) {
                throw ex;
            }
        }
        elements = this.getChildElements(e, "typeMapping");
        for (i = 0; i < elements.length; ++i) {
            try {
                mapping = new WSDDTypeMapping(elements[i]);
                this.deployTypeMapping(mapping);
                continue;
            }
            catch (WSDDNonFatalException ex) {
                log.info((Object)Messages.getMessage("ignoringNonFatalException00"), (Throwable)ex);
                continue;
            }
            catch (WSDDException ex) {
                throw ex;
            }
        }
        elements = this.getChildElements(e, "beanMapping");
        for (i = 0; i < elements.length; ++i) {
            mapping = new WSDDBeanMapping(elements[i]);
            this.deployTypeMapping(mapping);
        }
        elements = this.getChildElements(e, "arrayMapping");
        for (i = 0; i < elements.length; ++i) {
            mapping = new WSDDArrayMapping(elements[i]);
            this.deployTypeMapping(mapping);
        }
        Element el = this.getChildElement(e, "globalConfiguration");
        if (el != null) {
            this.globalConfig = new WSDDGlobalConfiguration(el);
        }
    }

    protected QName getElementName() {
        return QNAME_DEPLOY;
    }

    public void deployToRegistry(WSDDDeployment target) throws ConfigurationException {
        WSDDGlobalConfiguration global = this.getGlobalConfiguration();
        if (global != null) {
            target.setGlobalConfiguration(global);
        }
        Iterator i = this.handlers.values().iterator();
        while (i.hasNext()) {
            WSDDHandler handler = (WSDDHandler)i.next();
            target.deployHandler(handler);
        }
        i = this.transports.values().iterator();
        while (i.hasNext()) {
            WSDDTransport transport = (WSDDTransport)i.next();
            target.deployTransport(transport);
        }
        i = this.services.values().iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService)i.next();
            service.deployToRegistry(target);
        }
        i = this.typeMappings.values().iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
            target.deployTypeMapping(mapping);
        }
    }

    private void deployMapping(WSDDTypeMapping mapping) throws WSDDException {
        try {
            String encodingStyle = mapping.getEncodingStyle();
            if (encodingStyle == null) {
                encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
            }
            TypeMapping tm = this.tmr.getOrMakeTypeMapping(encodingStyle);
            SerializerFactory ser = null;
            DeserializerFactory deser = null;
            if (mapping.getSerializerName() != null && !mapping.getSerializerName().equals("")) {
                ser = BaseSerializerFactory.createFactory(mapping.getSerializer(), mapping.getLanguageSpecificType(), mapping.getQName());
            }
            if (mapping instanceof WSDDArrayMapping && ser instanceof ArraySerializerFactory) {
                WSDDArrayMapping am = (WSDDArrayMapping)mapping;
                ArraySerializerFactory factory = (ArraySerializerFactory)ser;
                factory.setComponentType(am.getInnerType());
            }
            if (mapping.getDeserializerName() != null && !mapping.getDeserializerName().equals("")) {
                deser = BaseDeserializerFactory.createFactory(mapping.getDeserializer(), mapping.getLanguageSpecificType(), mapping.getQName());
            }
            tm.register(mapping.getLanguageSpecificType(), mapping.getQName(), ser, deser);
        }
        catch (ClassNotFoundException e) {
            log.error((Object)Messages.getMessage("unabletoDeployTypemapping00", mapping.getQName().toString()), (Throwable)e);
            throw new WSDDNonFatalException(e);
        }
        catch (Exception e) {
            throw new WSDDException(e);
        }
    }

    public void writeToContext(SerializationContext context) throws IOException {
        context.registerPrefixForURI("", "http://xml.apache.org/axis/wsdd/");
        context.registerPrefixForURI("java", "http://xml.apache.org/axis/wsdd/providers/java");
        context.startElement(QNAME_DEPLOY, null);
        if (this.globalConfig != null) {
            this.globalConfig.writeToContext(context);
        }
        Iterator i = this.handlers.values().iterator();
        while (i.hasNext()) {
            WSDDHandler handler = (WSDDHandler)i.next();
            handler.writeToContext(context);
        }
        i = this.services.values().iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService)i.next();
            service.writeToContext(context);
        }
        i = this.transports.values().iterator();
        while (i.hasNext()) {
            WSDDTransport transport = (WSDDTransport)i.next();
            transport.writeToContext(context);
        }
        i = this.typeMappings.values().iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
            mapping.writeToContext(context);
        }
        context.endElement();
    }

    public WSDDGlobalConfiguration getGlobalConfiguration() {
        return this.globalConfig;
    }

    public void setGlobalConfiguration(WSDDGlobalConfiguration globalConfig) {
        this.globalConfig = globalConfig;
    }

    public WSDDTypeMapping[] getTypeMappings() {
        WSDDTypeMapping[] t = new WSDDTypeMapping[this.typeMappings.size()];
        this.typeMappings.values().toArray(t);
        return t;
    }

    public WSDDService[] getServices() {
        WSDDService[] serviceArray = new WSDDService[this.services.size()];
        this.services.values().toArray(serviceArray);
        return serviceArray;
    }

    public WSDDService getWSDDService(QName qname) {
        return (WSDDService)this.services.get(qname);
    }

    public Handler getHandler(QName name) throws ConfigurationException {
        WSDDHandler h = (WSDDHandler)this.handlers.get(name);
        if (h != null) {
            return h.getInstance(this);
        }
        return null;
    }

    public Handler getTransport(QName name) throws ConfigurationException {
        WSDDTransport t = (WSDDTransport)this.transports.get(name);
        if (t != null) {
            return t.getInstance(this);
        }
        return null;
    }

    public SOAPService getService(QName name) throws ConfigurationException {
        WSDDService s = (WSDDService)this.services.get(name);
        if (s != null) {
            return (SOAPService)s.getInstance(this);
        }
        return null;
    }

    public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException {
        WSDDService s = (WSDDService)this.namespaceToServices.get(namespace);
        if (s != null) {
            return (SOAPService)s.getInstance(this);
        }
        return null;
    }

    public void configureEngine(AxisEngine engine) throws ConfigurationException {
        this.engine = engine;
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return (TypeMapping)this.getTypeMappingRegistry().getTypeMapping(encodingStyle);
    }

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        if (!this.tmrDeployed) {
            Iterator i = this.typeMappings.values().iterator();
            while (i.hasNext()) {
                WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
                this.deployMapping(mapping);
            }
            this.tmrDeployed = true;
        }
        return this.tmr;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        WSDDRequestFlow reqFlow;
        if (this.globalConfig != null && (reqFlow = this.globalConfig.getRequestFlow()) != null) {
            return reqFlow.getInstance(this);
        }
        return null;
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        WSDDResponseFlow respFlow;
        if (this.globalConfig != null && (respFlow = this.globalConfig.getResponseFlow()) != null) {
            return respFlow.getInstance(this);
        }
        return null;
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        return this.globalConfig.getParametersTable();
    }

    public List getRoles() {
        return this.globalConfig == null ? new ArrayList() : this.globalConfig.getRoles();
    }

    public Iterator getDeployedServices() throws ConfigurationException {
        ArrayList<ServiceDesc> serviceDescs = new ArrayList<ServiceDesc>();
        Iterator i = this.services.values().iterator();
        while (i.hasNext()) {
            WSDDService service = (WSDDService)i.next();
            try {
                service.makeNewInstance(this);
                serviceDescs.add(service.getServiceDesc());
            }
            catch (WSDDNonFatalException ex) {
                log.info((Object)Messages.getMessage("ignoringNonFatalException00"), (Throwable)ex);
            }
        }
        return serviceDescs.iterator();
    }

    public void registerNamespaceForService(String namespace, WSDDService service) {
        this.namespaceToServices.put(namespace, service);
    }

    public void removeNamespaceMapping(String namespace) {
        this.namespaceToServices.remove(namespace);
    }

    public AxisEngine getEngine() {
        return this.engine;
    }

    public WSDDDeployment getDeployment() {
        return this;
    }

    public WSDDHandler[] getHandlers() {
        WSDDHandler[] handlerArray = new WSDDHandler[this.handlers.size()];
        this.handlers.values().toArray(handlerArray);
        return handlerArray;
    }

    public WSDDHandler getWSDDHandler(QName qname) {
        return (WSDDHandler)this.handlers.get(qname);
    }

    public WSDDTransport[] getTransports() {
        WSDDTransport[] transportArray = new WSDDTransport[this.transports.size()];
        this.transports.values().toArray(transportArray);
        return transportArray;
    }

    public WSDDTransport getWSDDTransport(QName qname) {
        return (WSDDTransport)this.transports.get(qname);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

