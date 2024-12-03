/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.v2.schemagen.Util
 *  com.sun.xml.txw2.TXW
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.output.ResultFactory
 *  com.sun.xml.txw2.output.TXWResult
 *  com.sun.xml.txw2.output.XmlSerializer
 *  javax.jws.soap.SOAPBinding$Style
 *  javax.jws.soap.SOAPBinding$Use
 *  javax.xml.bind.SchemaOutputResolver
 *  javax.xml.ws.Holder
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.wsdl.writer;

import com.oracle.webservices.api.databinding.WSDLResolver;
import com.sun.xml.bind.v2.schemagen.Util;
import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.output.ResultFactory;
import com.sun.xml.txw2.output.TXWResult;
import com.sun.xml.txw2.output.XmlSerializer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.MEP;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.soap.SOAPBinding;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.policy.jaxws.PolicyWSDLGeneratorExtension;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingHelper;
import com.sun.xml.ws.util.RuntimeVersion;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.writer.TXWContentHandler;
import com.sun.xml.ws.wsdl.writer.W3CAddressingMetadataWSDLGeneratorExtension;
import com.sun.xml.ws.wsdl.writer.W3CAddressingWSDLGeneratorExtension;
import com.sun.xml.ws.wsdl.writer.WSDLGeneratorExtensionFacade;
import com.sun.xml.ws.wsdl.writer.document.Binding;
import com.sun.xml.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.ws.wsdl.writer.document.Definitions;
import com.sun.xml.ws.wsdl.writer.document.Fault;
import com.sun.xml.ws.wsdl.writer.document.FaultType;
import com.sun.xml.ws.wsdl.writer.document.Import;
import com.sun.xml.ws.wsdl.writer.document.Message;
import com.sun.xml.ws.wsdl.writer.document.Operation;
import com.sun.xml.ws.wsdl.writer.document.ParamType;
import com.sun.xml.ws.wsdl.writer.document.Part;
import com.sun.xml.ws.wsdl.writer.document.Port;
import com.sun.xml.ws.wsdl.writer.document.PortType;
import com.sun.xml.ws.wsdl.writer.document.Service;
import com.sun.xml.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.ws.wsdl.writer.document.Types;
import com.sun.xml.ws.wsdl.writer.document.soap.Body;
import com.sun.xml.ws.wsdl.writer.document.soap.BodyType;
import com.sun.xml.ws.wsdl.writer.document.soap.Header;
import com.sun.xml.ws.wsdl.writer.document.soap12.SOAPAddress;
import com.sun.xml.ws.wsdl.writer.document.soap12.SOAPFault;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WSDLGenerator {
    private JAXWSOutputSchemaResolver resolver;
    private WSDLResolver wsdlResolver = null;
    private AbstractSEIModelImpl model;
    private Definitions serviceDefinitions;
    private Definitions portDefinitions;
    private Types types;
    private static final String DOT_WSDL = ".wsdl";
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String XSD_PREFIX = "xsd";
    private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";
    private static final String SOAP_PREFIX = "soap";
    private static final String SOAP12_PREFIX = "soap12";
    private static final String TNS_PREFIX = "tns";
    private static final String DOCUMENT = "document";
    private static final String RPC = "rpc";
    private static final String LITERAL = "literal";
    private static final String REPLACE_WITH_ACTUAL_URL = "REPLACE_WITH_ACTUAL_URL";
    public static final String XsdNs = "http://www.w3.org/2001/XMLSchema";
    private Set<QName> processedExceptions = new HashSet<QName>();
    private WSBinding binding;
    private String wsdlLocation;
    private String portWSDLID;
    private String schemaPrefix;
    private WSDLGeneratorExtension extension;
    List<WSDLGeneratorExtension> extensionHandlers;
    private String endpointAddress = "REPLACE_WITH_ACTUAL_URL";
    private Container container;
    private final Class implType;
    private boolean inlineSchemas;
    private final boolean disableXmlSecurity;

    public WSDLGenerator(AbstractSEIModelImpl model, WSDLResolver wsdlResolver, WSBinding binding, Container container, Class implType, boolean inlineSchemas, WSDLGeneratorExtension ... extensions) {
        this(model, wsdlResolver, binding, container, implType, inlineSchemas, false, extensions);
    }

    public WSDLGenerator(AbstractSEIModelImpl model, WSDLResolver wsdlResolver, WSBinding binding, Container container, Class implType, boolean inlineSchemas, boolean disableXmlSecurity, WSDLGeneratorExtension ... extensions) {
        WSDLGeneratorExtension[] wsdlGeneratorExtensions;
        this.model = model;
        this.resolver = new JAXWSOutputSchemaResolver();
        this.wsdlResolver = wsdlResolver;
        this.binding = binding;
        this.container = container;
        this.implType = implType;
        this.extensionHandlers = new ArrayList<WSDLGeneratorExtension>();
        this.inlineSchemas = inlineSchemas;
        this.disableXmlSecurity = disableXmlSecurity;
        this.register(new W3CAddressingWSDLGeneratorExtension());
        this.register(new W3CAddressingMetadataWSDLGeneratorExtension());
        this.register(new PolicyWSDLGeneratorExtension());
        if (container != null && (wsdlGeneratorExtensions = container.getSPI(WSDLGeneratorExtension[].class)) != null) {
            WSDLGeneratorExtension[] wSDLGeneratorExtensionArray = wsdlGeneratorExtensions;
            int n = wSDLGeneratorExtensionArray.length;
            for (int i = 0; i < n; ++i) {
                WSDLGeneratorExtension wsdlGeneratorExtension = wSDLGeneratorExtensionArray[i];
                this.register(wsdlGeneratorExtension);
            }
        }
        for (WSDLGeneratorExtension w : extensions) {
            this.register(w);
        }
        this.extension = new WSDLGeneratorExtensionFacade(this.extensionHandlers.toArray(new WSDLGeneratorExtension[0]));
    }

    public void setEndpointAddress(String address) {
        this.endpointAddress = address;
    }

    protected String mangleName(String name) {
        return BindingHelper.mangleNameToClassName(name);
    }

    public void doGeneration() {
        CommentFilter portWriter = null;
        String fileName = this.mangleName(this.model.getServiceQName().getLocalPart());
        Result result = this.wsdlResolver.getWSDL(fileName + DOT_WSDL);
        this.wsdlLocation = result.getSystemId();
        CommentFilter serviceWriter = new CommentFilter(ResultFactory.createSerializer((Result)result));
        if (this.model.getServiceQName().getNamespaceURI().equals(this.model.getTargetNamespace())) {
            portWriter = serviceWriter;
            this.schemaPrefix = fileName + "_";
        } else {
            String wsdlName = this.mangleName(this.model.getPortTypeName().getLocalPart());
            if (wsdlName.equals(fileName)) {
                wsdlName = wsdlName + "PortType";
            }
            Holder absWSDLName = new Holder();
            absWSDLName.value = wsdlName + DOT_WSDL;
            result = this.wsdlResolver.getAbstractWSDL((Holder<String>)absWSDLName);
            if (result != null) {
                this.portWSDLID = result.getSystemId();
                portWriter = this.portWSDLID.equals(this.wsdlLocation) ? serviceWriter : new CommentFilter(ResultFactory.createSerializer((Result)result));
            } else {
                this.portWSDLID = (String)absWSDLName.value;
            }
            this.schemaPrefix = new File(this.portWSDLID).getName();
            int idx = this.schemaPrefix.lastIndexOf(46);
            if (idx > 0) {
                this.schemaPrefix = this.schemaPrefix.substring(0, idx);
            }
            this.schemaPrefix = this.mangleName(this.schemaPrefix) + "_";
        }
        this.generateDocument(serviceWriter, portWriter);
    }

    private void generateDocument(XmlSerializer serviceStream, XmlSerializer portStream) {
        this.serviceDefinitions = (Definitions)TXW.create(Definitions.class, (XmlSerializer)serviceStream);
        this.serviceDefinitions._namespace(WSDL_NAMESPACE, "");
        this.serviceDefinitions._namespace("http://www.w3.org/2001/XMLSchema", XSD_PREFIX);
        this.serviceDefinitions.targetNamespace(this.model.getServiceQName().getNamespaceURI());
        this.serviceDefinitions._namespace(this.model.getServiceQName().getNamespaceURI(), TNS_PREFIX);
        if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
            this.serviceDefinitions._namespace(SOAP12_NAMESPACE, SOAP12_PREFIX);
        } else {
            this.serviceDefinitions._namespace(SOAP11_NAMESPACE, SOAP_PREFIX);
        }
        this.serviceDefinitions.name(this.model.getServiceQName().getLocalPart());
        WSDLGenExtnContext serviceCtx = new WSDLGenExtnContext(this.serviceDefinitions, this.model, this.binding, this.container, this.implType);
        this.extension.start(serviceCtx);
        if (serviceStream != portStream && portStream != null) {
            this.portDefinitions = (Definitions)TXW.create(Definitions.class, (XmlSerializer)portStream);
            this.portDefinitions._namespace(WSDL_NAMESPACE, "");
            this.portDefinitions._namespace("http://www.w3.org/2001/XMLSchema", XSD_PREFIX);
            if (this.model.getTargetNamespace() != null) {
                this.portDefinitions.targetNamespace(this.model.getTargetNamespace());
                this.portDefinitions._namespace(this.model.getTargetNamespace(), TNS_PREFIX);
            }
            String schemaLoc = WSDLGenerator.relativize(this.portWSDLID, this.wsdlLocation);
            Import _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
            _import.location(schemaLoc);
        } else if (portStream != null) {
            this.portDefinitions = this.serviceDefinitions;
        } else {
            String schemaLoc = WSDLGenerator.relativize(this.portWSDLID, this.wsdlLocation);
            Import _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
            _import.location(schemaLoc);
        }
        this.extension.addDefinitionsExtension(this.serviceDefinitions);
        if (this.portDefinitions != null) {
            this.generateTypes();
            this.generateMessages();
            this.generatePortType();
        }
        this.generateBinding();
        this.generateService();
        this.extension.end(serviceCtx);
        this.serviceDefinitions.commit();
        if (this.portDefinitions != null && this.portDefinitions != this.serviceDefinitions) {
            this.portDefinitions.commit();
        }
    }

    protected void generateTypes() {
        this.types = this.portDefinitions.types();
        if (this.model.getBindingContext() != null) {
            if (this.inlineSchemas && this.model.getBindingContext().getClass().getName().indexOf("glassfish") == -1) {
                this.resolver.nonGlassfishSchemas = new ArrayList();
            }
            try {
                this.model.getBindingContext().generateSchema(this.resolver);
            }
            catch (IOException e) {
                throw new WebServiceException(e.getMessage());
            }
        }
        if (this.resolver.nonGlassfishSchemas != null) {
            TransformerFactory tf = XmlUtil.newTransformerFactory(!this.disableXmlSecurity);
            try {
                Transformer t = tf.newTransformer();
                for (DOMResult xsd : this.resolver.nonGlassfishSchemas) {
                    Document doc = (Document)xsd.getNode();
                    if (this.inlineSchemas) {
                        NodeList importList = doc.getDocumentElement().getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "import");
                        for (int i = 0; i < importList.getLength(); ++i) {
                            Element impElem = (Element)importList.item(i);
                            impElem.removeAttribute("schemaLocation");
                        }
                    }
                    SAXResult sax = new SAXResult(new TXWContentHandler(this.types));
                    t.transform(new DOMSource(doc.getDocumentElement()), sax);
                }
            }
            catch (TransformerConfigurationException e) {
                throw new WebServiceException(e.getMessage(), (Throwable)e);
            }
            catch (TransformerException e) {
                throw new WebServiceException(e.getMessage(), (Throwable)e);
            }
        }
    }

    protected void generateMessages() {
        for (JavaMethodImpl method : this.model.getJavaMethods()) {
            this.generateSOAPMessages(method, method.getBinding());
        }
    }

    protected void generateSOAPMessages(JavaMethodImpl method, SOAPBinding binding) {
        Part part;
        boolean isDoclit = binding.isDocLit();
        Message message = this.portDefinitions.message().name(method.getRequestMessageName());
        this.extension.addInputMessageExtension(message, method);
        BindingContext jaxbContext = this.model.getBindingContext();
        boolean unwrappable = true;
        for (ParameterImpl param : method.getRequestParameters()) {
            if (isDoclit) {
                if (this.isHeaderParameter(param)) {
                    unwrappable = false;
                }
                part = message.part().name(param.getPartName());
                part.element(param.getName());
                continue;
            }
            if (param.isWrapperStyle()) {
                for (ParameterImpl childParam : ((WrapperParameter)param).getWrapperChildren()) {
                    part = message.part().name(childParam.getPartName());
                    part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
                }
                continue;
            }
            part = message.part().name(param.getPartName());
            part.element(param.getName());
        }
        if (method.getMEP() != MEP.ONE_WAY) {
            message = this.portDefinitions.message().name(method.getResponseMessageName());
            this.extension.addOutputMessageExtension(message, method);
            for (ParameterImpl param : method.getResponseParameters()) {
                if (isDoclit) {
                    part = message.part().name(param.getPartName());
                    part.element(param.getName());
                    continue;
                }
                if (param.isWrapperStyle()) {
                    for (ParameterImpl childParam : ((WrapperParameter)param).getWrapperChildren()) {
                        part = message.part().name(childParam.getPartName());
                        part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
                    }
                    continue;
                }
                part = message.part().name(param.getPartName());
                part.element(param.getName());
            }
        }
        for (CheckedExceptionImpl exception : method.getCheckedExceptions()) {
            QName tagName = exception.getDetailType().tagName;
            String messageName = exception.getMessageName();
            QName messageQName = new QName(this.model.getTargetNamespace(), messageName);
            if (this.processedExceptions.contains(messageQName)) continue;
            message = this.portDefinitions.message().name(messageName);
            this.extension.addFaultMessageExtension(message, method, exception);
            part = message.part().name("fault");
            part.element(tagName);
            this.processedExceptions.add(messageQName);
        }
    }

    protected void generatePortType() {
        PortType portType = this.portDefinitions.portType().name(this.model.getPortTypeName().getLocalPart());
        this.extension.addPortTypeExtension(portType);
        for (JavaMethodImpl method : this.model.getJavaMethods()) {
            Operation operation = portType.operation().name(method.getOperationName());
            this.generateParameterOrder(operation, method);
            this.extension.addOperationExtension(operation, method);
            switch (method.getMEP()) {
                case REQUEST_RESPONSE: {
                    this.generateInputMessage(operation, method);
                    this.generateOutputMessage(operation, method);
                    break;
                }
                case ONE_WAY: {
                    this.generateInputMessage(operation, method);
                    break;
                }
            }
            for (CheckedExceptionImpl exception : method.getCheckedExceptions()) {
                QName messageName = new QName(this.model.getTargetNamespace(), exception.getMessageName());
                FaultType paramType = operation.fault().message(messageName).name(exception.getMessageName());
                this.extension.addOperationFaultExtension(paramType, method, exception);
            }
        }
    }

    protected boolean isWrapperStyle(JavaMethodImpl method) {
        if (method.getRequestParameters().size() > 0) {
            ParameterImpl param = method.getRequestParameters().iterator().next();
            return param.isWrapperStyle();
        }
        return false;
    }

    protected boolean isRpcLit(JavaMethodImpl method) {
        return method.getBinding().getStyle() == SOAPBinding.Style.RPC;
    }

    protected void generateParameterOrder(Operation operation, JavaMethodImpl method) {
        if (method.getMEP() == MEP.ONE_WAY) {
            return;
        }
        if (this.isRpcLit(method)) {
            this.generateRpcParameterOrder(operation, method);
        } else {
            this.generateDocumentParameterOrder(operation, method);
        }
    }

    protected void generateRpcParameterOrder(Operation operation, JavaMethodImpl method) {
        StringBuilder paramOrder = new StringBuilder();
        HashSet<String> partNames = new HashSet<String>();
        List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
        int i = 0;
        for (ParameterImpl parameter : sortedParams) {
            String partName;
            if (parameter.getIndex() < 0 || partNames.contains(partName = parameter.getPartName())) continue;
            if (i++ > 0) {
                paramOrder.append(' ');
            }
            paramOrder.append(partName);
            partNames.add(partName);
        }
        if (i > 1) {
            operation.parameterOrder(paramOrder.toString());
        }
    }

    protected void generateDocumentParameterOrder(Operation operation, JavaMethodImpl method) {
        StringBuilder paramOrder = new StringBuilder();
        HashSet<String> partNames = new HashSet<String>();
        List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
        int i = 0;
        for (ParameterImpl parameter : sortedParams) {
            String partName;
            if (parameter.getIndex() < 0 || partNames.contains(partName = parameter.getPartName())) continue;
            if (i++ > 0) {
                paramOrder.append(' ');
            }
            paramOrder.append(partName);
            partNames.add(partName);
        }
        if (i > 1) {
            operation.parameterOrder(paramOrder.toString());
        }
    }

    protected List<ParameterImpl> sortMethodParameters(JavaMethodImpl method) {
        ParameterImpl param2;
        HashSet<ParameterImpl> paramSet = new HashSet<ParameterImpl>();
        ArrayList<ParameterImpl> sortedParams = new ArrayList<ParameterImpl>();
        if (this.isRpcLit(method)) {
            for (ParameterImpl param2 : method.getRequestParameters()) {
                if (param2 instanceof WrapperParameter) {
                    paramSet.addAll(((WrapperParameter)param2).getWrapperChildren());
                    continue;
                }
                paramSet.add(param2);
            }
            for (ParameterImpl param2 : method.getResponseParameters()) {
                if (param2 instanceof WrapperParameter) {
                    paramSet.addAll(((WrapperParameter)param2).getWrapperChildren());
                    continue;
                }
                paramSet.add(param2);
            }
        } else {
            paramSet.addAll(method.getRequestParameters());
            paramSet.addAll(method.getResponseParameters());
        }
        Iterator params = paramSet.iterator();
        if (paramSet.isEmpty()) {
            return sortedParams;
        }
        param2 = (ParameterImpl)params.next();
        sortedParams.add(param2);
        for (int i = 1; i < paramSet.size(); ++i) {
            int pos;
            param2 = (ParameterImpl)params.next();
            for (pos = 0; pos < i; ++pos) {
                ParameterImpl sortedParam = (ParameterImpl)sortedParams.get(pos);
                if (param2.getIndex() == sortedParam.getIndex() && param2 instanceof WrapperParameter || param2.getIndex() < sortedParam.getIndex()) break;
            }
            sortedParams.add(pos, param2);
        }
        return sortedParams;
    }

    protected boolean isBodyParameter(ParameterImpl parameter) {
        ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isBody();
    }

    protected boolean isHeaderParameter(ParameterImpl parameter) {
        ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isHeader();
    }

    protected boolean isAttachmentParameter(ParameterImpl parameter) {
        ParameterBinding paramBinding = parameter.getBinding();
        return paramBinding.isAttachment();
    }

    protected void generateBinding() {
        Binding newBinding = this.serviceDefinitions.binding().name(this.model.getBoundPortTypeName().getLocalPart());
        this.extension.addBindingExtension(newBinding);
        newBinding.type(this.model.getPortTypeName());
        boolean first = true;
        for (JavaMethodImpl method : this.model.getJavaMethods()) {
            if (first) {
                Object soapBinding;
                SOAPBinding sBinding = method.getBinding();
                SOAPVersion soapVersion = sBinding.getSOAPVersion();
                if (soapVersion == SOAPVersion.SOAP_12) {
                    soapBinding = newBinding.soap12Binding();
                    soapBinding.transport(this.binding.getBindingId().getTransport());
                    if (sBinding.getStyle().equals((Object)SOAPBinding.Style.DOCUMENT)) {
                        soapBinding.style(DOCUMENT);
                    } else {
                        soapBinding.style(RPC);
                    }
                } else {
                    soapBinding = newBinding.soapBinding();
                    soapBinding.transport(this.binding.getBindingId().getTransport());
                    if (sBinding.getStyle().equals((Object)SOAPBinding.Style.DOCUMENT)) {
                        soapBinding.style(DOCUMENT);
                    } else {
                        soapBinding.style(RPC);
                    }
                }
                first = false;
            }
            if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
                this.generateSOAP12BindingOperation(method, newBinding);
                continue;
            }
            this.generateBindingOperation(method, newBinding);
        }
    }

    protected void generateBindingOperation(JavaMethodImpl method, Binding binding) {
        StringBuilder parts;
        BindingOperationType operation = binding.operation().name(method.getOperationName());
        this.extension.addBindingOperationExtension(operation, method);
        String targetNamespace = this.model.getTargetNamespace();
        QName requestMessage = new QName(targetNamespace, method.getOperationName());
        ArrayList<ParameterImpl> bodyParams = new ArrayList<ParameterImpl>();
        ArrayList<ParameterImpl> headerParams = new ArrayList<ParameterImpl>();
        this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
        SOAPBinding soapBinding = method.getBinding();
        operation.soapOperation().soapAction(soapBinding.getSOAPAction());
        StartWithExtensionsType input = operation.input();
        this.extension.addBindingOperationInputExtension(input, method);
        BodyType body = (BodyType)input._element(Body.class);
        boolean isRpc = soapBinding.getStyle().equals((Object)SOAPBinding.Style.RPC);
        if (soapBinding.getUse() == SOAPBinding.Use.LITERAL) {
            body.use(LITERAL);
            if (headerParams.size() > 0) {
                if (bodyParams.size() > 0) {
                    ParameterImpl param = (ParameterImpl)bodyParams.iterator().next();
                    if (isRpc) {
                        parts = new StringBuilder();
                        int i = 0;
                        for (ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                            if (i++ > 0) {
                                parts.append(' ');
                            }
                            parts.append(parameter.getPartName());
                        }
                        body.parts(parts.toString());
                    } else {
                        body.parts(param.getPartName());
                    }
                } else {
                    body.parts("");
                }
                this.generateSOAPHeaders(input, headerParams, requestMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
        } else {
            throw new WebServiceException("encoded use is not supported");
        }
        if (method.getMEP() != MEP.ONE_WAY) {
            bodyParams.clear();
            headerParams.clear();
            this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
            StartWithExtensionsType output = operation.output();
            this.extension.addBindingOperationOutputExtension(output, method);
            body = (BodyType)output._element(Body.class);
            body.use(LITERAL);
            if (headerParams.size() > 0) {
                parts = new StringBuilder();
                if (bodyParams.size() > 0) {
                    ParameterImpl param;
                    ParameterImpl parameterImpl = param = bodyParams.iterator().hasNext() ? (ParameterImpl)bodyParams.iterator().next() : null;
                    if (param != null) {
                        if (isRpc) {
                            int i = 0;
                            for (ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                                if (i++ > 0) {
                                    parts.append(" ");
                                }
                                parts.append(parameter.getPartName());
                            }
                        } else {
                            parts = new StringBuilder(param.getPartName());
                        }
                    }
                }
                body.parts(parts.toString());
                QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
                this.generateSOAPHeaders(output, headerParams, responseMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
        }
        for (CheckedExceptionImpl exception : method.getCheckedExceptions()) {
            Fault fault = operation.fault().name(exception.getMessageName());
            this.extension.addBindingOperationFaultExtension(fault, method, exception);
            com.sun.xml.ws.wsdl.writer.document.soap.SOAPFault soapFault = ((com.sun.xml.ws.wsdl.writer.document.soap.SOAPFault)fault._element(com.sun.xml.ws.wsdl.writer.document.soap.SOAPFault.class)).name(exception.getMessageName());
            soapFault.use(LITERAL);
        }
    }

    protected void generateSOAP12BindingOperation(JavaMethodImpl method, Binding binding) {
        BindingOperationType operation = binding.operation().name(method.getOperationName());
        this.extension.addBindingOperationExtension(operation, method);
        String targetNamespace = this.model.getTargetNamespace();
        QName requestMessage = new QName(targetNamespace, method.getOperationName());
        ArrayList<ParameterImpl> bodyParams = new ArrayList<ParameterImpl>();
        ArrayList<ParameterImpl> headerParams = new ArrayList<ParameterImpl>();
        this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
        SOAPBinding soapBinding = method.getBinding();
        String soapAction = soapBinding.getSOAPAction();
        if (soapAction != null) {
            operation.soap12Operation().soapAction(soapAction);
        }
        StartWithExtensionsType input = operation.input();
        this.extension.addBindingOperationInputExtension(input, method);
        com.sun.xml.ws.wsdl.writer.document.soap12.BodyType body = (com.sun.xml.ws.wsdl.writer.document.soap12.BodyType)input._element(com.sun.xml.ws.wsdl.writer.document.soap12.Body.class);
        boolean isRpc = soapBinding.getStyle().equals((Object)SOAPBinding.Style.RPC);
        if (soapBinding.getUse().equals((Object)SOAPBinding.Use.LITERAL)) {
            body.use(LITERAL);
            if (headerParams.size() > 0) {
                if (bodyParams.size() > 0) {
                    ParameterImpl param = bodyParams.iterator().next();
                    if (isRpc) {
                        StringBuilder parts = new StringBuilder();
                        int i = 0;
                        for (ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                            if (i++ > 0) {
                                parts.append(' ');
                            }
                            parts.append(parameter.getPartName());
                        }
                        body.parts(parts.toString());
                    } else {
                        body.parts(param.getPartName());
                    }
                } else {
                    body.parts("");
                }
                this.generateSOAP12Headers(input, headerParams, requestMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
        } else {
            throw new WebServiceException("encoded use is not supported");
        }
        if (method.getMEP() != MEP.ONE_WAY) {
            bodyParams.clear();
            headerParams.clear();
            this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
            StartWithExtensionsType output = operation.output();
            this.extension.addBindingOperationOutputExtension(output, method);
            body = (com.sun.xml.ws.wsdl.writer.document.soap12.BodyType)output._element(com.sun.xml.ws.wsdl.writer.document.soap12.Body.class);
            body.use(LITERAL);
            if (headerParams.size() > 0) {
                if (bodyParams.size() > 0) {
                    ParameterImpl param = bodyParams.iterator().next();
                    if (isRpc) {
                        StringBuilder parts = new StringBuilder();
                        int i = 0;
                        for (ParameterImpl parameter : ((WrapperParameter)param).getWrapperChildren()) {
                            if (i++ > 0) {
                                parts.append(" ");
                            }
                            parts.append(parameter.getPartName());
                        }
                        body.parts(parts.toString());
                    } else {
                        body.parts(param.getPartName());
                    }
                } else {
                    body.parts("");
                }
                QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
                this.generateSOAP12Headers(output, headerParams, responseMessage);
            }
            if (isRpc) {
                body.namespace(method.getRequestParameters().iterator().next().getName().getNamespaceURI());
            }
        }
        for (CheckedExceptionImpl exception : method.getCheckedExceptions()) {
            Fault fault = operation.fault().name(exception.getMessageName());
            this.extension.addBindingOperationFaultExtension(fault, method, exception);
            SOAPFault soapFault = ((SOAPFault)fault._element(SOAPFault.class)).name(exception.getMessageName());
            soapFault.use(LITERAL);
        }
    }

    protected void splitParameters(List<ParameterImpl> bodyParams, List<ParameterImpl> headerParams, List<ParameterImpl> params) {
        for (ParameterImpl parameter : params) {
            if (this.isBodyParameter(parameter)) {
                bodyParams.add(parameter);
                continue;
            }
            headerParams.add(parameter);
        }
    }

    protected void generateSOAPHeaders(TypedXmlWriter writer, List<ParameterImpl> parameters, QName message) {
        for (ParameterImpl headerParam : parameters) {
            Header header = (Header)writer._element(Header.class);
            header.message(message);
            header.part(headerParam.getPartName());
            header.use(LITERAL);
        }
    }

    protected void generateSOAP12Headers(TypedXmlWriter writer, List<ParameterImpl> parameters, QName message) {
        for (ParameterImpl headerParam : parameters) {
            com.sun.xml.ws.wsdl.writer.document.soap12.Header header = (com.sun.xml.ws.wsdl.writer.document.soap12.Header)writer._element(com.sun.xml.ws.wsdl.writer.document.soap12.Header.class);
            header.message(message);
            header.part(headerParam.getPartName());
            header.use(LITERAL);
        }
    }

    protected void generateService() {
        QName portQName = this.model.getPortName();
        QName serviceQName = this.model.getServiceQName();
        Service service = this.serviceDefinitions.service().name(serviceQName.getLocalPart());
        this.extension.addServiceExtension(service);
        Port port = service.port().name(portQName.getLocalPart());
        port.binding(this.model.getBoundPortTypeName());
        this.extension.addPortExtension(port);
        if (this.model.getJavaMethods().isEmpty()) {
            return;
        }
        if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
            SOAPAddress address = (SOAPAddress)port._element(SOAPAddress.class);
            address.location(this.endpointAddress);
        } else {
            com.sun.xml.ws.wsdl.writer.document.soap.SOAPAddress address = (com.sun.xml.ws.wsdl.writer.document.soap.SOAPAddress)port._element(com.sun.xml.ws.wsdl.writer.document.soap.SOAPAddress.class);
            address.location(this.endpointAddress);
        }
    }

    protected void generateInputMessage(Operation operation, JavaMethodImpl method) {
        ParamType paramType = operation.input();
        this.extension.addOperationInputExtension(paramType, method);
        paramType.message(new QName(this.model.getTargetNamespace(), method.getRequestMessageName()));
    }

    protected void generateOutputMessage(Operation operation, JavaMethodImpl method) {
        ParamType paramType = operation.output();
        this.extension.addOperationOutputExtension(paramType, method);
        paramType.message(new QName(this.model.getTargetNamespace(), method.getResponseMessageName()));
    }

    public Result createOutputFile(String namespaceUri, String suggestedFileName) throws IOException {
        if (namespaceUri == null) {
            return null;
        }
        Holder fileNameHolder = new Holder();
        fileNameHolder.value = this.schemaPrefix + suggestedFileName;
        Result result = this.wsdlResolver.getSchemaOutput(namespaceUri, (Holder<String>)fileNameHolder);
        String schemaLoc = result == null ? (String)fileNameHolder.value : WSDLGenerator.relativize(result.getSystemId(), this.wsdlLocation);
        boolean isEmptyNs = namespaceUri.trim().equals("");
        if (!isEmptyNs) {
            com.sun.xml.ws.wsdl.writer.document.xsd.Import _import = this.types.schema()._import();
            _import.namespace(namespaceUri);
            _import.schemaLocation(schemaLoc);
        }
        return result;
    }

    private Result createInlineSchema(String namespaceUri, String suggestedFileName) throws IOException {
        if (namespaceUri.equals("")) {
            return null;
        }
        TXWResult result = new TXWResult((TypedXmlWriter)this.types);
        result.setSystemId("");
        return result;
    }

    protected static String relativize(String uri, String baseUri) {
        try {
            assert (uri != null);
            if (baseUri == null) {
                return uri;
            }
            URI theUri = new URI(Util.escapeURI((String)uri));
            URI theBaseUri = new URI(Util.escapeURI((String)baseUri));
            if (theUri.isOpaque() || theBaseUri.isOpaque()) {
                return uri;
            }
            if (!Util.equalsIgnoreCase((String)theUri.getScheme(), (String)theBaseUri.getScheme()) || !Util.equal((String)theUri.getAuthority(), (String)theBaseUri.getAuthority())) {
                return uri;
            }
            String uriPath = theUri.getPath();
            String basePath = theBaseUri.getPath();
            if (!basePath.endsWith("/")) {
                basePath = Util.normalizeUriPath((String)basePath);
            }
            if (uriPath.equals(basePath)) {
                return ".";
            }
            String relPath = WSDLGenerator.calculateRelativePath(uriPath, basePath);
            if (relPath == null) {
                return uri;
            }
            StringBuilder relUri = new StringBuilder();
            relUri.append(relPath);
            if (theUri.getQuery() != null) {
                relUri.append('?').append(theUri.getQuery());
            }
            if (theUri.getFragment() != null) {
                relUri.append('#').append(theUri.getFragment());
            }
            return relUri.toString();
        }
        catch (URISyntaxException e) {
            throw new InternalError("Error escaping one of these uris:\n\t" + uri + "\n\t" + baseUri);
        }
    }

    private static String calculateRelativePath(String uri, String base) {
        if (base == null) {
            return null;
        }
        if (uri.startsWith(base)) {
            return uri.substring(base.length());
        }
        return "../" + WSDLGenerator.calculateRelativePath(uri, Util.getParentUriPath((String)base));
    }

    private void register(WSDLGeneratorExtension h) {
        this.extensionHandlers.add(h);
    }

    protected class JAXWSOutputSchemaResolver
    extends SchemaOutputResolver {
        ArrayList<DOMResult> nonGlassfishSchemas = null;

        protected JAXWSOutputSchemaResolver() {
        }

        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
            return WSDLGenerator.this.inlineSchemas ? (this.nonGlassfishSchemas != null ? this.nonGlassfishSchemaResult(namespaceUri, suggestedFileName) : WSDLGenerator.this.createInlineSchema(namespaceUri, suggestedFileName)) : WSDLGenerator.this.createOutputFile(namespaceUri, suggestedFileName);
        }

        private Result nonGlassfishSchemaResult(String namespaceUri, String suggestedFileName) throws IOException {
            DOMResult result = new DOMResult();
            result.setSystemId("");
            this.nonGlassfishSchemas.add(result);
            return result;
        }
    }

    private static class CommentFilter
    implements XmlSerializer {
        final XmlSerializer serializer;
        private static final String VERSION_COMMENT = " Generated by JAX-WS RI (https://github.com/eclipse-ee4j/metro-jax-ws). RI's version is " + RuntimeVersion.VERSION + ". ";

        CommentFilter(XmlSerializer serializer) {
            this.serializer = serializer;
        }

        public void startDocument() {
            this.serializer.startDocument();
            this.comment(new StringBuilder(VERSION_COMMENT));
            this.text(new StringBuilder("\n"));
        }

        public void beginStartTag(String uri, String localName, String prefix) {
            this.serializer.beginStartTag(uri, localName, prefix);
        }

        public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
            this.serializer.writeAttribute(uri, localName, prefix, value);
        }

        public void writeXmlns(String prefix, String uri) {
            this.serializer.writeXmlns(prefix, uri);
        }

        public void endStartTag(String uri, String localName, String prefix) {
            this.serializer.endStartTag(uri, localName, prefix);
        }

        public void endTag() {
            this.serializer.endTag();
        }

        public void text(StringBuilder text) {
            this.serializer.text(text);
        }

        public void cdata(StringBuilder text) {
            this.serializer.cdata(text);
        }

        public void comment(StringBuilder comment) {
            this.serializer.comment(comment);
        }

        public void endDocument() {
            this.serializer.endDocument();
        }

        public void flush() {
            this.serializer.flush();
        }
    }
}

