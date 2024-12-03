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
import com.sun.xml.ws.api.SOAPVersion;
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
import org.xml.sax.helpers.AttributesImpl;

public class StringHeader
extends AbstractHeaderImpl {
    protected final QName name;
    protected final String value;
    protected boolean mustUnderstand = false;
    protected SOAPVersion soapVersion;
    protected static final String MUST_UNDERSTAND = "mustUnderstand";
    protected static final String S12_MUST_UNDERSTAND_TRUE = "true";
    protected static final String S11_MUST_UNDERSTAND_TRUE = "1";

    public StringHeader(@NotNull QName name, @NotNull String value) {
        assert (name != null);
        assert (value != null);
        this.name = name;
        this.value = value;
    }

    public StringHeader(@NotNull QName name, @NotNull String value, @NotNull SOAPVersion soapVersion, boolean mustUnderstand) {
        this.name = name;
        this.value = value;
        this.soapVersion = soapVersion;
        this.mustUnderstand = mustUnderstand;
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return this.name.getNamespaceURI();
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return this.name.getLocalPart();
    }

    @Override
    @Nullable
    public String getAttribute(@NotNull String nsUri, @NotNull String localName) {
        if (this.mustUnderstand && this.soapVersion.nsUri.equals(nsUri) && MUST_UNDERSTAND.equals(localName)) {
            return StringHeader.getMustUnderstandLiteral(this.soapVersion);
        }
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
        w.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
        w.writeDefaultNamespace(this.name.getNamespaceURI());
        if (this.mustUnderstand) {
            w.writeNamespace("S", this.soapVersion.nsUri);
            w.writeAttribute("S", this.soapVersion.nsUri, MUST_UNDERSTAND, StringHeader.getMustUnderstandLiteral(this.soapVersion));
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
        if (this.mustUnderstand) {
            she.setMustUnderstand(true);
        }
        she.addTextNode(this.value);
    }

    @Override
    public void writeTo(ContentHandler h, ErrorHandler errorHandler) throws SAXException {
        String nsUri = this.name.getNamespaceURI();
        String ln = this.name.getLocalPart();
        h.startPrefixMapping("", nsUri);
        if (this.mustUnderstand) {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(this.soapVersion.nsUri, MUST_UNDERSTAND, "S:mustUnderstand", "CDATA", StringHeader.getMustUnderstandLiteral(this.soapVersion));
            h.startElement(nsUri, ln, ln, attributes);
        } else {
            h.startElement(nsUri, ln, ln, EMPTY_ATTS);
        }
        h.characters(this.value.toCharArray(), 0, this.value.length());
        h.endElement(nsUri, ln, ln);
    }

    private static String getMustUnderstandLiteral(SOAPVersion sv) {
        if (sv == SOAPVersion.SOAP_12) {
            return S12_MUST_UNDERSTAND_TRUE;
        }
        return S11_MUST_UNDERSTAND_TRUE;
    }
}

