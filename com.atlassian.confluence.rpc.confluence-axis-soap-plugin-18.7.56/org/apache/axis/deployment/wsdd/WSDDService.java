/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.FaultableHandler;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentsImpl;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.deployment.wsdd.WSDDArrayMapping;
import org.apache.axis.deployment.wsdd.WSDDBeanMapping;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocumentation;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDFaultFlow;
import org.apache.axis.deployment.wsdd.WSDDJAXRPCHandlerInfoChain;
import org.apache.axis.deployment.wsdd.WSDDNonFatalException;
import org.apache.axis.deployment.wsdd.WSDDOperation;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDRequestFlow;
import org.apache.axis.deployment.wsdd.WSDDResponseFlow;
import org.apache.axis.deployment.wsdd.WSDDTargetedChain;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.deployment.wsdd.WSDDTypeMappingContainer;
import org.apache.axis.description.JavaServiceDesc;
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
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDService
extends WSDDTargetedChain
implements WSDDTypeMappingContainer {
    private TypeMappingRegistry tmr = null;
    private Vector faultFlows = new Vector();
    private Vector typeMappings = new Vector();
    private Vector operations = new Vector();
    private Vector namespaces = new Vector();
    private List roles = new ArrayList();
    private String descriptionURL;
    private Style style = Style.DEFAULT;
    private Use use = Use.DEFAULT;
    private transient SOAPService cachedService = null;
    private QName providerQName;
    private WSDDJAXRPCHandlerInfoChain _wsddHIchain;
    JavaServiceDesc desc = new JavaServiceDesc();
    private boolean streaming = false;
    private int sendType = 1;

    public WSDDService() {
    }

    public WSDDService(Element e) throws WSDDException {
        super(e);
        Element hcEl;
        String providerStr;
        Element urlElem;
        Element docElem;
        String attachmentStr;
        String useStr;
        this.desc.setName(this.getQName().getLocalPart());
        String styleStr = e.getAttribute("style");
        if (styleStr != null && !styleStr.equals("")) {
            this.style = Style.getStyle(styleStr, Style.DEFAULT);
            this.desc.setStyle(this.style);
            this.providerQName = this.style.getProvider();
        }
        if ((useStr = e.getAttribute("use")) != null && !useStr.equals("")) {
            this.use = Use.getUse(useStr, Use.DEFAULT);
            this.desc.setUse(this.use);
        } else if (this.style != Style.RPC) {
            this.use = Use.LITERAL;
            this.desc.setUse(this.use);
        }
        String streamStr = e.getAttribute("streaming");
        if (streamStr != null && streamStr.equals("on")) {
            this.streaming = true;
        }
        if ((attachmentStr = e.getAttribute("attachment")) != null && !attachmentStr.equals("")) {
            this.sendType = AttachmentsImpl.getSendType(attachmentStr);
        }
        Element[] operationElements = this.getChildElements(e, "operation");
        for (int i = 0; i < operationElements.length; ++i) {
            WSDDOperation operation = new WSDDOperation(operationElements[i], this.desc);
            this.addOperation(operation);
        }
        Element[] typeMappingElements = this.getChildElements(e, "typeMapping");
        for (int i = 0; i < typeMappingElements.length; ++i) {
            WSDDTypeMapping mapping = new WSDDTypeMapping(typeMappingElements[i]);
            this.typeMappings.add(mapping);
        }
        Element[] beanMappingElements = this.getChildElements(e, "beanMapping");
        for (int i = 0; i < beanMappingElements.length; ++i) {
            WSDDBeanMapping mapping = new WSDDBeanMapping(beanMappingElements[i]);
            this.typeMappings.add(mapping);
        }
        Element[] arrayMappingElements = this.getChildElements(e, "arrayMapping");
        for (int i = 0; i < arrayMappingElements.length; ++i) {
            WSDDArrayMapping mapping = new WSDDArrayMapping(arrayMappingElements[i]);
            this.typeMappings.add(mapping);
        }
        Element[] namespaceElements = this.getChildElements(e, "namespace");
        for (int i = 0; i < namespaceElements.length; ++i) {
            String ns = XMLUtils.getChildCharacterData(namespaceElements[i]);
            this.namespaces.add(ns);
        }
        if (!this.namespaces.isEmpty()) {
            this.desc.setNamespaceMappings(this.namespaces);
        }
        Element[] roleElements = this.getChildElements(e, "role");
        for (int i = 0; i < roleElements.length; ++i) {
            String role = XMLUtils.getChildCharacterData(roleElements[i]);
            this.roles.add(role);
        }
        Element wsdlElem = this.getChildElement(e, "wsdlFile");
        if (wsdlElem != null) {
            String fileName = XMLUtils.getChildCharacterData(wsdlElem);
            this.desc.setWSDLFile(fileName.trim());
        }
        if ((docElem = this.getChildElement(e, "documentation")) != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(docElem);
            this.desc.setDocumentation(documentation.getValue());
        }
        if ((urlElem = this.getChildElement(e, "endpointURL")) != null) {
            String endpointURL = XMLUtils.getChildCharacterData(urlElem);
            this.desc.setEndpointURL(endpointURL);
        }
        if ((providerStr = e.getAttribute("provider")) != null && !providerStr.equals("")) {
            this.providerQName = XMLUtils.getQNameFromString(providerStr, e);
            if (WSDDConstants.QNAME_JAVAMSG_PROVIDER.equals(this.providerQName)) {
                this.desc.setStyle(Style.MESSAGE);
            }
        }
        if ((hcEl = this.getChildElement(e, "handlerInfoChain")) != null) {
            this._wsddHIchain = new WSDDJAXRPCHandlerInfoChain(hcEl);
        }
        this.initTMR();
        this.validateDescriptors();
    }

    protected void initTMR() throws WSDDException {
        if (this.tmr == null) {
            this.createTMR();
            for (int i = 0; i < this.typeMappings.size(); ++i) {
                this.deployTypeMapping((WSDDTypeMapping)this.typeMappings.get(i));
            }
        }
    }

    private void createTMR() {
        this.tmr = new TypeMappingRegistryImpl(false);
        String version = this.getParameter("typeMappingVersion");
        ((TypeMappingRegistryImpl)this.tmr).doRegisterFromVersion(version);
    }

    public void validateDescriptors() throws WSDDException {
        if (this.tmr == null) {
            this.initTMR();
        }
        this.desc.setTypeMappingRegistry(this.tmr);
        this.desc.setTypeMapping(this.getTypeMapping(this.desc.getUse().getEncoding()));
        String allowedMethods = this.getParameter("allowedMethods");
        if (allowedMethods != null && !"*".equals(allowedMethods)) {
            ArrayList<String> methodList = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
            while (tokenizer.hasMoreTokens()) {
                methodList.add(tokenizer.nextToken());
            }
            this.desc.setAllowedMethods(methodList);
        }
    }

    public void addTypeMapping(WSDDTypeMapping mapping) {
        this.typeMappings.add(mapping);
    }

    public void addOperation(WSDDOperation operation) {
        this.operations.add(operation);
        this.desc.addOperationDesc(operation.getOperationDesc());
    }

    protected QName getElementName() {
        return QNAME_SERVICE;
    }

    public String getServiceDescriptionURL() {
        return this.descriptionURL;
    }

    public void setServiceDescriptionURL(String sdUrl) {
        this.descriptionURL = sdUrl;
    }

    public QName getProviderQName() {
        return this.providerQName;
    }

    public void setProviderQName(QName providerQName) {
        this.providerQName = providerQName;
    }

    public ServiceDesc getServiceDesc() {
        return this.desc;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Use getUse() {
        return this.use;
    }

    public void setUse(Use use) {
        this.use = use;
    }

    public WSDDFaultFlow[] getFaultFlows() {
        WSDDFaultFlow[] t = new WSDDFaultFlow[this.faultFlows.size()];
        this.faultFlows.toArray(t);
        return t;
    }

    public Vector getNamespaces() {
        return this.namespaces;
    }

    public WSDDFaultFlow getFaultFlow(QName name) {
        WSDDFaultFlow[] t = this.getFaultFlows();
        for (int n = 0; n < t.length; ++n) {
            if (!t[n].getQName().equals(name)) continue;
            return t[n];
        }
        return null;
    }

    public Handler makeNewInstance(EngineConfiguration registry) throws ConfigurationException {
        if (this.cachedService != null) {
            return this.cachedService;
        }
        this.initTMR();
        Handler reqHandler = null;
        WSDDRequestFlow request = this.getRequestFlow();
        if (request != null) {
            reqHandler = request.getInstance(registry);
        }
        Handler providerHandler = null;
        if (this.providerQName != null) {
            try {
                providerHandler = WSDDProvider.getInstance(this.providerQName, this, registry);
            }
            catch (Exception e) {
                throw new ConfigurationException(e);
            }
            if (providerHandler == null) {
                throw new WSDDException(Messages.getMessage("couldntConstructProvider00"));
            }
        }
        Handler respHandler = null;
        WSDDResponseFlow response = this.getResponseFlow();
        if (response != null) {
            respHandler = response.getInstance(registry);
        }
        SOAPService service = new SOAPService(reqHandler, providerHandler, respHandler);
        service.setStyle(this.style);
        service.setUse(this.use);
        service.setServiceDescription(this.desc);
        service.setHighFidelityRecording(!this.streaming);
        service.setSendType(this.sendType);
        if (this.getQName() != null) {
            service.setName(this.getQName().getLocalPart());
        }
        service.setOptions(this.getParametersTable());
        service.setRoles(this.roles);
        service.setEngine(((WSDDDeployment)registry).getEngine());
        if (this.use != Use.ENCODED) {
            service.setOption("sendMultiRefs", Boolean.FALSE);
            service.setOption("sendXsiTypes", Boolean.FALSE);
        }
        if (this._wsddHIchain != null) {
            HandlerInfoChainFactory hiChainFactory = this._wsddHIchain.getHandlerChainFactory();
            service.setOption("handlerInfoChain", hiChainFactory);
        }
        AxisEngine.normaliseOptions(service);
        WSDDFaultFlow[] faultFlows = this.getFaultFlows();
        if (faultFlows != null && faultFlows.length > 0) {
            FaultableHandler wrapper = new FaultableHandler(service);
            for (int i = 0; i < faultFlows.length; ++i) {
                WSDDFaultFlow flow = faultFlows[i];
                Handler faultHandler = flow.getInstance(registry);
                wrapper.setOption("fault-" + flow.getQName().getLocalPart(), faultHandler);
            }
        }
        try {
            service.getInitializedServiceDesc(MessageContext.getCurrentContext());
        }
        catch (AxisFault axisFault) {
            throw new ConfigurationException(axisFault);
        }
        this.cachedService = service;
        return service;
    }

    public void deployTypeMapping(WSDDTypeMapping mapping) throws WSDDException {
        if (!this.typeMappings.contains(mapping)) {
            this.typeMappings.add(mapping);
        }
        if (this.tmr == null) {
            this.createTMR();
        }
        try {
            String encodingStyle = mapping.getEncodingStyle();
            if (encodingStyle == null) {
                encodingStyle = this.use.getEncoding();
            }
            TypeMapping tm = this.tmr.getOrMakeTypeMapping(encodingStyle);
            this.desc.setTypeMappingRegistry(this.tmr);
            this.desc.setTypeMapping(tm);
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
        int i;
        AttributesImpl attrs = new AttributesImpl();
        QName name = this.getQName();
        if (name != null) {
            attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
        }
        if (this.providerQName != null) {
            attrs.addAttribute("", "provider", "provider", "CDATA", context.qName2String(this.providerQName));
        }
        if (this.style != Style.DEFAULT) {
            attrs.addAttribute("", "style", "style", "CDATA", this.style.getName());
        }
        if (this.use != Use.DEFAULT) {
            attrs.addAttribute("", "use", "use", "CDATA", this.use.getName());
        }
        if (this.streaming) {
            attrs.addAttribute("", "streaming", "streaming", "CDATA", "on");
        }
        if (this.sendType != 1) {
            attrs.addAttribute("", "attachment", "attachment", "CDATA", AttachmentsImpl.getSendTypeString(this.sendType));
        }
        context.startElement(WSDDConstants.QNAME_SERVICE, attrs);
        if (this.desc.getWSDLFile() != null) {
            context.startElement(QNAME_WSDLFILE, null);
            context.writeSafeString(this.desc.getWSDLFile());
            context.endElement();
        }
        if (this.desc.getDocumentation() != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(this.desc.getDocumentation());
            documentation.writeToContext(context);
        }
        for (i = 0; i < this.operations.size(); ++i) {
            WSDDOperation operation = (WSDDOperation)this.operations.elementAt(i);
            operation.writeToContext(context);
        }
        this.writeFlowsToContext(context);
        this.writeParamsToContext(context);
        for (i = 0; i < this.typeMappings.size(); ++i) {
            ((WSDDTypeMapping)this.typeMappings.elementAt(i)).writeToContext(context);
        }
        for (i = 0; i < this.namespaces.size(); ++i) {
            context.startElement(QNAME_NAMESPACE, null);
            context.writeString((String)this.namespaces.get(i));
            context.endElement();
        }
        String endpointURL = this.desc.getEndpointURL();
        if (endpointURL != null) {
            context.startElement(QNAME_ENDPOINTURL, null);
            context.writeSafeString(endpointURL);
            context.endElement();
        }
        if (this._wsddHIchain != null) {
            this._wsddHIchain.writeToContext(context);
        }
        context.endElement();
    }

    public void setCachedService(SOAPService service) {
        this.cachedService = service;
    }

    public Vector getTypeMappings() {
        return this.typeMappings;
    }

    public void setTypeMappings(Vector typeMappings) {
        this.typeMappings = typeMappings;
    }

    public void deployToRegistry(WSDDDeployment registry) {
        registry.addService(this);
        registry.registerNamespaceForService(this.getQName().getLocalPart(), this);
        for (int i = 0; i < this.namespaces.size(); ++i) {
            String namespace = (String)this.namespaces.elementAt(i);
            registry.registerNamespaceForService(namespace, this);
        }
        super.deployToRegistry(registry);
    }

    public void removeNamespaceMappings(WSDDDeployment registry) {
        for (int i = 0; i < this.namespaces.size(); ++i) {
            String namespace = (String)this.namespaces.elementAt(i);
            registry.removeNamespaceMapping(namespace);
        }
        registry.removeNamespaceMapping(this.getQName().getLocalPart());
    }

    public TypeMapping getTypeMapping(String encodingStyle) {
        if (this.tmr == null) {
            return null;
        }
        return this.tmr.getOrMakeTypeMapping(encodingStyle);
    }

    public WSDDJAXRPCHandlerInfoChain getHandlerInfoChain() {
        return this._wsddHIchain;
    }

    public void setHandlerInfoChain(WSDDJAXRPCHandlerInfoChain hichain) {
        this._wsddHIchain = hichain;
    }
}

