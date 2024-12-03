/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class FaultDetailHeader
extends AbstractHeaderImpl {
    private AddressingVersion av;
    private String wrapper;
    private String problemValue = null;

    public FaultDetailHeader(AddressingVersion av, String wrapper, QName problemHeader) {
        this.av = av;
        this.wrapper = wrapper;
        this.problemValue = problemHeader.toString();
    }

    public FaultDetailHeader(AddressingVersion av, String wrapper, String problemValue) {
        this.av = av;
        this.wrapper = wrapper;
        this.problemValue = problemValue;
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return this.av.nsUri;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return this.av.faultDetailTag.getLocalPart();
    }

    @Override
    @Nullable
    public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
        return null;
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        MutableXMLStreamBuffer buf = new MutableXMLStreamBuffer();
        XMLStreamWriter w = buf.createFromXMLStreamWriter();
        this.writeTo(w);
        return buf.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        w.writeStartElement("", this.av.faultDetailTag.getLocalPart(), this.av.faultDetailTag.getNamespaceURI());
        w.writeDefaultNamespace(this.av.nsUri);
        w.writeStartElement("", this.wrapper, this.av.nsUri);
        w.writeCharacters(this.problemValue);
        w.writeEndElement();
        w.writeEndElement();
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        SOAPHeaderElement she = header.addHeaderElement(this.av.faultDetailTag);
        she = header.addHeaderElement(new QName(this.av.nsUri, this.wrapper));
        she.addTextNode(this.problemValue);
    }

    @Override
    public void writeTo(ContentHandler h, ErrorHandler errorHandler) throws SAXException {
        String nsUri = this.av.nsUri;
        String ln = this.av.faultDetailTag.getLocalPart();
        h.startPrefixMapping("", nsUri);
        h.startElement(nsUri, ln, ln, EMPTY_ATTS);
        h.startElement(nsUri, this.wrapper, this.wrapper, EMPTY_ATTS);
        h.characters(this.problemValue.toCharArray(), 0, this.problemValue.length());
        h.endElement(nsUri, this.wrapper, this.wrapper);
        h.endElement(nsUri, ln, ln);
    }
}

