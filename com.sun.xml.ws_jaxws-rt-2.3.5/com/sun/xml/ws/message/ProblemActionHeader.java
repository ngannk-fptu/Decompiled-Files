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

public class ProblemActionHeader
extends AbstractHeaderImpl {
    @NotNull
    protected String action;
    protected String soapAction;
    @NotNull
    protected AddressingVersion av;
    private static final String actionLocalName = "Action";
    private static final String soapActionLocalName = "SoapAction";

    public ProblemActionHeader(@NotNull String action, @NotNull AddressingVersion av) {
        this(action, null, av);
    }

    public ProblemActionHeader(@NotNull String action, String soapAction, @NotNull AddressingVersion av) {
        assert (action != null);
        assert (av != null);
        this.action = action;
        this.soapAction = soapAction;
        this.av = av;
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return this.av.nsUri;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return "ProblemAction";
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
        w.writeStartElement("", this.getLocalPart(), this.getNamespaceURI());
        w.writeDefaultNamespace(this.getNamespaceURI());
        w.writeStartElement(actionLocalName);
        w.writeCharacters(this.action);
        w.writeEndElement();
        if (this.soapAction != null) {
            w.writeStartElement(soapActionLocalName);
            w.writeCharacters(this.soapAction);
            w.writeEndElement();
        }
        w.writeEndElement();
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        SOAPHeaderElement she = header.addHeaderElement(new QName(this.getNamespaceURI(), this.getLocalPart()));
        she.addChildElement(actionLocalName);
        she.addTextNode(this.action);
        if (this.soapAction != null) {
            she.addChildElement(soapActionLocalName);
            she.addTextNode(this.soapAction);
        }
    }

    @Override
    public void writeTo(ContentHandler h, ErrorHandler errorHandler) throws SAXException {
        String nsUri = this.getNamespaceURI();
        String ln = this.getLocalPart();
        h.startPrefixMapping("", nsUri);
        h.startElement(nsUri, ln, ln, EMPTY_ATTS);
        h.startElement(nsUri, actionLocalName, actionLocalName, EMPTY_ATTS);
        h.characters(this.action.toCharArray(), 0, this.action.length());
        h.endElement(nsUri, actionLocalName, actionLocalName);
        if (this.soapAction != null) {
            h.startElement(nsUri, soapActionLocalName, soapActionLocalName, EMPTY_ATTS);
            h.characters(this.soapAction.toCharArray(), 0, this.soapAction.length());
            h.endElement(nsUri, soapActionLocalName, soapActionLocalName);
        }
        h.endElement(nsUri, ln, ln);
    }
}

