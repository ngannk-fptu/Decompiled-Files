/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.addressing;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class EPRHeader
extends AbstractHeaderImpl {
    private final String nsUri;
    private final String localName;
    private final WSEndpointReference epr;

    EPRHeader(QName tagName, WSEndpointReference epr) {
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
        this.epr = epr;
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return this.nsUri;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return this.localName;
    }

    @Override
    @Nullable
    public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
        try {
            XMLStreamReader sr = this.epr.read("EndpointReference");
            while (sr.getEventType() != 1) {
                sr.next();
            }
            return sr.getAttributeValue(nsUri, localName);
        }
        catch (XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return this.epr.read(this.localName);
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        this.epr.writeTo(this.localName, w);
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            Transformer t = XmlUtil.newTransformer();
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLStreamWriter w = XMLOutputFactory.newFactory().createXMLStreamWriter(baos);
            this.epr.writeTo(this.localName, w);
            w.flush();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DocumentBuilderFactory fac = XmlUtil.newDocumentBuilderFactory(false);
            fac.setNamespaceAware(true);
            Element eprNode = fac.newDocumentBuilder().parse(bais).getDocumentElement();
            Node eprNodeToAdd = header.getOwnerDocument().importNode(eprNode, true);
            header.appendChild(eprNodeToAdd);
        }
        catch (Exception e) {
            throw new SOAPException((Throwable)e);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        this.epr.writeTo(this.localName, contentHandler, errorHandler, true);
    }
}

