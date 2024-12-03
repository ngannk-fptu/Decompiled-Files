/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.unmarshaller.DOMScanner
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.streaming.DOMStreamReader;
import com.sun.xml.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class DOMHeader<N extends Element>
extends AbstractHeaderImpl {
    protected final N node;
    private final String nsUri;
    private final String localName;

    public DOMHeader(N node) {
        assert (node != null);
        this.node = node;
        this.nsUri = DOMHeader.fixNull(node.getNamespaceURI());
        this.localName = node.getLocalName();
    }

    @Override
    public String getNamespaceURI() {
        return this.nsUri;
    }

    @Override
    public String getLocalPart() {
        return this.localName;
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        DOMStreamReader r = new DOMStreamReader((Node)this.node);
        r.nextTag();
        return r;
    }

    @Override
    public <T> T readAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        return (T)unmarshaller.unmarshal(this.node);
    }

    @Override
    public <T> T readAsJAXB(Bridge<T> bridge) throws JAXBException {
        return (T)bridge.unmarshal(this.node);
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        DOMUtil.serializeNode(this.node, w);
    }

    private static String fixNull(String s) {
        if (s != null) {
            return s;
        }
        return "";
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.node);
    }

    @Override
    public String getAttribute(String nsUri, String localName) {
        if (nsUri.length() == 0) {
            nsUri = null;
        }
        return this.node.getAttributeNS(nsUri, localName);
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        Node clone = header.getOwnerDocument().importNode((Node)this.node, true);
        header.appendChild(clone);
    }

    @Override
    public String getStringContent() {
        return this.node.getTextContent();
    }

    public N getWrappedNode() {
        return this.node;
    }

    public int hashCode() {
        return this.getWrappedNode().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof DOMHeader) {
            return this.getWrappedNode().equals(((DOMHeader)obj).getWrappedNode());
        }
        return false;
    }
}

