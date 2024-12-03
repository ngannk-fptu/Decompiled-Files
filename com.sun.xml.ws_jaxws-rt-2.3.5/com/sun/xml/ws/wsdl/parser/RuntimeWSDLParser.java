/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.AbstractCreatorProcessor
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferMark
 *  com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator
 *  javax.jws.soap.SOAPBinding$Style
 *  javax.xml.ws.Service
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.BindingIDFactory;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSDLLocator;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.ws.api.wsdl.parser.PolicyWSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.ws.model.wsdl.WSDLBoundFaultImpl;
import com.sun.xml.ws.model.wsdl.WSDLBoundOperationImpl;
import com.sun.xml.ws.model.wsdl.WSDLBoundPortTypeImpl;
import com.sun.xml.ws.model.wsdl.WSDLFaultImpl;
import com.sun.xml.ws.model.wsdl.WSDLInputImpl;
import com.sun.xml.ws.model.wsdl.WSDLMessageImpl;
import com.sun.xml.ws.model.wsdl.WSDLModelImpl;
import com.sun.xml.ws.model.wsdl.WSDLOperationImpl;
import com.sun.xml.ws.model.wsdl.WSDLOutputImpl;
import com.sun.xml.ws.model.wsdl.WSDLPartDescriptorImpl;
import com.sun.xml.ws.model.wsdl.WSDLPartImpl;
import com.sun.xml.ws.model.wsdl.WSDLPortImpl;
import com.sun.xml.ws.model.wsdl.WSDLPortTypeImpl;
import com.sun.xml.ws.model.wsdl.WSDLServiceImpl;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.resources.WsdlmodelMessages;
import com.sun.xml.ws.streaming.SourceReaderFactory;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.EntityResolverWrapper;
import com.sun.xml.ws.wsdl.parser.FoolProofParserExtension;
import com.sun.xml.ws.wsdl.parser.InaccessibleWSDLException;
import com.sun.xml.ws.wsdl.parser.MIMEConstants;
import com.sun.xml.ws.wsdl.parser.MemberSubmissionAddressingWSDLParserExtension;
import com.sun.xml.ws.wsdl.parser.MexEntityResolver;
import com.sun.xml.ws.wsdl.parser.ParserUtil;
import com.sun.xml.ws.wsdl.parser.SOAPConstants;
import com.sun.xml.ws.wsdl.parser.W3CAddressingMetadataWSDLParserExtension;
import com.sun.xml.ws.wsdl.parser.W3CAddressingWSDLParserExtension;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.ws.wsdl.parser.WSDLParserExtensionContextImpl;
import com.sun.xml.ws.wsdl.parser.WSDLParserExtensionFacade;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class RuntimeWSDLParser {
    private final EditableWSDLModel wsdlDoc;
    private String targetNamespace;
    private final Set<String> importedWSDLs = new HashSet<String>();
    private final XMLEntityResolver resolver;
    private final PolicyResolver policyResolver;
    private final WSDLParserExtension extensionFacade;
    private final WSDLParserExtensionContextImpl context;
    List<WSDLParserExtension> extensions;
    Map<String, String> wsdldef_nsdecl = new HashMap<String, String>();
    Map<String, String> service_nsdecl = new HashMap<String, String>();
    Map<String, String> port_nsdecl = new HashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(RuntimeWSDLParser.class.getName());

    public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        return RuntimeWSDLParser.parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, PolicyResolverFactory.create(), extensions);
    }

    public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        return RuntimeWSDLParser.parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, PolicyResolverFactory.create(), extensions);
    }

    public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, @NotNull PolicyResolver policyResolver, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        return RuntimeWSDLParser.parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, policyResolver, extensions);
    }

    public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, @NotNull PolicyResolver policyResolver, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        return RuntimeWSDLParser.parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, policyResolver, false, extensions);
    }

    public static WSDLModel parse(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, @NotNull PolicyResolver policyResolver, boolean isUseStreamFromEntityResolverWrapper, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        XMLEntityResolver.Parser parser;
        assert (resolver != null);
        RuntimeWSDLParser wsdlParser = new RuntimeWSDLParser(wsdlSource.getSystemId(), new EntityResolverWrapper(resolver, isUseStreamFromEntityResolverWrapper), isClientSide, container, policyResolver, extensions);
        try {
            parser = wsdlParser.resolveWSDL(wsdlLoc, wsdlSource, serviceClass);
            if (!RuntimeWSDLParser.hasWSDLDefinitions(parser.parser)) {
                throw new XMLStreamException(ClientMessages.RUNTIME_WSDLPARSER_INVALID_WSDL(parser.systemId, WSDLConstants.QNAME_DEFINITIONS, parser.parser.getName(), parser.parser.getLocation()));
            }
        }
        catch (XMLStreamException e) {
            if (wsdlLoc == null) {
                throw e;
            }
            return RuntimeWSDLParser.tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, e, serviceClass, policyResolver, extensions);
        }
        catch (IOException e) {
            if (wsdlLoc == null) {
                throw e;
            }
            return RuntimeWSDLParser.tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, e, serviceClass, policyResolver, extensions);
        }
        wsdlParser.extensionFacade.start(wsdlParser.context);
        wsdlParser.parseWSDL(parser, false);
        wsdlParser.wsdlDoc.freeze();
        wsdlParser.extensionFacade.finished(wsdlParser.context);
        wsdlParser.extensionFacade.postFinished(wsdlParser.context);
        if (wsdlParser.wsdlDoc.getServices().isEmpty()) {
            throw new WebServiceException(ClientMessages.WSDL_CONTAINS_NO_SERVICE(wsdlLoc));
        }
        return wsdlParser.wsdlDoc;
    }

    private static WSDLModel tryWithMex(@NotNull RuntimeWSDLParser wsdlParser, @NotNull URL wsdlLoc, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Throwable e, Class serviceClass, PolicyResolver policyResolver, WSDLParserExtension ... extensions) throws SAXException, XMLStreamException {
        ArrayList<Throwable> exceptions = new ArrayList<Throwable>();
        try {
            WSDLModel wsdlModel = wsdlParser.parseUsingMex(wsdlLoc, resolver, isClientSide, container, serviceClass, policyResolver, extensions);
            if (wsdlModel == null) {
                throw new WebServiceException(ClientMessages.FAILED_TO_PARSE(wsdlLoc.toExternalForm(), e.getMessage()), e);
            }
            return wsdlModel;
        }
        catch (URISyntaxException e1) {
            exceptions.add(e);
            exceptions.add(e1);
        }
        catch (IOException e1) {
            exceptions.add(e);
            exceptions.add(e1);
        }
        throw new InaccessibleWSDLException(exceptions);
    }

    private WSDLModel parseUsingMex(@NotNull URL wsdlLoc, @NotNull EntityResolver resolver, boolean isClientSide, Container container, Class serviceClass, PolicyResolver policyResolver, WSDLParserExtension[] extensions) throws IOException, SAXException, XMLStreamException, URISyntaxException {
        Object resolverFactory;
        MetaDataResolver mdResolver = null;
        ServiceDescriptor serviceDescriptor = null;
        RuntimeWSDLParser wsdlParser = null;
        Iterator<MetadataResolverFactory> iterator = ServiceFinder.find(MetadataResolverFactory.class).iterator();
        while (iterator.hasNext() && (serviceDescriptor = (mdResolver = ((MetadataResolverFactory)(resolverFactory = iterator.next())).metadataResolver(resolver)).resolve(wsdlLoc.toURI())) == null) {
        }
        if (serviceDescriptor != null) {
            List<? extends Source> wsdls = serviceDescriptor.getWSDLs();
            wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new MexEntityResolver(wsdls), isClientSide, container, policyResolver, extensions);
            wsdlParser.extensionFacade.start(wsdlParser.context);
            for (Source source : wsdls) {
                String systemId = source.getSystemId();
                XMLEntityResolver.Parser parser = wsdlParser.resolver.resolveEntity(null, systemId);
                wsdlParser.parseWSDL(parser, false);
            }
        }
        if ((mdResolver == null || serviceDescriptor == null) && (wsdlLoc.getProtocol().equals("http") || wsdlLoc.getProtocol().equals("https")) && wsdlLoc.getQuery() == null) {
            String urlString = wsdlLoc.toExternalForm();
            urlString = urlString + "?wsdl";
            wsdlLoc = new URL(urlString);
            wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new EntityResolverWrapper(resolver), isClientSide, container, policyResolver, extensions);
            wsdlParser.extensionFacade.start(wsdlParser.context);
            XMLEntityResolver.Parser parser = this.resolveWSDL(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), serviceClass);
            wsdlParser.parseWSDL(parser, false);
        }
        if (wsdlParser == null) {
            return null;
        }
        wsdlParser.wsdlDoc.freeze();
        wsdlParser.extensionFacade.finished(wsdlParser.context);
        wsdlParser.extensionFacade.postFinished(wsdlParser.context);
        return wsdlParser.wsdlDoc;
    }

    private static boolean hasWSDLDefinitions(XMLStreamReader reader) {
        XMLStreamReaderUtil.nextElementContent(reader);
        return reader.getName().equals(WSDLConstants.QNAME_DEFINITIONS);
    }

    public static WSDLModel parse(XMLEntityResolver.Parser wsdl, XMLEntityResolver resolver, boolean isClientSide, Container container, PolicyResolver policyResolver, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        assert (resolver != null);
        RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, policyResolver, extensions);
        parser.extensionFacade.start(parser.context);
        parser.parseWSDL(wsdl, false);
        parser.wsdlDoc.freeze();
        parser.extensionFacade.finished(parser.context);
        parser.extensionFacade.postFinished(parser.context);
        return parser.wsdlDoc;
    }

    public static WSDLModel parse(XMLEntityResolver.Parser wsdl, XMLEntityResolver resolver, boolean isClientSide, Container container, WSDLParserExtension ... extensions) throws IOException, XMLStreamException, SAXException {
        assert (resolver != null);
        RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
        parser.extensionFacade.start(parser.context);
        parser.parseWSDL(wsdl, false);
        parser.wsdlDoc.freeze();
        parser.extensionFacade.finished(parser.context);
        parser.extensionFacade.postFinished(parser.context);
        return parser.wsdlDoc;
    }

    private RuntimeWSDLParser(@NotNull String sourceLocation, XMLEntityResolver resolver, boolean isClientSide, Container container, PolicyResolver policyResolver, WSDLParserExtension ... extensions) {
        this.wsdlDoc = sourceLocation != null ? new WSDLModelImpl(sourceLocation) : new WSDLModelImpl();
        this.resolver = resolver;
        this.policyResolver = policyResolver;
        this.extensions = new ArrayList<WSDLParserExtension>();
        this.context = new WSDLParserExtensionContextImpl(this.wsdlDoc, isClientSide, container, policyResolver);
        boolean isPolicyExtensionFound = false;
        for (WSDLParserExtension e : extensions) {
            if (e instanceof PolicyWSDLParserExtension) {
                isPolicyExtensionFound = true;
            }
            this.register(e);
        }
        if (!isPolicyExtensionFound) {
            this.register(new com.sun.xml.ws.policy.jaxws.PolicyWSDLParserExtension());
        }
        this.register(new MemberSubmissionAddressingWSDLParserExtension());
        this.register(new W3CAddressingWSDLParserExtension());
        this.register(new W3CAddressingMetadataWSDLParserExtension());
        this.extensionFacade = new WSDLParserExtensionFacade(this.extensions.toArray(new WSDLParserExtension[0]));
    }

    private XMLEntityResolver.Parser resolveWSDL(@Nullable URL wsdlLoc, @NotNull Source wsdlSource, Class serviceClass) throws IOException, SAXException, XMLStreamException {
        String ruExForm;
        URL ru;
        String exForm;
        String systemId = wsdlSource.getSystemId();
        XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, systemId);
        if (parser == null && wsdlLoc != null && (parser = this.resolver.resolveEntity(null, exForm = wsdlLoc.toExternalForm())) == null && serviceClass != null && (ru = serviceClass.getResource(".")) != null && exForm.startsWith(ruExForm = ru.toExternalForm())) {
            parser = this.resolver.resolveEntity(null, exForm.substring(ruExForm.length()));
        }
        if (parser == null) {
            if (this.isKnownReadableSource(wsdlSource)) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
            } else if (wsdlLoc != null) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, RuntimeWSDLParser.createReader(wsdlLoc, serviceClass));
            }
            if (parser == null) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
            }
        }
        return parser;
    }

    private boolean isKnownReadableSource(Source wsdlSource) {
        if (wsdlSource instanceof StreamSource) {
            return ((StreamSource)wsdlSource).getInputStream() != null || ((StreamSource)wsdlSource).getReader() != null;
        }
        return false;
    }

    private XMLStreamReader createReader(@NotNull Source src) throws XMLStreamException {
        return new TidyXMLStreamReader(SourceReaderFactory.createSourceReader(src, true), null);
    }

    private void parseImport(@NotNull URL wsdlLoc) throws XMLStreamException, IOException, SAXException {
        String systemId = wsdlLoc.toExternalForm();
        XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, systemId);
        if (parser == null) {
            parser = new XMLEntityResolver.Parser(wsdlLoc, RuntimeWSDLParser.createReader(wsdlLoc));
        }
        this.parseWSDL(parser, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseWSDL(XMLEntityResolver.Parser parser, boolean imported) throws XMLStreamException, IOException, SAXException {
        XMLStreamReader reader = parser.parser;
        try {
            if (parser.systemId != null && !this.importedWSDLs.add(parser.systemId.toExternalForm())) {
                return;
            }
            if (reader.getEventType() == 7) {
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            if (WSDLConstants.QNAME_DEFINITIONS.equals(reader.getName())) {
                RuntimeWSDLParser.readNSDecl(this.wsdldef_nsdecl, reader);
            }
            if (reader.getEventType() != 8 && reader.getName().equals(WSDLConstants.QNAME_SCHEMA) && imported) {
                LOGGER.warning(WsdlmodelMessages.WSDL_IMPORT_SHOULD_BE_WSDL(parser.systemId));
                return;
            }
            String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
            String oldTargetNamespace = this.targetNamespace;
            this.targetNamespace = tns;
            while (XMLStreamReaderUtil.nextElementContent(reader) != 2 && reader.getEventType() != 8) {
                QName name = reader.getName();
                if (WSDLConstants.QNAME_IMPORT.equals(name)) {
                    this.parseImport(parser.systemId, reader);
                    continue;
                }
                if (WSDLConstants.QNAME_MESSAGE.equals(name)) {
                    this.parseMessage(reader);
                    continue;
                }
                if (WSDLConstants.QNAME_PORT_TYPE.equals(name)) {
                    this.parsePortType(reader);
                    continue;
                }
                if (WSDLConstants.QNAME_BINDING.equals(name)) {
                    this.parseBinding(reader);
                    continue;
                }
                if (WSDLConstants.QNAME_SERVICE.equals(name)) {
                    this.parseService(reader);
                    continue;
                }
                this.extensionFacade.definitionsElements(reader);
            }
            this.targetNamespace = oldTargetNamespace;
        }
        finally {
            this.wsdldef_nsdecl = new HashMap<String, String>();
            reader.close();
        }
    }

    private void parseService(XMLStreamReader reader) {
        this.service_nsdecl.putAll(this.wsdldef_nsdecl);
        RuntimeWSDLParser.readNSDecl(this.service_nsdecl, reader);
        String serviceName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        WSDLServiceImpl service = new WSDLServiceImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, serviceName));
        this.extensionFacade.serviceAttributes(service, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (WSDLConstants.QNAME_PORT.equals(name)) {
                this.parsePort(reader, service);
                if (reader.getEventType() == 2) continue;
                XMLStreamReaderUtil.next(reader);
                continue;
            }
            this.extensionFacade.serviceElements(service, reader);
        }
        this.wsdlDoc.addService(service);
        this.service_nsdecl = new HashMap<String, String>();
    }

    private void parsePort(XMLStreamReader reader, EditableWSDLService service) {
        this.port_nsdecl.putAll(this.service_nsdecl);
        RuntimeWSDLParser.readNSDecl(this.port_nsdecl, reader);
        String portName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        String binding = ParserUtil.getMandatoryNonEmptyAttribute(reader, "binding");
        QName bindingName = ParserUtil.getQName(reader, binding);
        QName portQName = new QName(service.getName().getNamespaceURI(), portName);
        WSDLPortImpl port = new WSDLPortImpl(reader, service, portQName, bindingName);
        this.extensionFacade.portAttributes(port, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (SOAPConstants.QNAME_ADDRESS.equals(name) || SOAPConstants.QNAME_SOAP12ADDRESS.equals(name)) {
                String location = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
                if (location != null) {
                    try {
                        port.setAddress(new EndpointAddress(location));
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        // empty catch block
                    }
                }
                XMLStreamReaderUtil.next(reader);
                continue;
            }
            if (AddressingVersion.W3C.nsUri.equals(name.getNamespaceURI()) && "EndpointReference".equals(name.getLocalPart())) {
                try {
                    StreamReaderBufferCreator creator = new StreamReaderBufferCreator(new MutableXMLStreamBuffer());
                    XMLStreamBufferMark eprbuffer = new XMLStreamBufferMark(this.port_nsdecl, (AbstractCreatorProcessor)creator);
                    creator.createElementFragment(reader, false);
                    WSEndpointReference wsepr = new WSEndpointReference((XMLStreamBuffer)eprbuffer, AddressingVersion.W3C);
                    port.setEPR(wsepr);
                    if (reader.getEventType() != 2 || !reader.getName().equals(WSDLConstants.QNAME_PORT)) continue;
                    break;
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
            this.extensionFacade.portElements(port, reader);
        }
        if (port.getAddress() == null) {
            try {
                port.setAddress(new EndpointAddress(""));
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
        }
        service.put(portQName, port);
        this.port_nsdecl = new HashMap<String, String>();
    }

    private void parseBinding(XMLStreamReader reader) {
        String bindingName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "type");
        if (bindingName == null || portTypeName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        WSDLBoundPortTypeImpl binding = new WSDLBoundPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, bindingName), ParserUtil.getQName(reader, portTypeName));
        this.extensionFacade.bindingAttributes(binding, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            String style;
            String transport;
            QName name = reader.getName();
            if (WSDLConstants.NS_SOAP_BINDING.equals(name)) {
                transport = reader.getAttributeValue(null, "transport");
                binding.setBindingId(RuntimeWSDLParser.createBindingId(transport, SOAPVersion.SOAP_11));
                style = reader.getAttributeValue(null, "style");
                if (style != null && style.equals("rpc")) {
                    binding.setStyle(SOAPBinding.Style.RPC);
                } else {
                    binding.setStyle(SOAPBinding.Style.DOCUMENT);
                }
                RuntimeWSDLParser.goToEnd(reader);
                continue;
            }
            if (WSDLConstants.NS_SOAP12_BINDING.equals(name)) {
                transport = reader.getAttributeValue(null, "transport");
                binding.setBindingId(RuntimeWSDLParser.createBindingId(transport, SOAPVersion.SOAP_12));
                style = reader.getAttributeValue(null, "style");
                if (style != null && style.equals("rpc")) {
                    binding.setStyle(SOAPBinding.Style.RPC);
                } else {
                    binding.setStyle(SOAPBinding.Style.DOCUMENT);
                }
                RuntimeWSDLParser.goToEnd(reader);
                continue;
            }
            if (WSDLConstants.QNAME_OPERATION.equals(name)) {
                this.parseBindingOperation(reader, binding);
                continue;
            }
            this.extensionFacade.bindingElements(binding, reader);
        }
    }

    private static BindingID createBindingId(String transport, SOAPVersion soapVersion) {
        if (!transport.equals("http://schemas.xmlsoap.org/soap/http")) {
            for (BindingIDFactory f : ServiceFinder.find(BindingIDFactory.class)) {
                BindingID bindingId = f.create(transport, soapVersion);
                if (bindingId == null) continue;
                return bindingId;
            }
        }
        return soapVersion.equals((Object)SOAPVersion.SOAP_11) ? BindingID.SOAP11_HTTP : BindingID.SOAP12_HTTP;
    }

    private void parseBindingOperation(XMLStreamReader reader, EditableWSDLBoundPortType binding) {
        String bindingOpName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (bindingOpName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        QName opName = new QName(binding.getPortTypeName().getNamespaceURI(), bindingOpName);
        WSDLBoundOperationImpl bindingOp = new WSDLBoundOperationImpl(reader, binding, opName);
        binding.put(opName, bindingOp);
        this.extensionFacade.bindingOperationAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            String style = null;
            if (WSDLConstants.QNAME_INPUT.equals(name)) {
                this.parseInputBinding(reader, bindingOp);
            } else if (WSDLConstants.QNAME_OUTPUT.equals(name)) {
                this.parseOutputBinding(reader, bindingOp);
            } else if (WSDLConstants.QNAME_FAULT.equals(name)) {
                this.parseFaultBinding(reader, bindingOp);
            } else if (SOAPConstants.QNAME_OPERATION.equals(name) || SOAPConstants.QNAME_SOAP12OPERATION.equals(name)) {
                style = reader.getAttributeValue(null, "style");
                String soapAction = reader.getAttributeValue(null, "soapAction");
                if (soapAction != null) {
                    bindingOp.setSoapAction(soapAction);
                }
                RuntimeWSDLParser.goToEnd(reader);
            } else {
                this.extensionFacade.bindingOperationElements(bindingOp, reader);
            }
            if (style != null) {
                if (style.equals("rpc")) {
                    bindingOp.setStyle(SOAPBinding.Style.RPC);
                    continue;
                }
                bindingOp.setStyle(SOAPBinding.Style.DOCUMENT);
                continue;
            }
            bindingOp.setStyle(binding.getStyle());
        }
    }

    private void parseInputBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
        boolean bodyFound = false;
        this.extensionFacade.bindingOperationInputAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
                bodyFound = true;
                bindingOp.setInputExplicitBodyParts(RuntimeWSDLParser.parseSOAPBodyBinding(reader, bindingOp, BindingMode.INPUT));
                RuntimeWSDLParser.goToEnd(reader);
                continue;
            }
            if (SOAPConstants.QNAME_HEADER.equals(name) || SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
                RuntimeWSDLParser.parseSOAPHeaderBinding(reader, bindingOp.getInputParts());
                continue;
            }
            if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                RuntimeWSDLParser.parseMimeMultipartBinding(reader, bindingOp, BindingMode.INPUT);
                continue;
            }
            this.extensionFacade.bindingOperationInputElements(bindingOp, reader);
        }
    }

    private void parseOutputBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
        boolean bodyFound = false;
        this.extensionFacade.bindingOperationOutputAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
                bodyFound = true;
                bindingOp.setOutputExplicitBodyParts(RuntimeWSDLParser.parseSOAPBodyBinding(reader, bindingOp, BindingMode.OUTPUT));
                RuntimeWSDLParser.goToEnd(reader);
                continue;
            }
            if (SOAPConstants.QNAME_HEADER.equals(name) || SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
                RuntimeWSDLParser.parseSOAPHeaderBinding(reader, bindingOp.getOutputParts());
                continue;
            }
            if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                RuntimeWSDLParser.parseMimeMultipartBinding(reader, bindingOp, BindingMode.OUTPUT);
                continue;
            }
            this.extensionFacade.bindingOperationOutputElements(bindingOp, reader);
        }
    }

    private void parseFaultBinding(XMLStreamReader reader, EditableWSDLBoundOperation bindingOp) {
        String faultName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        WSDLBoundFaultImpl wsdlBoundFault = new WSDLBoundFaultImpl(reader, faultName, bindingOp);
        bindingOp.addFault(wsdlBoundFault);
        this.extensionFacade.bindingOperationFaultAttributes(wsdlBoundFault, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.bindingOperationFaultElements(wsdlBoundFault, reader);
        }
    }

    private static boolean parseSOAPBodyBinding(XMLStreamReader reader, EditableWSDLBoundOperation op, BindingMode mode) {
        String namespace = reader.getAttributeValue(null, "namespace");
        if (mode == BindingMode.INPUT) {
            op.setRequestNamespace(namespace);
            return RuntimeWSDLParser.parseSOAPBodyBinding(reader, op.getInputParts());
        }
        op.setResponseNamespace(namespace);
        return RuntimeWSDLParser.parseSOAPBodyBinding(reader, op.getOutputParts());
    }

    private static boolean parseSOAPBodyBinding(XMLStreamReader reader, Map<String, ParameterBinding> parts) {
        String partsString = reader.getAttributeValue(null, "parts");
        if (partsString != null) {
            List<String> partsList = XmlUtil.parseTokenList(partsString);
            if (partsList.isEmpty()) {
                parts.put(" ", ParameterBinding.BODY);
            } else {
                for (String part : partsList) {
                    parts.put(part, ParameterBinding.BODY);
                }
            }
            return true;
        }
        return false;
    }

    private static void parseSOAPHeaderBinding(XMLStreamReader reader, Map<String, ParameterBinding> parts) {
        String part = reader.getAttributeValue(null, "part");
        if (part == null || part.equals("")) {
            return;
        }
        parts.put(part, ParameterBinding.HEADER);
        RuntimeWSDLParser.goToEnd(reader);
    }

    private static void parseMimeMultipartBinding(XMLStreamReader reader, EditableWSDLBoundOperation op, BindingMode mode) {
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (MIMEConstants.QNAME_PART.equals(name)) {
                RuntimeWSDLParser.parseMIMEPart(reader, op, mode);
                continue;
            }
            XMLStreamReaderUtil.skipElement(reader);
        }
    }

    private static void parseMIMEPart(XMLStreamReader reader, EditableWSDLBoundOperation op, BindingMode mode) {
        boolean bodyFound = false;
        Map<String, ParameterBinding> parts = null;
        if (mode == BindingMode.INPUT) {
            parts = op.getInputParts();
        } else if (mode == BindingMode.OUTPUT) {
            parts = op.getOutputParts();
        } else if (mode == BindingMode.FAULT) {
            parts = op.getFaultParts();
        }
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (SOAPConstants.QNAME_BODY.equals(name) && !bodyFound) {
                bodyFound = true;
                RuntimeWSDLParser.parseSOAPBodyBinding(reader, op, mode);
                XMLStreamReaderUtil.next(reader);
                continue;
            }
            if (SOAPConstants.QNAME_HEADER.equals(name)) {
                bodyFound = true;
                RuntimeWSDLParser.parseSOAPHeaderBinding(reader, parts);
                XMLStreamReaderUtil.next(reader);
                continue;
            }
            if (MIMEConstants.QNAME_CONTENT.equals(name)) {
                String part = reader.getAttributeValue(null, "part");
                String type = reader.getAttributeValue(null, "type");
                if (part == null || type == null) {
                    XMLStreamReaderUtil.skipElement(reader);
                    continue;
                }
                ParameterBinding sb = ParameterBinding.createAttachment(type);
                if (parts != null && sb != null && part != null) {
                    parts.put(part, sb);
                }
                XMLStreamReaderUtil.next(reader);
                continue;
            }
            XMLStreamReaderUtil.skipElement(reader);
        }
    }

    protected void parseImport(@Nullable URL baseURL, XMLStreamReader reader) throws IOException, SAXException, XMLStreamException {
        String importLocation = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
        URL importURL = baseURL != null ? new URL(baseURL, importLocation) : new URL(importLocation);
        this.parseImport(importURL);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            XMLStreamReaderUtil.skipElement(reader);
        }
    }

    private void parsePortType(XMLStreamReader reader) {
        String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (portTypeName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        WSDLPortTypeImpl portType = new WSDLPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, portTypeName));
        this.extensionFacade.portTypeAttributes(portType, reader);
        this.wsdlDoc.addPortType(portType);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (WSDLConstants.QNAME_OPERATION.equals(name)) {
                this.parsePortTypeOperation(reader, portType);
                continue;
            }
            this.extensionFacade.portTypeElements(portType, reader);
        }
    }

    private void parsePortTypeOperation(XMLStreamReader reader, EditableWSDLPortType portType) {
        String operationName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (operationName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        QName operationQName = new QName(portType.getName().getNamespaceURI(), operationName);
        WSDLOperationImpl operation = new WSDLOperationImpl(reader, portType, operationQName);
        this.extensionFacade.portTypeOperationAttributes(operation, reader);
        String parameterOrder = ParserUtil.getAttribute(reader, "parameterOrder");
        operation.setParameterOrder(parameterOrder);
        portType.put(operationName, operation);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (name.equals(WSDLConstants.QNAME_INPUT)) {
                this.parsePortTypeOperationInput(reader, operation);
                continue;
            }
            if (name.equals(WSDLConstants.QNAME_OUTPUT)) {
                this.parsePortTypeOperationOutput(reader, operation);
                continue;
            }
            if (name.equals(WSDLConstants.QNAME_FAULT)) {
                this.parsePortTypeOperationFault(reader, operation);
                continue;
            }
            this.extensionFacade.portTypeOperationElements(operation, reader);
        }
    }

    private void parsePortTypeOperationFault(XMLStreamReader reader, EditableWSDLOperation operation) {
        String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
        QName msgName = ParserUtil.getQName(reader, msg);
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        WSDLFaultImpl fault = new WSDLFaultImpl(reader, name, msgName, operation);
        operation.addFault(fault);
        this.extensionFacade.portTypeOperationFaultAttributes(fault, reader);
        this.extensionFacade.portTypeOperationFault(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationFaultElements(fault, reader);
        }
    }

    private void parsePortTypeOperationInput(XMLStreamReader reader, EditableWSDLOperation operation) {
        String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
        QName msgName = ParserUtil.getQName(reader, msg);
        String name = ParserUtil.getAttribute(reader, "name");
        WSDLInputImpl input = new WSDLInputImpl(reader, name, msgName, operation);
        operation.setInput(input);
        this.extensionFacade.portTypeOperationInputAttributes(input, reader);
        this.extensionFacade.portTypeOperationInput(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationInputElements(input, reader);
        }
    }

    private void parsePortTypeOperationOutput(XMLStreamReader reader, EditableWSDLOperation operation) {
        String msg = ParserUtil.getAttribute(reader, "message");
        QName msgName = ParserUtil.getQName(reader, msg);
        String name = ParserUtil.getAttribute(reader, "name");
        WSDLOutputImpl output = new WSDLOutputImpl(reader, name, msgName, operation);
        operation.setOutput(output);
        this.extensionFacade.portTypeOperationOutputAttributes(output, reader);
        this.extensionFacade.portTypeOperationOutput(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationOutputElements(output, reader);
        }
    }

    private void parseMessage(XMLStreamReader reader) {
        String msgName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        WSDLMessageImpl msg = new WSDLMessageImpl(reader, new QName(this.targetNamespace, msgName));
        this.extensionFacade.messageAttributes(msg, reader);
        int partIndex = 0;
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            QName name = reader.getName();
            if (WSDLConstants.QNAME_PART.equals(name)) {
                String part = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                String desc = null;
                int index = reader.getAttributeCount();
                WSDLDescriptorKind kind = WSDLDescriptorKind.ELEMENT;
                for (int i = 0; i < index; ++i) {
                    QName descName = reader.getAttributeName(i);
                    if (descName.getLocalPart().equals("element")) {
                        kind = WSDLDescriptorKind.ELEMENT;
                    } else if (descName.getLocalPart().equals("type")) {
                        kind = WSDLDescriptorKind.TYPE;
                    }
                    if (!descName.getLocalPart().equals("element") && !descName.getLocalPart().equals("type")) continue;
                    desc = reader.getAttributeValue(i);
                    break;
                }
                if (desc != null) {
                    WSDLPartImpl wsdlPart = new WSDLPartImpl(reader, part, partIndex, new WSDLPartDescriptorImpl(reader, ParserUtil.getQName(reader, desc), kind));
                    msg.add(wsdlPart);
                }
                if (reader.getEventType() == 2) continue;
                RuntimeWSDLParser.goToEnd(reader);
                continue;
            }
            this.extensionFacade.messageElements(msg, reader);
        }
        this.wsdlDoc.addMessage(msg);
        if (reader.getEventType() != 2) {
            RuntimeWSDLParser.goToEnd(reader);
        }
    }

    private static void goToEnd(XMLStreamReader reader) {
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            XMLStreamReaderUtil.skipElement(reader);
        }
    }

    private static XMLStreamReader createReader(URL wsdlLoc) throws IOException, XMLStreamException {
        return RuntimeWSDLParser.createReader(wsdlLoc, null);
    }

    private static XMLStreamReader createReader(URL wsdlLoc, Class<Service> serviceClass) throws IOException, XMLStreamException {
        InputStream stream;
        try {
            stream = wsdlLoc.openStream();
        }
        catch (IOException io) {
            WSDLLocator locator;
            if (serviceClass != null && (locator = ContainerResolver.getInstance().getContainer().getSPI(WSDLLocator.class)) != null) {
                String ruExForm;
                String exForm = wsdlLoc.toExternalForm();
                URL ru = serviceClass.getResource(".");
                String loc = wsdlLoc.getPath();
                if (ru != null && exForm.startsWith(ruExForm = ru.toExternalForm())) {
                    loc = exForm.substring(ruExForm.length());
                }
                if ((wsdlLoc = locator.locateWSDL(serviceClass, loc)) != null) {
                    stream = new FilterInputStream(wsdlLoc.openStream()){
                        boolean closed;

                        @Override
                        public void close() throws IOException {
                            if (!this.closed) {
                                this.closed = true;
                                byte[] buf = new byte[8192];
                                while (this.read(buf) != -1) {
                                }
                                super.close();
                            }
                        }
                    };
                }
            }
            throw io;
        }
        return new TidyXMLStreamReader(XMLStreamReaderFactory.create(wsdlLoc.toExternalForm(), stream, false), stream);
    }

    private void register(WSDLParserExtension e) {
        this.extensions.add(new FoolProofParserExtension(e));
    }

    private static void readNSDecl(Map<String, String> ns_map, XMLStreamReader reader) {
        if (reader.getNamespaceCount() > 0) {
            for (int i = 0; i < reader.getNamespaceCount(); ++i) {
                ns_map.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
        }
    }

    private static enum BindingMode {
        INPUT,
        OUTPUT,
        FAULT;

    }
}

