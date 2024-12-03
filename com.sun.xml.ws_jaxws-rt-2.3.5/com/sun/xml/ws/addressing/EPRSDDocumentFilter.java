/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 */
package com.sun.xml.ws.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentFilter;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.server.WSEndpointImpl;
import com.sun.xml.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

public class EPRSDDocumentFilter
implements SDDocumentFilter {
    private final WSEndpointImpl<?> endpoint;
    List<BoundEndpoint> beList;

    public EPRSDDocumentFilter(@NotNull WSEndpointImpl<?> endpoint) {
        this.endpoint = endpoint;
    }

    @Nullable
    private WSEndpointImpl<?> getEndpoint(String serviceName, String portName) {
        if (serviceName == null || portName == null) {
            return null;
        }
        if (this.endpoint.getServiceName().getLocalPart().equals(serviceName) && this.endpoint.getPortName().getLocalPart().equals(portName)) {
            return this.endpoint;
        }
        if (this.beList == null) {
            Module module = this.endpoint.getContainer().getSPI(Module.class);
            this.beList = module != null ? module.getBoundEndpoints() : Collections.emptyList();
        }
        for (BoundEndpoint be : this.beList) {
            WSEndpoint wse = be.getEndpoint();
            if (!wse.getServiceName().getLocalPart().equals(serviceName) || !wse.getPortName().getLocalPart().equals(portName)) continue;
            return (WSEndpointImpl)wse;
        }
        return null;
    }

    @Override
    public XMLStreamWriter filter(SDDocument doc, XMLStreamWriter w) throws XMLStreamException, IOException {
        if (!doc.isWSDL()) {
            return w;
        }
        return new XMLStreamWriterFilter(w){
            private boolean eprExtnFilterON;
            private boolean portHasEPR;
            private int eprDepth;
            private String serviceName;
            private boolean onService;
            private int serviceDepth;
            private String portName;
            private boolean onPort;
            private int portDepth;
            private String portAddress;
            private boolean onPortAddress;
            {
                this.eprExtnFilterON = false;
                this.portHasEPR = false;
                this.eprDepth = -1;
                this.serviceName = null;
                this.onService = false;
                this.serviceDepth = -1;
                this.portName = null;
                this.onPort = false;
                this.portDepth = -1;
                this.onPortAddress = false;
            }

            private void handleStartElement(String localName, String namespaceURI) throws XMLStreamException {
                this.resetOnElementFlags();
                if (this.serviceDepth >= 0) {
                    ++this.serviceDepth;
                }
                if (this.portDepth >= 0) {
                    ++this.portDepth;
                }
                if (this.eprDepth >= 0) {
                    ++this.eprDepth;
                }
                if (namespaceURI.equals(WSDLConstants.QNAME_SERVICE.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_SERVICE.getLocalPart())) {
                    this.onService = true;
                    this.serviceDepth = 0;
                } else if (namespaceURI.equals(WSDLConstants.QNAME_PORT.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_PORT.getLocalPart())) {
                    if (this.serviceDepth >= 1) {
                        this.onPort = true;
                        this.portDepth = 0;
                    }
                } else if (namespaceURI.equals("http://www.w3.org/2005/08/addressing") && localName.equals("EndpointReference")) {
                    if (this.serviceDepth >= 1 && this.portDepth >= 1) {
                        this.portHasEPR = true;
                        this.eprDepth = 0;
                    }
                } else if ((namespaceURI.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS.getNamespaceURI()) || namespaceURI.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS.getNamespaceURI())) && localName.equals("address") && this.portDepth == 1) {
                    this.onPortAddress = true;
                }
                WSEndpointImpl endpoint = EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName);
                if (endpoint != null && this.eprDepth == 1 && !namespaceURI.equals("http://www.w3.org/2005/08/addressing")) {
                    this.eprExtnFilterON = true;
                }
            }

            private void resetOnElementFlags() {
                if (this.onService) {
                    this.onService = false;
                }
                if (this.onPort) {
                    this.onPort = false;
                }
                if (this.onPortAddress) {
                    this.onPortAddress = false;
                }
            }

            private void writeEPRExtensions(Collection<WSEndpointReference.EPRExtension> eprExtns) throws XMLStreamException {
                if (eprExtns != null) {
                    for (WSEndpointReference.EPRExtension e : eprExtns) {
                        XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
                        XMLStreamReader r = e.readAsXMLStreamReader();
                        c.bridge(r, this.writer);
                        XMLStreamReaderFactory.recycle(r);
                    }
                }
            }

            @Override
            public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
                this.handleStartElement(localName, namespaceURI);
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(prefix, localName, namespaceURI);
                }
            }

            @Override
            public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
                this.handleStartElement(localName, namespaceURI);
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(namespaceURI, localName);
                }
            }

            @Override
            public void writeStartElement(String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(localName);
                }
            }

            private void handleEndElement() throws XMLStreamException {
                this.resetOnElementFlags();
                if (this.portDepth == 0 && !this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
                    this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
                    this.writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
                    this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
                    this.writer.writeCharacters(this.portAddress);
                    this.writer.writeEndElement();
                    this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
                    this.writer.writeEndElement();
                }
                if (this.eprDepth == 0) {
                    if (this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
                        this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
                    }
                    this.eprExtnFilterON = false;
                }
                if (this.serviceDepth >= 0) {
                    --this.serviceDepth;
                }
                if (this.portDepth >= 0) {
                    --this.portDepth;
                }
                if (this.eprDepth >= 0) {
                    --this.eprDepth;
                }
                if (this.serviceDepth == -1) {
                    this.serviceName = null;
                }
                if (this.portDepth == -1) {
                    this.portHasEPR = false;
                    this.portAddress = null;
                    this.portName = null;
                }
            }

            @Override
            public void writeEndElement() throws XMLStreamException {
                this.handleEndElement();
                if (!this.eprExtnFilterON) {
                    super.writeEndElement();
                }
            }

            private void handleAttribute(String localName, String value) {
                if (localName.equals("name")) {
                    if (this.onService) {
                        this.serviceName = value;
                        this.onService = false;
                    } else if (this.onPort) {
                        this.portName = value;
                        this.onPort = false;
                    }
                }
                if (localName.equals("location") && this.onPortAddress) {
                    this.portAddress = value;
                }
            }

            @Override
            public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(prefix, namespaceURI, localName, value);
                }
            }

            @Override
            public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(namespaceURI, localName, value);
                }
            }

            @Override
            public void writeAttribute(String localName, String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(localName, value);
                }
            }

            @Override
            public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(namespaceURI, localName);
                }
            }

            @Override
            public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeNamespace(prefix, namespaceURI);
                }
            }

            @Override
            public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setNamespaceContext(context);
                }
            }

            @Override
            public void setDefaultNamespace(String uri) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setDefaultNamespace(uri);
                }
            }

            @Override
            public void setPrefix(String prefix, String uri) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setPrefix(prefix, uri);
                }
            }

            @Override
            public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeProcessingInstruction(target, data);
                }
            }

            @Override
            public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(prefix, localName, namespaceURI);
                }
            }

            @Override
            public void writeCData(String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCData(data);
                }
            }

            @Override
            public void writeCharacters(String text) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCharacters(text);
                }
            }

            @Override
            public void writeComment(String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeComment(data);
                }
            }

            @Override
            public void writeDTD(String dtd) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeDTD(dtd);
                }
            }

            @Override
            public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeDefaultNamespace(namespaceURI);
                }
            }

            @Override
            public void writeEmptyElement(String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(localName);
                }
            }

            @Override
            public void writeEntityRef(String name) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEntityRef(name);
                }
            }

            @Override
            public void writeProcessingInstruction(String target) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeProcessingInstruction(target);
                }
            }

            @Override
            public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCharacters(text, start, len);
                }
            }
        };
    }
}

