/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  com.sun.xml.stream.buffer.XMLStreamBufferSource
 *  com.sun.xml.stream.buffer.sax.SAXBufferProcessor
 *  com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor
 *  com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator
 *  javax.xml.bind.JAXBContext
 *  javax.xml.ws.Dispatch
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.Service
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 */
package com.sun.xml.ws.api.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.ws.addressing.EndpointReferenceUtil;
import com.sun.xml.ws.addressing.WSEPRExtension;
import com.sun.xml.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.EPRHeader;
import com.sun.xml.ws.api.addressing.OutboundReferenceParameterHeader;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.resources.AddressingMessages;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.spi.ProviderImpl;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public final class WSEndpointReference
implements WSDLExtension {
    private final XMLStreamBuffer infoset;
    private final AddressingVersion version;
    @NotNull
    private Header[] referenceParameters;
    @NotNull
    private String address;
    @NotNull
    private QName rootElement;
    private static final OutboundReferenceParameterHeader[] EMPTY_ARRAY = new OutboundReferenceParameterHeader[0];
    private Map<QName, EPRExtension> rootEprExtensions;

    public WSEndpointReference(EndpointReference epr, AddressingVersion version) {
        try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            epr.writeTo((Result)new XMLStreamBufferResult(xsb));
            this.infoset = xsb;
            this.version = version;
            this.rootElement = new QName("EndpointReference", version.nsUri);
            this.parse();
        }
        catch (XMLStreamException e) {
            throw new WebServiceException(ClientMessages.FAILED_TO_PARSE_EPR(epr), (Throwable)e);
        }
    }

    public WSEndpointReference(EndpointReference epr) {
        this(epr, AddressingVersion.fromSpecClass(epr.getClass()));
    }

    public WSEndpointReference(XMLStreamBuffer infoset, AddressingVersion version) {
        try {
            this.infoset = infoset;
            this.version = version;
            this.rootElement = new QName("EndpointReference", version.nsUri);
            this.parse();
        }
        catch (XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }

    public WSEndpointReference(InputStream infoset, AddressingVersion version) throws XMLStreamException {
        this(XMLStreamReaderFactory.create(null, infoset, false), version);
    }

    public WSEndpointReference(XMLStreamReader in, AddressingVersion version) throws XMLStreamException {
        this(XMLStreamBuffer.createNewBufferFromXMLStreamReader((XMLStreamReader)in), version);
    }

    public WSEndpointReference(URL address, AddressingVersion version) {
        this(address.toExternalForm(), version);
    }

    public WSEndpointReference(URI address, AddressingVersion version) {
        this(address.toString(), version);
    }

    public WSEndpointReference(String address, AddressingVersion version) {
        this.infoset = WSEndpointReference.createBufferFromAddress(address, version);
        this.version = version;
        this.address = address;
        this.rootElement = new QName("EndpointReference", version.nsUri);
        this.referenceParameters = EMPTY_ARRAY;
    }

    private static XMLStreamBuffer createBufferFromAddress(String address, AddressingVersion version) {
        try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            StreamWriterBufferCreator w = new StreamWriterBufferCreator(xsb);
            w.writeStartDocument();
            w.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            w.writeNamespace(version.getPrefix(), version.nsUri);
            w.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
            w.writeCharacters(address);
            w.writeEndElement();
            w.writeEndElement();
            w.writeEndDocument();
            w.close();
            return xsb;
        }
        catch (XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }

    public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable List<Element> referenceParameters) {
        this(version, address, service, port, portType, metadata, wsdlAddress, null, referenceParameters, null, null);
    }

    public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable List<Element> referenceParameters, @Nullable Collection<EPRExtension> extns, @Nullable Map<QName, String> attributes) {
        this(WSEndpointReference.createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, null, extns, attributes), version);
    }

    public WSEndpointReference(@NotNull AddressingVersion version, @NotNull String address, @Nullable QName service, @Nullable QName port, @Nullable QName portType, @Nullable List<Element> metadata, @Nullable String wsdlAddress, @Nullable String wsdlTargetNamepsace, @Nullable List<Element> referenceParameters, @Nullable List<Element> elements, @Nullable Map<QName, String> attributes) {
        this(WSEndpointReference.createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamepsace, elements, attributes), version);
    }

    private static XMLStreamBuffer createBufferFromData(AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable List<Element> elements, @Nullable Map<QName, String> attributes) {
        StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        try {
            writer.writeStartDocument();
            writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            writer.writeNamespace(version.getPrefix(), version.nsUri);
            WSEndpointReference.writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
            if (elements != null) {
                for (Element e : elements) {
                    DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            return writer.getXMLStreamBuffer();
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static XMLStreamBuffer createBufferFromData(AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable Collection<EPRExtension> extns, @Nullable Map<QName, String> attributes) {
        StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        try {
            writer.writeStartDocument();
            writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            writer.writeNamespace(version.getPrefix(), version.nsUri);
            WSEndpointReference.writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
            if (extns != null) {
                for (EPRExtension e : extns) {
                    XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
                    XMLStreamReader r = e.readAsXMLStreamReader();
                    c.bridge(r, (XMLStreamWriter)writer);
                    XMLStreamReaderFactory.recycle(r);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            return writer.getXMLStreamBuffer();
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static void writePartialEPRInfoset(StreamWriterBufferCreator writer, AddressingVersion version, String address, List<Element> referenceParameters, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace, @Nullable Map<QName, String> attributes) throws XMLStreamException {
        if (attributes != null) {
            for (Map.Entry<QName, String> entry : attributes.entrySet()) {
                QName qname = entry.getKey();
                writer.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), entry.getValue());
            }
        }
        writer.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
        writer.writeCharacters(address);
        writer.writeEndElement();
        if (referenceParameters != null && referenceParameters.size() > 0) {
            writer.writeStartElement(version.getPrefix(), version.eprType.referenceParameters, version.nsUri);
            for (Element e : referenceParameters) {
                DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
            }
            writer.writeEndElement();
        }
        switch (version) {
            case W3C: {
                WSEndpointReference.writeW3CMetaData(writer, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace);
                break;
            }
            case MEMBER: {
                WSEndpointReference.writeMSMetaData(writer, service, port, portType, metadata);
                if (wsdlAddress == null) break;
                writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI());
                writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI());
                writer.writeAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
                WSEndpointReference.writeWsdl(writer, service, wsdlAddress);
                writer.writeEndElement();
                writer.writeEndElement();
            }
        }
    }

    private static boolean isEmty(QName qname) {
        return qname == null || qname.toString().trim().length() == 0;
    }

    private static void writeW3CMetaData(StreamWriterBufferCreator writer, QName service, QName port, QName portType, List<Element> metadata, String wsdlAddress, String wsdlTargetNamespace) throws XMLStreamException {
        if (WSEndpointReference.isEmty(service) && WSEndpointReference.isEmty(port) && WSEndpointReference.isEmty(portType) && metadata == null) {
            return;
        }
        writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart(), AddressingVersion.W3C.nsUri);
        writer.writeNamespace(AddressingVersion.W3C.getWsdlPrefix(), AddressingVersion.W3C.wsdlNsUri);
        if (wsdlAddress != null) {
            WSEndpointReference.writeWsdliLocation(writer, service, wsdlAddress, wsdlTargetNamespace);
        }
        if (portType != null) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.portTypeName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            String portTypePrefix = portType.getPrefix();
            if (portTypePrefix == null || portTypePrefix.equals("")) {
                portTypePrefix = "wsns";
            }
            writer.writeNamespace(portTypePrefix, portType.getNamespaceURI());
            writer.writeCharacters(portTypePrefix + ":" + portType.getLocalPart());
            writer.writeEndElement();
        }
        if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.serviceName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            String servicePrefix = service.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
                servicePrefix = "wsns";
            }
            writer.writeNamespace(servicePrefix, service.getNamespaceURI());
            if (port != null) {
                writer.writeAttribute(AddressingVersion.W3C.eprType.portName, port.getLocalPart());
            }
            writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
            writer.writeEndElement();
        }
        if (metadata != null) {
            for (Element e : metadata) {
                DOMUtil.serializeNode(e, (XMLStreamWriter)writer);
            }
        }
        writer.writeEndElement();
    }

    private static void writeWsdliLocation(StreamWriterBufferCreator writer, QName service, String wsdlAddress, String wsdlTargetNamespace) throws XMLStreamException {
        String wsdliLocation = "";
        if (wsdlTargetNamespace != null) {
            wsdliLocation = wsdlTargetNamespace + " ";
        } else if (service != null) {
            wsdliLocation = service.getNamespaceURI() + " ";
        } else {
            throw new WebServiceException("WSDL target Namespace cannot be resolved");
        }
        wsdliLocation = wsdliLocation + wsdlAddress;
        writer.writeNamespace("wsdli", "http://www.w3.org/ns/wsdl-instance");
        writer.writeAttribute("wsdli", "http://www.w3.org/ns/wsdl-instance", "wsdlLocation", wsdliLocation);
    }

    private static void writeMSMetaData(StreamWriterBufferCreator writer, QName service, QName port, QName portType, List<Element> metadata) throws XMLStreamException {
        if (portType != null) {
            writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.portTypeName, AddressingVersion.MEMBER.nsUri);
            String portTypePrefix = portType.getPrefix();
            if (portTypePrefix == null || portTypePrefix.equals("")) {
                portTypePrefix = "wsns";
            }
            writer.writeNamespace(portTypePrefix, portType.getNamespaceURI());
            writer.writeCharacters(portTypePrefix + ":" + portType.getLocalPart());
            writer.writeEndElement();
        }
        if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
            writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.serviceName, AddressingVersion.MEMBER.nsUri);
            String servicePrefix = service.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
                servicePrefix = "wsns";
            }
            writer.writeNamespace(servicePrefix, service.getNamespaceURI());
            if (port != null) {
                writer.writeAttribute(AddressingVersion.MEMBER.eprType.portName, port.getLocalPart());
            }
            writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
            writer.writeEndElement();
        }
    }

    private static void writeWsdl(StreamWriterBufferCreator writer, QName service, String wsdlAddress) throws XMLStreamException {
        writer.writeStartElement("wsdl", WSDLConstants.QNAME_DEFINITIONS.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
        writer.writeNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        writer.writeStartElement("wsdl", WSDLConstants.QNAME_IMPORT.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
        writer.writeAttribute("namespace", service.getNamespaceURI());
        writer.writeAttribute("location", wsdlAddress);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    @Nullable
    public static WSEndpointReference create(@Nullable EndpointReference epr) {
        if (epr != null) {
            return new WSEndpointReference(epr);
        }
        return null;
    }

    @NotNull
    public WSEndpointReference createWithAddress(@NotNull URI newAddress) {
        return this.createWithAddress(newAddress.toString());
    }

    @NotNull
    public WSEndpointReference createWithAddress(@NotNull URL newAddress) {
        return this.createWithAddress(newAddress.toString());
    }

    @NotNull
    public WSEndpointReference createWithAddress(final @NotNull String newAddress) {
        MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        XMLFilterImpl filter = new XMLFilterImpl(){
            private boolean inAddress = false;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if (localName.equals("Address") && uri.equals(((WSEndpointReference)WSEndpointReference.this).version.nsUri)) {
                    this.inAddress = true;
                }
                super.startElement(uri, localName, qName, atts);
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (!this.inAddress) {
                    super.characters(ch, start, length);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (this.inAddress) {
                    super.characters(newAddress.toCharArray(), 0, newAddress.length());
                }
                this.inAddress = false;
                super.endElement(uri, localName, qName);
            }
        };
        filter.setContentHandler((ContentHandler)xsb.createFromSAXBufferCreator());
        try {
            this.infoset.writeTo((ContentHandler)filter, false);
        }
        catch (SAXException e) {
            throw new AssertionError((Object)e);
        }
        return new WSEndpointReference((XMLStreamBuffer)xsb, this.version);
    }

    @NotNull
    public EndpointReference toSpec() {
        return ProviderImpl.INSTANCE.readEndpointReference(this.asSource("EndpointReference"));
    }

    @NotNull
    public <T extends EndpointReference> T toSpec(Class<T> clazz) {
        return EndpointReferenceUtil.transform(clazz, this.toSpec());
    }

    @NotNull
    public <T> T getPort(@NotNull Service jaxwsService, @NotNull Class<T> serviceEndpointInterface, WebServiceFeature ... features) {
        return (T)jaxwsService.getPort(this.toSpec(), serviceEndpointInterface, features);
    }

    @NotNull
    public <T> Dispatch<T> createDispatch(@NotNull Service jaxwsService, @NotNull Class<T> type, @NotNull Service.Mode mode, WebServiceFeature ... features) {
        return jaxwsService.createDispatch(this.toSpec(), type, mode, features);
    }

    @NotNull
    public Dispatch<Object> createDispatch(@NotNull Service jaxwsService, @NotNull JAXBContext context, @NotNull Service.Mode mode, WebServiceFeature ... features) {
        return jaxwsService.createDispatch(this.toSpec(), context, mode, features);
    }

    @NotNull
    public AddressingVersion getVersion() {
        return this.version;
    }

    @NotNull
    public String getAddress() {
        return this.address;
    }

    public boolean isAnonymous() {
        return this.address.equals(this.version.anonymousUri);
    }

    public boolean isNone() {
        return this.address.equals(this.version.noneUri);
    }

    private void parse() throws XMLStreamException {
        StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
        if (xsr.getEventType() == 7) {
            xsr.nextTag();
        }
        assert (xsr.getEventType() == 1);
        String rootLocalName = xsr.getLocalName();
        if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
            throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
        }
        this.rootElement = new QName(xsr.getNamespaceURI(), rootLocalName);
        ArrayList<Header> marks = null;
        while (xsr.nextTag() == 1) {
            String localName = xsr.getLocalName();
            if (this.version.isReferenceParameter(localName)) {
                XMLStreamBuffer mark;
                while ((mark = xsr.nextTagAndMark()) != null) {
                    if (marks == null) {
                        marks = new ArrayList<Header>();
                    }
                    marks.add(this.version.createReferenceParameterHeader(mark, xsr.getNamespaceURI(), xsr.getLocalName()));
                    XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                }
                continue;
            }
            if (localName.equals("Address")) {
                if (this.address != null) {
                    throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), AddressingVersion.fault_duplicateAddressInEpr);
                }
                this.address = xsr.getElementText().trim();
                continue;
            }
            XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
        }
        this.referenceParameters = marks == null ? EMPTY_ARRAY : marks.toArray(new Header[marks.size()]);
        if (this.address == null) {
            throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), this.version.fault_missingAddressInEpr);
        }
    }

    public XMLStreamReader read(final @NotNull String localName) throws XMLStreamException {
        return new StreamReaderBufferProcessor(this.infoset){

            protected void processElement(String prefix, String uri, String _localName, boolean inScope) {
                if (this._depth == 0) {
                    _localName = localName;
                }
                super.processElement(prefix, uri, _localName, WSEndpointReference.this.isInscope(WSEndpointReference.this.infoset, this._depth));
            }
        };
    }

    private boolean isInscope(XMLStreamBuffer buffer, int depth) {
        return buffer.getInscopeNamespaces().size() > 0 && depth == 0;
    }

    public Source asSource(@NotNull String localName) {
        return new SAXSource((XMLReader)((Object)new SAXBufferProcessorImpl(localName)), new InputSource());
    }

    public void writeTo(@NotNull String localName, ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        SAXBufferProcessorImpl p = new SAXBufferProcessorImpl(localName);
        p.setContentHandler(contentHandler);
        p.setErrorHandler(errorHandler);
        p.process(this.infoset, fragment);
    }

    public void writeTo(final @NotNull String localName, @NotNull XMLStreamWriter w) throws XMLStreamException {
        this.infoset.writeToXMLStreamWriter((XMLStreamWriter)new XMLStreamWriterFilter(w){
            private boolean root;
            {
                super(writer);
                this.root = true;
            }

            @Override
            public void writeStartDocument() throws XMLStreamException {
            }

            @Override
            public void writeStartDocument(String encoding, String version) throws XMLStreamException {
            }

            @Override
            public void writeStartDocument(String version) throws XMLStreamException {
            }

            @Override
            public void writeEndDocument() throws XMLStreamException {
            }

            private String override(String ln) {
                if (this.root) {
                    this.root = false;
                    return localName;
                }
                return ln;
            }

            @Override
            public void writeStartElement(String localName2) throws XMLStreamException {
                super.writeStartElement(this.override(localName2));
            }

            @Override
            public void writeStartElement(String namespaceURI, String localName2) throws XMLStreamException {
                super.writeStartElement(namespaceURI, this.override(localName2));
            }

            @Override
            public void writeStartElement(String prefix, String localName2, String namespaceURI) throws XMLStreamException {
                super.writeStartElement(prefix, this.override(localName2), namespaceURI);
            }
        }, true);
    }

    public Header createHeader(QName rootTagName) {
        return new EPRHeader(rootTagName, this);
    }

    public void addReferenceParametersToList(HeaderList outbound) {
        for (Header header : this.referenceParameters) {
            outbound.add(header);
        }
    }

    public void addReferenceParametersToList(MessageHeaders outbound) {
        for (Header header : this.referenceParameters) {
            outbound.addOrReplace(header);
        }
    }

    public void addReferenceParameters(HeaderList headers) {
        if (headers != null) {
            Header[] hs = new Header[this.referenceParameters.length + headers.size()];
            System.arraycopy(this.referenceParameters, 0, hs, 0, this.referenceParameters.length);
            int i = this.referenceParameters.length;
            for (Header h : headers) {
                hs[i++] = h;
            }
            this.referenceParameters = hs;
        }
    }

    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            XmlUtil.newTransformer().transform(this.asSource("EndpointReference"), new StreamResult(sw));
            return sw.toString();
        }
        catch (TransformerException e) {
            return e.toString();
        }
    }

    @Override
    public QName getName() {
        return this.rootElement;
    }

    @Nullable
    public EPRExtension getEPRExtension(QName extnQName) throws XMLStreamException {
        if (this.rootEprExtensions == null) {
            this.parseEPRExtensions();
        }
        return this.rootEprExtensions.get(extnQName);
    }

    @NotNull
    public Collection<EPRExtension> getEPRExtensions() throws XMLStreamException {
        if (this.rootEprExtensions == null) {
            this.parseEPRExtensions();
        }
        return this.rootEprExtensions.values();
    }

    private void parseEPRExtensions() throws XMLStreamException {
        XMLStreamBuffer mark;
        this.rootEprExtensions = new HashMap<QName, EPRExtension>();
        StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
        if (xsr.getEventType() == 7) {
            xsr.nextTag();
        }
        assert (xsr.getEventType() == 1);
        if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
            throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
        }
        while ((mark = xsr.nextTagAndMark()) != null) {
            String localName = xsr.getLocalName();
            String ns = xsr.getNamespaceURI();
            if (this.version.nsUri.equals(ns)) {
                XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                continue;
            }
            QName qn = new QName(ns, localName);
            this.rootEprExtensions.put(qn, new WSEPRExtension(mark, qn));
            XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
        }
    }

    @NotNull
    public Metadata getMetaData() {
        return new Metadata();
    }

    public class Metadata {
        @Nullable
        private QName serviceName;
        @Nullable
        private QName portName;
        @Nullable
        private QName portTypeName;
        @Nullable
        private Source wsdlSource;
        @Nullable
        private String wsdliLocation;

        @Nullable
        public QName getServiceName() {
            return this.serviceName;
        }

        @Nullable
        public QName getPortName() {
            return this.portName;
        }

        @Nullable
        public QName getPortTypeName() {
            return this.portTypeName;
        }

        @Nullable
        public Source getWsdlSource() {
            return this.wsdlSource;
        }

        @Nullable
        public String getWsdliLocation() {
            return this.wsdliLocation;
        }

        private Metadata() {
            try {
                this.parseMetaData();
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        private void parseMetaData() throws XMLStreamException {
            StreamReaderBufferProcessor xsr = WSEndpointReference.this.infoset.readAsXMLStreamReader();
            if (xsr.getEventType() == 7) {
                xsr.nextTag();
            }
            assert (xsr.getEventType() == 1);
            String rootElement = xsr.getLocalName();
            if (!xsr.getNamespaceURI().equals(((WSEndpointReference)WSEndpointReference.this).version.nsUri)) {
                throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(((WSEndpointReference)WSEndpointReference.this).version.nsUri, xsr.getNamespaceURI()));
            }
            if (WSEndpointReference.this.version == AddressingVersion.W3C) {
                do {
                    if (xsr.getLocalName().equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.wsdlMetadata.getLocalPart())) {
                        XMLStreamBuffer mark;
                        String wsdlLoc = xsr.getAttributeValue("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                        if (wsdlLoc != null) {
                            this.wsdliLocation = wsdlLoc.trim();
                        }
                        while ((mark = xsr.nextTagAndMark()) != null) {
                            String localName = xsr.getLocalName();
                            String ns = xsr.getNamespaceURI();
                            if (localName.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.serviceName)) {
                                String portStr = xsr.getAttributeValue(null, ((WSEndpointReference)WSEndpointReference.this).version.eprType.portName);
                                if (this.serviceName != null) {
                                    throw new RuntimeException("More than one " + ((WSEndpointReference)WSEndpointReference.this).version.eprType.serviceName + " element in EPR Metadata");
                                }
                                this.serviceName = this.getElementTextAsQName(xsr);
                                if (this.serviceName == null || portStr == null) continue;
                                this.portName = new QName(this.serviceName.getNamespaceURI(), portStr);
                                continue;
                            }
                            if (localName.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.portTypeName)) {
                                if (this.portTypeName != null) {
                                    throw new RuntimeException("More than one " + ((WSEndpointReference)WSEndpointReference.this).version.eprType.portTypeName + " element in EPR Metadata");
                                }
                                this.portTypeName = this.getElementTextAsQName(xsr);
                                continue;
                            }
                            if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                this.wsdlSource = new XMLStreamBufferSource(mark);
                                continue;
                            }
                            XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                        }
                    } else {
                        if (xsr.getLocalName().equals(rootElement)) continue;
                        XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                    }
                } while (XMLStreamReaderUtil.nextElementContent((XMLStreamReader)xsr) == 1);
                if (this.wsdliLocation != null) {
                    String wsdlLocation = this.wsdliLocation.trim();
                    wsdlLocation = wsdlLocation.substring(this.wsdliLocation.lastIndexOf(" "));
                    this.wsdlSource = new StreamSource(wsdlLocation);
                }
            } else if (WSEndpointReference.this.version == AddressingVersion.MEMBER) {
                do {
                    String localName = xsr.getLocalName();
                    String ns = xsr.getNamespaceURI();
                    if (localName.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.wsdlMetadata.getLocalPart()) && ns.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.wsdlMetadata.getNamespaceURI())) {
                        while (xsr.nextTag() == 1) {
                            XMLStreamBuffer mark;
                            while ((mark = xsr.nextTagAndMark()) != null) {
                                localName = xsr.getLocalName();
                                ns = xsr.getNamespaceURI();
                                if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                    this.wsdlSource = new XMLStreamBufferSource(mark);
                                    continue;
                                }
                                XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                            }
                        }
                    } else {
                        if (localName.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.serviceName)) {
                            String portStr = xsr.getAttributeValue(null, ((WSEndpointReference)WSEndpointReference.this).version.eprType.portName);
                            this.serviceName = this.getElementTextAsQName(xsr);
                            if (this.serviceName == null || portStr == null) continue;
                            this.portName = new QName(this.serviceName.getNamespaceURI(), portStr);
                            continue;
                        }
                        if (localName.equals(((WSEndpointReference)WSEndpointReference.this).version.eprType.portTypeName)) {
                            this.portTypeName = this.getElementTextAsQName(xsr);
                            continue;
                        }
                        if (xsr.getLocalName().equals(rootElement)) continue;
                        XMLStreamReaderUtil.skipElement((XMLStreamReader)xsr);
                    }
                } while (XMLStreamReaderUtil.nextElementContent((XMLStreamReader)xsr) == 1);
            }
        }

        private QName getElementTextAsQName(StreamReaderBufferProcessor xsr) throws XMLStreamException {
            String text = xsr.getElementText().trim();
            String prefix = XmlUtil.getPrefix(text);
            String name = XmlUtil.getLocalPart(text);
            if (name != null) {
                if (prefix != null) {
                    String ns = xsr.getNamespaceURI(prefix);
                    if (ns != null) {
                        return new QName(ns, name, prefix);
                    }
                } else {
                    return new QName(null, name);
                }
            }
            return null;
        }
    }

    public static abstract class EPRExtension {
        public abstract XMLStreamReader readAsXMLStreamReader() throws XMLStreamException;

        public abstract QName getQName();
    }

    class SAXBufferProcessorImpl
    extends SAXBufferProcessor {
        private final String rootLocalName;
        private boolean root;

        public SAXBufferProcessorImpl(String rootLocalName) {
            super(WSEndpointReference.this.infoset, false);
            this.root = true;
            this.rootLocalName = rootLocalName;
        }

        protected void processElement(String uri, String localName, String qName, boolean inscope) throws SAXException {
            if (this.root) {
                this.root = false;
                if (qName.equals(localName)) {
                    qName = localName = this.rootLocalName;
                } else {
                    localName = this.rootLocalName;
                    int idx = qName.indexOf(58);
                    qName = qName.substring(0, idx + 1) + this.rootLocalName;
                }
            }
            super.processElement(uri, localName, qName, inscope);
        }
    }
}

