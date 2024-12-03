/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.ExtensibilityElement
 *  javax.wsdl.extensions.soap.SOAPAddress
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLReader
 *  javax.xml.rpc.Service
 *  javax.xml.rpc.ServiceFactory
 *  javax.xml.rpc.handler.Handler
 *  javax.xml.rpc.handler.HandlerChain
 *  javax.xml.rpc.handler.HandlerInfo
 *  javax.xml.rpc.handler.HandlerRegistry
 */
package org.apache.naming.factory.webservices;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;
import org.apache.naming.HandlerRef;
import org.apache.naming.ServiceRef;
import org.apache.naming.factory.webservices.ServiceProxy;

public class ServiceRefFactory
implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj instanceof ServiceRef) {
            ServiceRef ref = (ServiceRef)obj;
            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            if (tcl == null) {
                tcl = this.getClass().getClassLoader();
            }
            ServiceFactory factory = ServiceFactory.newInstance();
            Service service = null;
            RefAddr tmp = ref.get("serviceInterface");
            String serviceInterface = null;
            if (tmp != null) {
                serviceInterface = (String)tmp.getContent();
            }
            tmp = ref.get("wsdl");
            String wsdlRefAddr = null;
            if (tmp != null) {
                wsdlRefAddr = (String)tmp.getContent();
            }
            Hashtable<String, QName> portComponentRef = new Hashtable<String, QName>();
            QName serviceQname = null;
            tmp = ref.get("service local part");
            if (tmp != null) {
                String serviceLocalPart = (String)tmp.getContent();
                tmp = ref.get("service namespace");
                if (tmp == null) {
                    serviceQname = new QName(serviceLocalPart);
                } else {
                    String serviceNamespace = (String)tmp.getContent();
                    serviceQname = new QName(serviceNamespace, serviceLocalPart);
                }
            }
            Class<?> serviceInterfaceClass = null;
            if (serviceInterface == null) {
                if (serviceQname == null) {
                    throw new NamingException("Could not create service-ref instance");
                }
                try {
                    if (wsdlRefAddr == null) {
                        service = factory.createService(serviceQname);
                    }
                    service = factory.createService(new URI(wsdlRefAddr).toURL(), serviceQname);
                }
                catch (Exception e) {
                    NamingException ex = new NamingException("Could not create service");
                    ex.initCause(e);
                    throw ex;
                }
            } else {
                try {
                    serviceInterfaceClass = tcl.loadClass(serviceInterface);
                }
                catch (ClassNotFoundException e) {
                    NamingException ex = new NamingException("Could not load service Interface");
                    ex.initCause(e);
                    throw ex;
                }
                if (serviceInterfaceClass == null) {
                    throw new NamingException("Could not load service Interface");
                }
                try {
                    if (wsdlRefAddr == null) {
                        if (!Service.class.isAssignableFrom(serviceInterfaceClass)) {
                            throw new NamingException("service Interface should extend javax.xml.rpc.Service");
                        }
                        service = factory.loadService(serviceInterfaceClass);
                    } else {
                        service = factory.loadService(new URI(wsdlRefAddr).toURL(), serviceInterfaceClass, new Properties());
                    }
                }
                catch (Exception e) {
                    NamingException ex = new NamingException("Could not create service");
                    ex.initCause(e);
                    throw ex;
                }
            }
            if (service == null) {
                throw new NamingException("Cannot create service object");
            }
            serviceQname = service.getServiceName();
            serviceInterfaceClass = service.getClass();
            if (wsdlRefAddr != null) {
                try {
                    WSDLFactory wsdlfactory = WSDLFactory.newInstance();
                    WSDLReader reader = wsdlfactory.newWSDLReader();
                    reader.setFeature("javax.wsdl.importDocuments", true);
                    Definition def = reader.readWSDL(new URI(wsdlRefAddr).toURL().toExternalForm());
                    javax.wsdl.Service wsdlservice = def.getService(serviceQname);
                    Map ports = wsdlservice.getPorts();
                    Method m = serviceInterfaceClass.getMethod("setEndpointAddress", String.class, String.class);
                    for (String portName : ports.keySet()) {
                        Port port = wsdlservice.getPort(portName);
                        String endpoint = this.getSOAPLocation(port);
                        m.invoke((Object)service, port.getName(), endpoint);
                        portComponentRef.put(endpoint, new QName(port.getName()));
                    }
                }
                catch (Exception e) {
                    if (e instanceof InvocationTargetException) {
                        Throwable cause = e.getCause();
                        if (cause instanceof ThreadDeath) {
                            throw (ThreadDeath)cause;
                        }
                        if (cause instanceof VirtualMachineError) {
                            throw (VirtualMachineError)cause;
                        }
                    }
                    NamingException ex = new NamingException("Error while reading Wsdl File");
                    ex.initCause(e);
                    throw ex;
                }
            }
            ServiceProxy proxy = new ServiceProxy(service);
            for (int i = 0; i < ref.size(); ++i) {
                if (!"serviceendpointinterface".equals(ref.get(i).getType())) continue;
                String serviceendpoint = "";
                String portlink = "";
                serviceendpoint = (String)ref.get(i).getContent();
                if ("portcomponentlink".equals(ref.get(i + 1).getType())) {
                    portlink = (String)ref.get(++i).getContent();
                }
                portComponentRef.put(serviceendpoint, new QName(portlink));
            }
            proxy.setPortComponentRef(portComponentRef);
            Class<?>[] serviceInterfaces = serviceInterfaceClass.getInterfaces();
            Class<?>[] interfaces = Arrays.copyOf(serviceInterfaces, serviceInterfaces.length + 1);
            interfaces[interfaces.length - 1] = Service.class;
            Object proxyInstance = null;
            try {
                proxyInstance = Proxy.newProxyInstance(tcl, interfaces, (InvocationHandler)proxy);
            }
            catch (IllegalArgumentException e) {
                proxyInstance = Proxy.newProxyInstance(tcl, serviceInterfaces, (InvocationHandler)proxy);
            }
            if (ref.getHandlersSize() > 0) {
                HandlerRegistry handlerRegistry = service.getHandlerRegistry();
                ArrayList<String> soaproles = new ArrayList<String>();
                while (ref.getHandlersSize() > 0) {
                    HandlerRef handlerRef = ref.getHandler();
                    HandlerInfo handlerInfo = new HandlerInfo();
                    tmp = handlerRef.get("handlerclass");
                    if (tmp == null || tmp.getContent() == null) break;
                    Class<?> handlerClass = null;
                    try {
                        handlerClass = tcl.loadClass((String)tmp.getContent());
                    }
                    catch (ClassNotFoundException e) {
                        break;
                    }
                    ArrayList<QName> headers = new ArrayList<QName>();
                    Hashtable<String, String> config = new Hashtable<String, String>();
                    ArrayList<String> portNames = new ArrayList<String>();
                    for (int i = 0; i < handlerRef.size(); ++i) {
                        if ("handlerlocalpart".equals(handlerRef.get(i).getType())) {
                            String localpart = "";
                            String namespace = "";
                            localpart = (String)handlerRef.get(i).getContent();
                            if ("handlernamespace".equals(handlerRef.get(i + 1).getType())) {
                                namespace = (String)handlerRef.get(++i).getContent();
                            }
                            QName header = new QName(namespace, localpart);
                            headers.add(header);
                            continue;
                        }
                        if ("handlerparamname".equals(handlerRef.get(i).getType())) {
                            String paramName = "";
                            String paramValue = "";
                            paramName = (String)handlerRef.get(i).getContent();
                            if ("handlerparamvalue".equals(handlerRef.get(i + 1).getType())) {
                                paramValue = (String)handlerRef.get(++i).getContent();
                            }
                            config.put(paramName, paramValue);
                            continue;
                        }
                        if ("handlersoaprole".equals(handlerRef.get(i).getType())) {
                            String soaprole = "";
                            soaprole = (String)handlerRef.get(i).getContent();
                            soaproles.add(soaprole);
                            continue;
                        }
                        if (!"handlerportname".equals(handlerRef.get(i).getType())) continue;
                        String portName = "";
                        portName = (String)handlerRef.get(i).getContent();
                        portNames.add(portName);
                    }
                    handlerInfo.setHandlerClass(handlerClass);
                    handlerInfo.setHeaders(headers.toArray(new QName[0]));
                    handlerInfo.setHandlerConfig(config);
                    if (!portNames.isEmpty()) {
                        for (String portName : portNames) {
                            this.initHandlerChain(new QName(portName), handlerRegistry, handlerInfo, soaproles);
                        }
                        continue;
                    }
                    Enumeration<QName> e = portComponentRef.elements();
                    while (e.hasMoreElements()) {
                        this.initHandlerChain(e.nextElement(), handlerRegistry, handlerInfo, soaproles);
                    }
                }
            }
            return proxyInstance;
        }
        return null;
    }

    private String getSOAPLocation(Port port) {
        String endpoint = null;
        List extensions = port.getExtensibilityElements();
        for (ExtensibilityElement ext : extensions) {
            if (!(ext instanceof SOAPAddress)) continue;
            SOAPAddress addr = (SOAPAddress)ext;
            endpoint = addr.getLocationURI();
        }
        return endpoint;
    }

    private void initHandlerChain(QName portName, HandlerRegistry handlerRegistry, HandlerInfo handlerInfo, List<String> soaprolesToAdd) {
        HandlerChain handlerChain = (HandlerChain)handlerRegistry.getHandlerChain(portName);
        for (Handler handler : handlerChain) {
            handler.init(handlerInfo);
        }
        String[] soaprolesRegistered = handlerChain.getRoles();
        String[] soaproles = new String[soaprolesRegistered.length + soaprolesToAdd.size()];
        for (int i = 0; i < soaprolesRegistered.length; ++i) {
            soaproles[i] = soaprolesRegistered[i];
        }
        for (int j = 0; j < soaprolesToAdd.size(); ++j) {
            soaproles[i + j] = soaprolesToAdd.get(j);
        }
        handlerChain.setRoles(soaproles);
        handlerRegistry.setHandlerChain(portName, (List)handlerChain);
    }
}

