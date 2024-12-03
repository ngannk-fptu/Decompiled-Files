/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.XMLStreamException2
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.util.JAXBResult
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message.jaxb;

import com.sun.istack.NotNull;
import com.sun.istack.XMLStreamException2;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.message.RootElementSniffer;
import com.sun.xml.ws.message.jaxb.JAXBBridgeSource;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBHeader
extends AbstractHeaderImpl {
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private String nsUri;
    private String localName;
    private Attributes atts;
    private XMLStreamBuffer infoset;

    public JAXBHeader(BindingContext context, Object jaxbObject) {
        this.jaxbObject = jaxbObject;
        this.bridge = context.createFragmentBridge();
        if (jaxbObject instanceof JAXBElement) {
            JAXBElement e = (JAXBElement)jaxbObject;
            this.nsUri = e.getName().getNamespaceURI();
            this.localName = e.getName().getLocalPart();
        }
    }

    public JAXBHeader(XMLBridge bridge, Object jaxbObject) {
        this.jaxbObject = jaxbObject;
        this.bridge = bridge;
        QName tagName = bridge.getTypeInfo().tagName;
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
    }

    private void parse() {
        RootElementSniffer sniffer = new RootElementSniffer();
        try {
            this.bridge.marshal(this.jaxbObject, sniffer, null);
        }
        catch (JAXBException e) {
            this.nsUri = sniffer.getNsUri();
            this.localName = sniffer.getLocalName();
            this.atts = sniffer.getAttributes();
        }
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        if (this.nsUri == null) {
            this.parse();
        }
        return this.nsUri;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        if (this.localName == null) {
            this.parse();
        }
        return this.localName;
    }

    @Override
    public String getAttribute(String nsUri, String localName) {
        if (this.atts == null) {
            this.parse();
        }
        return this.atts.getValue(nsUri, localName);
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        if (this.infoset == null) {
            MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
            this.writeTo(buffer.createFromXMLStreamWriter());
            this.infoset = buffer;
        }
        return this.infoset.readAsXMLStreamReader();
    }

    @Override
    public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        try {
            JAXBResult r = new JAXBResult(unmarshaller);
            r.getHandler().startDocument();
            this.bridge.marshal(this.jaxbObject, (Result)r);
            r.getHandler().endDocument();
            return (T)r.getResult();
        }
        catch (SAXException e) {
            throw new JAXBException((Throwable)e);
        }
    }

    @Override
    public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
        return (T)bridge.unmarshal((Source)new JAXBBridgeSource(this.bridge, this.jaxbObject));
    }

    @Override
    public <T> T readAsJAXB(XMLBridge<T> bond) throws JAXBException {
        return bond.unmarshal(new JAXBBridgeSource(this.bridge, this.jaxbObject), null);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        try {
            OutputStream os;
            String encoding = XMLStreamWriterUtil.getEncoding(sw);
            OutputStream outputStream = os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), null);
            } else {
                this.bridge.marshal(this.jaxbObject, sw, null);
            }
        }
        catch (JAXBException e) {
            throw new XMLStreamException2((Throwable)e);
        }
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            this.bridge.marshal(this.jaxbObject, (Node)header);
        }
        catch (JAXBException e) {
            throw new SOAPException((Throwable)e);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        try {
            this.bridge.marshal(this.jaxbObject, contentHandler, null);
        }
        catch (JAXBException e) {
            SAXParseException x = new SAXParseException(e.getMessage(), null, null, -1, -1, (Exception)((Object)e));
            errorHandler.fatalError(x);
            throw x;
        }
    }
}

