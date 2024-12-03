/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Operation
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.soap.SOAPAddress
 */
package org.apache.axis.client;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;
import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.AxisClientProxy;
import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.apache.axis.client.Transport;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.WSDLUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.w3c.dom.Document;

public class Service
implements javax.xml.rpc.Service,
Serializable,
Referenceable {
    private transient AxisEngine engine = null;
    private transient EngineConfiguration config = null;
    private QName serviceName = null;
    private String wsdlLocation = null;
    private javax.wsdl.Service wsdlService = null;
    private boolean maintainSession = false;
    private HandlerRegistryImpl registry = new HandlerRegistryImpl();
    private Parser wsdlParser = null;
    private static HashMap cachedWSDL = new HashMap();
    private static boolean cachingWSDL = true;
    protected Call _call = null;
    private Hashtable transportImpls = new Hashtable();
    static /* synthetic */ Class class$javax$xml$rpc$Service;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$javax$xml$rpc$Stub;

    protected javax.wsdl.Service getWSDLService() {
        return this.wsdlService;
    }

    public Parser getWSDLParser() {
        return this.wsdlParser;
    }

    protected AxisClient getAxisClient() {
        return new AxisClient(this.getEngineConfiguration());
    }

    public Service() {
        this.engine = this.getAxisClient();
    }

    public Service(QName serviceName) {
        this.serviceName = serviceName;
        this.engine = this.getAxisClient();
    }

    public Service(EngineConfiguration engineConfiguration, AxisClient axisClient) {
        this.config = engineConfiguration;
        this.engine = axisClient;
    }

    public Service(EngineConfiguration config) {
        this.config = config;
        this.engine = this.getAxisClient();
    }

    public Service(URL wsdlDoc, QName serviceName) throws ServiceException {
        this.serviceName = serviceName;
        this.engine = this.getAxisClient();
        this.wsdlLocation = wsdlDoc.toString();
        Parser parser = null;
        if (cachingWSDL && (parser = (Parser)cachedWSDL.get(this.wsdlLocation.toString())) != null) {
            this.initService(parser, serviceName);
        } else {
            this.initService(wsdlDoc.toString(), serviceName);
        }
    }

    public Service(Parser parser, QName serviceName) throws ServiceException {
        this.serviceName = serviceName;
        this.engine = this.getAxisClient();
        this.initService(parser, serviceName);
    }

    public Service(String wsdlLocation, QName serviceName) throws ServiceException {
        this.serviceName = serviceName;
        this.wsdlLocation = wsdlLocation;
        this.engine = this.getAxisClient();
        Parser parser = null;
        if (cachingWSDL && (parser = (Parser)cachedWSDL.get(wsdlLocation)) != null) {
            this.initService(parser, serviceName);
        } else {
            this.initService(wsdlLocation, serviceName);
        }
    }

    public Service(InputStream wsdlInputStream, QName serviceName) throws ServiceException {
        this.engine = this.getAxisClient();
        Document doc = null;
        try {
            doc = XMLUtils.newDocument(wsdlInputStream);
        }
        catch (Exception exp) {
            throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
        }
        this.initService(null, doc, serviceName);
    }

    private void initService(String url, QName serviceName) throws ServiceException {
        try {
            Parser parser = new Parser();
            parser.run(url);
            if (cachingWSDL && this.wsdlLocation != null) {
                cachedWSDL.put(url, parser);
            }
            this.initService(parser, serviceName);
        }
        catch (Exception exp) {
            throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp), exp);
        }
    }

    private void initService(String context, Document doc, QName serviceName) throws ServiceException {
        try {
            Parser parser = new Parser();
            parser.run(context, doc);
            this.initService(parser, serviceName);
        }
        catch (Exception exp) {
            throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
        }
    }

    private void initService(Parser parser, QName serviceName) throws ServiceException {
        try {
            this.wsdlParser = parser;
            ServiceEntry serviceEntry = parser.getSymbolTable().getServiceEntry(serviceName);
            if (serviceEntry != null) {
                this.wsdlService = serviceEntry.getService();
            }
            if (this.wsdlService == null) {
                throw new ServiceException(Messages.getMessage("noService00", "" + serviceName));
            }
        }
        catch (Exception exp) {
            throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + exp));
        }
    }

    public Remote getPort(QName portName, Class proxyInterface) throws ServiceException {
        if (this.wsdlService == null) {
            throw new ServiceException(Messages.getMessage("wsdlMissing00"));
        }
        Port port = this.wsdlService.getPort(portName.getLocalPart());
        if (port == null) {
            throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
        }
        Remote stub = this.getGeneratedStub(portName, proxyInterface);
        return stub != null ? stub : this.getPort(null, portName, proxyInterface);
    }

    private Remote getGeneratedStub(QName portName, Class proxyInterface) {
        try {
            String pkg = proxyInterface.getName();
            pkg = pkg.substring(0, pkg.lastIndexOf(46));
            Port port = this.wsdlService.getPort(portName.getLocalPart());
            String binding = port.getBinding().getQName().getLocalPart();
            Class stubClass = ClassUtils.forName(pkg + "." + binding + "Stub");
            if (proxyInterface.isAssignableFrom(stubClass)) {
                Class[] formalArgs = new Class[]{class$javax$xml$rpc$Service == null ? (class$javax$xml$rpc$Service = Service.class$("javax.xml.rpc.Service")) : class$javax$xml$rpc$Service};
                Object[] actualArgs = new Object[]{this};
                Constructor ctor = stubClass.getConstructor(formalArgs);
                Stub stub = (Stub)ctor.newInstance(actualArgs);
                stub._setProperty("javax.xml.rpc.service.endpoint.address", WSDLUtils.getAddressFromPort(port));
                stub.setPortName(portName);
                return (Remote)((Object)stub);
            }
            return null;
        }
        catch (Throwable t) {
            return null;
        }
    }

    public Remote getPort(Class proxyInterface) throws ServiceException {
        Remote stub;
        Port port;
        if (this.wsdlService == null) {
            throw new ServiceException(Messages.getMessage("wsdlMissing00"));
        }
        Map ports = this.wsdlService.getPorts();
        if (ports == null || ports.size() <= 0) {
            throw new ServiceException(Messages.getMessage("noPort00", ""));
        }
        String clazzName = proxyInterface.getName();
        if (clazzName.lastIndexOf(46) != -1) {
            clazzName = clazzName.substring(clazzName.lastIndexOf(46) + 1);
        }
        if ((port = (Port)ports.get(clazzName)) == null) {
            port = (Port)ports.values().iterator().next();
        }
        return (stub = this.getGeneratedStub(new QName(port.getName()), proxyInterface)) != null ? stub : this.getPort(null, new QName(port.getName()), proxyInterface);
    }

    public Remote getPort(String endpoint, Class proxyInterface) throws ServiceException {
        return this.getPort(endpoint, null, proxyInterface);
    }

    private Remote getPort(String endpoint, QName portName, Class proxyInterface) throws ServiceException {
        if (!proxyInterface.isInterface()) {
            throw new ServiceException(Messages.getMessage("mustBeIface00"));
        }
        if (!(class$java$rmi$Remote == null ? (class$java$rmi$Remote = Service.class$("java.rmi.Remote")) : class$java$rmi$Remote).isAssignableFrom(proxyInterface)) {
            throw new ServiceException(Messages.getMessage("mustExtendRemote00"));
        }
        if (this.wsdlParser != null) {
            Port port = this.wsdlService.getPort(portName.getLocalPart());
            if (port == null) {
                throw new ServiceException(Messages.getMessage("noPort00", "" + proxyInterface.getName()));
            }
            Binding binding = port.getBinding();
            SymbolTable symbolTable = this.wsdlParser.getSymbolTable();
            BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
            if (bEntry.getParameters().size() != proxyInterface.getMethods().length) {
                throw new ServiceException(Messages.getMessage("incompatibleSEI00", "" + proxyInterface.getName()));
            }
        }
        try {
            Call call = null;
            if (portName == null) {
                call = (Call)this.createCall();
                if (endpoint != null) {
                    call.setTargetEndpointAddress(new URL(endpoint));
                }
            } else {
                call = (Call)this.createCall(portName);
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            javax.xml.rpc.Stub stub = (javax.xml.rpc.Stub)Proxy.newProxyInstance(classLoader, new Class[]{proxyInterface, class$javax$xml$rpc$Stub == null ? (class$javax$xml$rpc$Stub = Service.class$("javax.xml.rpc.Stub")) : class$javax$xml$rpc$Stub}, (InvocationHandler)new AxisClientProxy(call, portName));
            if (stub instanceof Stub) {
                ((Stub)stub).setPortName(portName);
            }
            return (Remote)((Object)stub);
        }
        catch (Exception e) {
            throw new ServiceException(Messages.getMessage("wsdlError00", "", "\n" + e));
        }
    }

    public javax.xml.rpc.Call createCall(QName portName) throws ServiceException {
        Call call = (Call)this.createCall();
        call.setPortName(portName);
        if (this.wsdlParser == null) {
            return call;
        }
        Port port = this.wsdlService.getPort(portName.getLocalPart());
        if (port == null) {
            throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
        }
        Binding binding = port.getBinding();
        PortType portType = binding.getPortType();
        if (portType == null) {
            throw new ServiceException(Messages.getMessage("noPortType00", "" + portName));
        }
        List list = port.getExtensibilityElements();
        for (int i = 0; list != null && i < list.size(); ++i) {
            Object obj = list.get(i);
            if (!(obj instanceof SOAPAddress)) continue;
            try {
                SOAPAddress addr = (SOAPAddress)obj;
                URL url = new URL(addr.getLocationURI());
                call.setTargetEndpointAddress(url);
                continue;
            }
            catch (Exception exp) {
                throw new ServiceException(Messages.getMessage("cantSetURI00", "" + exp));
            }
        }
        return call;
    }

    public javax.xml.rpc.Call createCall(QName portName, String operationName) throws ServiceException {
        Call call = (Call)this.createCall();
        call.setOperation(portName, operationName);
        return call;
    }

    public javax.xml.rpc.Call createCall(QName portName, QName operationName) throws ServiceException {
        Call call = (Call)this.createCall();
        call.setOperation(portName, operationName);
        return call;
    }

    public javax.xml.rpc.Call createCall() throws ServiceException {
        this._call = new Call(this);
        return this._call;
    }

    public javax.xml.rpc.Call[] getCalls(QName portName) throws ServiceException {
        if (portName == null) {
            throw new ServiceException(Messages.getMessage("badPort00"));
        }
        if (this.wsdlService == null) {
            throw new ServiceException(Messages.getMessage("wsdlMissing00"));
        }
        Port port = this.wsdlService.getPort(portName.getLocalPart());
        if (port == null) {
            throw new ServiceException(Messages.getMessage("noPort00", "" + portName));
        }
        Binding binding = port.getBinding();
        SymbolTable symbolTable = this.wsdlParser.getSymbolTable();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        Iterator i = bEntry.getParameters().keySet().iterator();
        Vector<javax.xml.rpc.Call> calls = new Vector<javax.xml.rpc.Call>();
        while (i.hasNext()) {
            Operation operation = (Operation)i.next();
            javax.xml.rpc.Call call = this.createCall(QName.valueOf(port.getName()), QName.valueOf(operation.getName()));
            calls.add(call);
        }
        javax.xml.rpc.Call[] array = new javax.xml.rpc.Call[calls.size()];
        calls.toArray(array);
        return array;
    }

    public HandlerRegistry getHandlerRegistry() {
        return this.registry;
    }

    public URL getWSDLDocumentLocation() {
        try {
            return new URL(this.wsdlLocation);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public QName getServiceName() {
        if (this.serviceName != null) {
            return this.serviceName;
        }
        if (this.wsdlService == null) {
            return null;
        }
        QName qn = this.wsdlService.getQName();
        return new QName(qn.getNamespaceURI(), qn.getLocalPart());
    }

    public Iterator getPorts() throws ServiceException {
        if (this.wsdlService == null) {
            throw new ServiceException(Messages.getMessage("wsdlMissing00"));
        }
        if (this.wsdlService.getPorts() == null) {
            return new Vector().iterator();
        }
        Map portmap = this.wsdlService.getPorts();
        ArrayList<QName> portlist = new ArrayList<QName>(portmap.size());
        Iterator portiterator = portmap.values().iterator();
        while (portiterator.hasNext()) {
            Port port = (Port)portiterator.next();
            portlist.add(new QName(this.wsdlService.getQName().getNamespaceURI(), port.getName()));
        }
        return portlist.iterator();
    }

    public void setTypeMappingRegistry(TypeMappingRegistry registry) throws ServiceException {
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        return this.engine.getTypeMappingRegistry();
    }

    public Reference getReference() {
        String classname = this.getClass().getName();
        Reference reference = new Reference(classname, "org.apache.axis.client.ServiceFactory", null);
        StringRefAddr addr = null;
        if (!classname.equals("org.apache.axis.client.Service")) {
            addr = new StringRefAddr("service classname", classname);
            reference.add(addr);
        } else {
            QName serviceName;
            if (this.wsdlLocation != null) {
                addr = new StringRefAddr("WSDL location", this.wsdlLocation.toString());
                reference.add(addr);
            }
            if ((serviceName = this.getServiceName()) != null) {
                addr = new StringRefAddr("service namespace", serviceName.getNamespaceURI());
                reference.add(addr);
                addr = new StringRefAddr("service local part", serviceName.getLocalPart());
                reference.add(addr);
            }
        }
        if (this.maintainSession) {
            addr = new StringRefAddr("maintain session", "true");
            reference.add(addr);
        }
        return reference;
    }

    public void setEngine(AxisEngine engine) {
        this.engine = engine;
    }

    public AxisEngine getEngine() {
        return this.engine;
    }

    public void setEngineConfiguration(EngineConfiguration config) {
        this.config = config;
    }

    protected EngineConfiguration getEngineConfiguration() {
        if (this.config == null) {
            this.config = EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
        }
        return this.config;
    }

    public void setMaintainSession(boolean yesno) {
        this.maintainSession = yesno;
    }

    public boolean getMaintainSession() {
        return this.maintainSession;
    }

    public Call getCall() throws ServiceException {
        return this._call;
    }

    public boolean getCacheWSDL() {
        return cachingWSDL;
    }

    public void setCacheWSDL(boolean flag) {
        cachingWSDL = flag;
    }

    void registerTransportForURL(URL url, Transport transport) {
        this.transportImpls.put(url.toString(), transport);
    }

    Transport getTransportForURL(URL url) {
        return (Transport)this.transportImpls.get(url.toString());
    }

    public void setTypeMappingVersion(String version) {
        ((TypeMappingRegistryImpl)this.getTypeMappingRegistry()).doRegisterFromVersion(version);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected static class HandlerRegistryImpl
    implements HandlerRegistry {
        Map map = new HashMap();

        protected HandlerRegistryImpl() {
        }

        public List getHandlerChain(QName portName) {
            String key = portName.getLocalPart();
            ArrayList list = (ArrayList)this.map.get(key);
            if (list == null) {
                list = new ArrayList();
                this.setHandlerChain(portName, list);
            }
            return list;
        }

        public void setHandlerChain(QName portName, List chain) {
            this.map.put(portName.getLocalPart(), chain);
        }
    }
}

