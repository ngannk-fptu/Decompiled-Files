/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message;

import com.sun.xml.ws.message.StringHeader;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class RelatesToHeader
extends StringHeader {
    protected String type;
    private final QName typeAttributeName;

    public RelatesToHeader(QName name, String messageId, String type) {
        super(name, messageId);
        this.type = type;
        this.typeAttributeName = new QName(name.getNamespaceURI(), "type");
    }

    public RelatesToHeader(QName name, String mid) {
        super(name, mid);
        this.typeAttributeName = new QName(name.getNamespaceURI(), "type");
    }

    public String getType() {
        return this.type;
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        w.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
        w.writeDefaultNamespace(this.name.getNamespaceURI());
        if (this.type != null) {
            w.writeAttribute("type", this.type);
        }
        w.writeCharacters(this.value);
        w.writeEndElement();
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        SOAPHeaderElement she = header.addHeaderElement(this.name);
        if (this.type != null) {
            she.addAttribute(this.typeAttributeName, this.type);
        }
        she.addTextNode(this.value);
    }
}

