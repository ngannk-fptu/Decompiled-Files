/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PostConstruct
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.handler.PortInfo
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.util.HandlerAnnotationInfo;
import com.sun.xml.ws.util.JAXWSUtils;
import com.sun.xml.ws.util.UtilException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

public class HandlerChainsModel {
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.util");
    private Class annotatedClass;
    private List<HandlerChainType> handlerChains;
    private String id;
    public static final String PROTOCOL_SOAP11_TOKEN = "##SOAP11_HTTP";
    public static final String PROTOCOL_SOAP12_TOKEN = "##SOAP12_HTTP";
    public static final String PROTOCOL_XML_TOKEN = "##XML_HTTP";
    public static final String NS_109 = "http://java.sun.com/xml/ns/javaee";
    public static final QName QNAME_CHAIN_PORT_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "port-name-pattern");
    public static final QName QNAME_CHAIN_PROTOCOL_BINDING = new QName("http://java.sun.com/xml/ns/javaee", "protocol-bindings");
    public static final QName QNAME_CHAIN_SERVICE_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "service-name-pattern");
    public static final QName QNAME_HANDLER_CHAIN = new QName("http://java.sun.com/xml/ns/javaee", "handler-chain");
    public static final QName QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/xml/ns/javaee", "handler-chains");
    public static final QName QNAME_HANDLER = new QName("http://java.sun.com/xml/ns/javaee", "handler");
    public static final QName QNAME_HANDLER_NAME = new QName("http://java.sun.com/xml/ns/javaee", "handler-name");
    public static final QName QNAME_HANDLER_CLASS = new QName("http://java.sun.com/xml/ns/javaee", "handler-class");
    public static final QName QNAME_HANDLER_PARAM = new QName("http://java.sun.com/xml/ns/javaee", "init-param");
    public static final QName QNAME_HANDLER_PARAM_NAME = new QName("http://java.sun.com/xml/ns/javaee", "param-name");
    public static final QName QNAME_HANDLER_PARAM_VALUE = new QName("http://java.sun.com/xml/ns/javaee", "param-value");
    public static final QName QNAME_HANDLER_HEADER = new QName("http://java.sun.com/xml/ns/javaee", "soap-header");
    public static final QName QNAME_HANDLER_ROLE = new QName("http://java.sun.com/xml/ns/javaee", "soap-role");

    private HandlerChainsModel(Class annotatedClass) {
        this.annotatedClass = annotatedClass;
    }

    private List<HandlerChainType> getHandlerChain() {
        if (this.handlerChains == null) {
            this.handlerChains = new ArrayList<HandlerChainType>();
        }
        return this.handlerChains;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public static HandlerChainsModel parseHandlerConfigFile(Class annotatedClass, XMLStreamReader reader) {
        HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CHAINS);
        HandlerChainsModel handlerModel = new HandlerChainsModel(annotatedClass);
        List<HandlerChainType> hChains = handlerModel.getHandlerChain();
        XMLStreamReaderUtil.nextElementContent(reader);
        while (reader.getName().equals(QNAME_HANDLER_CHAIN)) {
            HandlerChainType hChain = new HandlerChainType();
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
                QName portNamePattern = XMLStreamReaderUtil.getElementQName(reader);
                hChain.setPortNamePattern(portNamePattern);
                XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
                String bindingList = XMLStreamReaderUtil.getElementText(reader);
                StringTokenizer stk = new StringTokenizer(bindingList);
                while (stk.hasMoreTokens()) {
                    String token = stk.nextToken();
                    hChain.addProtocolBinding(token);
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
                QName serviceNamepattern = XMLStreamReaderUtil.getElementQName(reader);
                hChain.setServiceNamePattern(serviceNamepattern);
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            List<HandlerType> handlers = hChain.getHandlers();
            while (reader.getName().equals(QNAME_HANDLER)) {
                HandlerType handler = new HandlerType();
                XMLStreamReaderUtil.nextContent(reader);
                if (reader.getName().equals(QNAME_HANDLER_NAME)) {
                    String handlerName = XMLStreamReaderUtil.getElementText(reader).trim();
                    handler.setHandlerName(handlerName);
                    XMLStreamReaderUtil.nextContent(reader);
                }
                HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CLASS);
                String handlerClass = XMLStreamReaderUtil.getElementText(reader).trim();
                handler.setHandlerClass(handlerClass);
                XMLStreamReaderUtil.nextContent(reader);
                while (reader.getName().equals(QNAME_HANDLER_PARAM)) {
                    HandlerChainsModel.skipInitParamElement(reader);
                }
                while (reader.getName().equals(QNAME_HANDLER_HEADER)) {
                    HandlerChainsModel.skipTextElement(reader);
                }
                while (reader.getName().equals(QNAME_HANDLER_ROLE)) {
                    List<String> soapRoles = handler.getSoapRoles();
                    soapRoles.add(XMLStreamReaderUtil.getElementText(reader));
                    XMLStreamReaderUtil.nextContent(reader);
                }
                handlers.add(handler);
                HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER);
                XMLStreamReaderUtil.nextContent(reader);
            }
            HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CHAIN);
            hChains.add(hChain);
            XMLStreamReaderUtil.nextContent(reader);
        }
        return handlerModel;
    }

    public static HandlerAnnotationInfo parseHandlerFile(XMLStreamReader reader, ClassLoader classLoader, QName serviceName, QName portName, WSBinding wsbinding) {
        HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CHAINS);
        String bindingId = wsbinding.getBindingId().toString();
        HandlerAnnotationInfo info = new HandlerAnnotationInfo();
        XMLStreamReaderUtil.nextElementContent(reader);
        ArrayList<Handler> handlerChain = new ArrayList<Handler>();
        HashSet<String> roles = new HashSet<String>();
        while (reader.getName().equals(QNAME_HANDLER_CHAIN)) {
            boolean parseChain;
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
                if (portName == null) {
                    logger.warning("handler chain sepcified for port but port QName passed to parser is null");
                }
                if (!(parseChain = JAXWSUtils.matchQNames(portName, XMLStreamReaderUtil.getElementQName(reader)))) {
                    HandlerChainsModel.skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
                if (bindingId == null) {
                    logger.warning("handler chain sepcified for bindingId but bindingId passed to parser is null");
                }
                String bindingConstraint = XMLStreamReaderUtil.getElementText(reader);
                boolean skipThisChain = true;
                StringTokenizer stk = new StringTokenizer(bindingConstraint);
                ArrayList<String> bindingList = new ArrayList<String>();
                while (stk.hasMoreTokens()) {
                    String tokenOrURI = stk.nextToken();
                    tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
                    String binding = BindingID.parse(tokenOrURI).toString();
                    bindingList.add(binding);
                }
                if (bindingList.contains(bindingId)) {
                    skipThisChain = false;
                }
                if (skipThisChain) {
                    HandlerChainsModel.skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
                if (serviceName == null) {
                    logger.warning("handler chain sepcified for service but service QName passed to parser is null");
                }
                if (!(parseChain = JAXWSUtils.matchQNames(serviceName, XMLStreamReaderUtil.getElementQName(reader)))) {
                    HandlerChainsModel.skipChain(reader);
                    continue;
                }
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            while (reader.getName().equals(QNAME_HANDLER)) {
                Handler handler;
                XMLStreamReaderUtil.nextContent(reader);
                if (reader.getName().equals(QNAME_HANDLER_NAME)) {
                    HandlerChainsModel.skipTextElement(reader);
                }
                HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CLASS);
                try {
                    handler = (Handler)HandlerChainsModel.loadClass(classLoader, XMLStreamReaderUtil.getElementText(reader).trim()).newInstance();
                }
                catch (InstantiationException ie) {
                    throw new RuntimeException(ie);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                XMLStreamReaderUtil.nextContent(reader);
                while (reader.getName().equals(QNAME_HANDLER_PARAM)) {
                    HandlerChainsModel.skipInitParamElement(reader);
                }
                while (reader.getName().equals(QNAME_HANDLER_HEADER)) {
                    HandlerChainsModel.skipTextElement(reader);
                }
                while (reader.getName().equals(QNAME_HANDLER_ROLE)) {
                    roles.add(XMLStreamReaderUtil.getElementText(reader));
                    XMLStreamReaderUtil.nextContent(reader);
                }
                for (Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PostConstruct.class) == null) continue;
                    try {
                        method.invoke((Object)handler, new Object[0]);
                        break;
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                handlerChain.add(handler);
                HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER);
                XMLStreamReaderUtil.nextContent(reader);
            }
            HandlerChainsModel.ensureProperName(reader, QNAME_HANDLER_CHAIN);
            XMLStreamReaderUtil.nextContent(reader);
        }
        info.setHandlers(handlerChain);
        info.setRoles(roles);
        return info;
    }

    public HandlerAnnotationInfo getHandlersForPortInfo(PortInfo info) {
        HandlerAnnotationInfo handlerInfo = new HandlerAnnotationInfo();
        ArrayList<Handler> handlerClassList = new ArrayList<Handler>();
        HashSet<String> roles = new HashSet<String>();
        for (HandlerChainType hchain : this.handlerChains) {
            boolean hchainMatched = false;
            if (!hchain.isConstraintSet() || JAXWSUtils.matchQNames(info.getServiceName(), hchain.getServiceNamePattern()) || JAXWSUtils.matchQNames(info.getPortName(), hchain.getPortNamePattern()) || hchain.getProtocolBindings().contains(info.getBindingID())) {
                hchainMatched = true;
            }
            if (!hchainMatched) continue;
            for (HandlerType handler : hchain.getHandlers()) {
                try {
                    Handler handlerClass = (Handler)HandlerChainsModel.loadClass(this.annotatedClass.getClassLoader(), handler.getHandlerClass()).newInstance();
                    HandlerChainsModel.callHandlerPostConstruct(handlerClass);
                    handlerClassList.add(handlerClass);
                }
                catch (InstantiationException ie) {
                    throw new RuntimeException(ie);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                roles.addAll(handler.getSoapRoles());
            }
        }
        handlerInfo.setHandlers(handlerClassList);
        handlerInfo.setRoles(roles);
        return handlerInfo;
    }

    private static Class loadClass(ClassLoader loader, String name) {
        try {
            return Class.forName(name, true, loader);
        }
        catch (ClassNotFoundException e) {
            throw new UtilException("util.handler.class.not.found", name);
        }
    }

    private static void callHandlerPostConstruct(Object handlerClass) {
        for (Method method : handlerClass.getClass().getMethods()) {
            if (method.getAnnotation(PostConstruct.class) == null) continue;
            try {
                method.invoke(handlerClass, new Object[0]);
                break;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void skipChain(XMLStreamReader reader) {
        while (XMLStreamReaderUtil.nextContent(reader) != 2 || !reader.getName().equals(QNAME_HANDLER_CHAIN)) {
        }
        XMLStreamReaderUtil.nextElementContent(reader);
    }

    private static void skipTextElement(XMLStreamReader reader) {
        XMLStreamReaderUtil.nextContent(reader);
        XMLStreamReaderUtil.nextElementContent(reader);
        XMLStreamReaderUtil.nextElementContent(reader);
    }

    private static void skipInitParamElement(XMLStreamReader reader) {
        int state;
        while ((state = XMLStreamReaderUtil.nextContent(reader)) != 2 || !reader.getName().equals(QNAME_HANDLER_PARAM)) {
        }
        XMLStreamReaderUtil.nextElementContent(reader);
    }

    private static void ensureProperName(XMLStreamReader reader, QName expectedName) {
        if (!reader.getName().equals(expectedName)) {
            HandlerChainsModel.failWithLocalName("util.parser.wrong.element", reader, expectedName.getLocalPart());
        }
    }

    static void ensureProperName(XMLStreamReader reader, String expectedName) {
        if (!reader.getLocalName().equals(expectedName)) {
            HandlerChainsModel.failWithLocalName("util.parser.wrong.element", reader, expectedName);
        }
    }

    private static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
        throw new UtilException(key, Integer.toString(reader.getLocation().getLineNumber()), reader.getLocalName(), arg);
    }

    static class HandlerType {
        String handlerName;
        String handlerClass;
        List<String> soapRoles;
        String id;

        public String getHandlerName() {
            return this.handlerName;
        }

        public void setHandlerName(String value) {
            this.handlerName = value;
        }

        public String getHandlerClass() {
            return this.handlerClass;
        }

        public void setHandlerClass(String value) {
            this.handlerClass = value;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String value) {
            this.id = value;
        }

        public List<String> getSoapRoles() {
            if (this.soapRoles == null) {
                this.soapRoles = new ArrayList<String>();
            }
            return this.soapRoles;
        }
    }

    static class HandlerChainType {
        QName serviceNamePattern;
        QName portNamePattern;
        List<String> protocolBindings = new ArrayList<String>();
        boolean constraintSet = false;
        List<HandlerType> handlers;
        String id;

        public void setServiceNamePattern(QName value) {
            this.serviceNamePattern = value;
            this.constraintSet = true;
        }

        public QName getServiceNamePattern() {
            return this.serviceNamePattern;
        }

        public void setPortNamePattern(QName value) {
            this.portNamePattern = value;
            this.constraintSet = true;
        }

        public QName getPortNamePattern() {
            return this.portNamePattern;
        }

        public List<String> getProtocolBindings() {
            return this.protocolBindings;
        }

        public void addProtocolBinding(String tokenOrURI) {
            tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
            String binding = BindingID.parse(tokenOrURI).toString();
            this.protocolBindings.add(binding);
            this.constraintSet = true;
        }

        public boolean isConstraintSet() {
            return this.constraintSet || !this.protocolBindings.isEmpty();
        }

        public String getId() {
            return this.id;
        }

        public void setId(String value) {
            this.id = value;
        }

        public List<HandlerType> getHandlers() {
            if (this.handlers == null) {
                this.handlers = new ArrayList<HandlerType>();
            }
            return this.handlers;
        }
    }
}

