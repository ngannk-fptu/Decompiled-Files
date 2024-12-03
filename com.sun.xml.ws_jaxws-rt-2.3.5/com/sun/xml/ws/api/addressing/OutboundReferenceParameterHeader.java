/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FinalArrayList
 *  com.sun.istack.NotNull
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferException
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.addressing;

import com.sun.istack.FinalArrayList;
import com.sun.istack.NotNull;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferException;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class OutboundReferenceParameterHeader
extends AbstractHeaderImpl {
    private final XMLStreamBuffer infoset;
    private final String nsUri;
    private final String localName;
    private FinalArrayList<Attribute> attributes;
    private static final String TRUE_VALUE = "1";
    private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";

    OutboundReferenceParameterHeader(XMLStreamBuffer infoset, String nsUri, String localName) {
        this.infoset = infoset;
        this.nsUri = nsUri;
        this.localName = localName;
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
    public String getAttribute(String nsUri, String localName) {
        if (this.attributes == null) {
            this.parseAttributes();
        }
        for (int i = this.attributes.size() - 1; i >= 0; --i) {
            Attribute a = (Attribute)this.attributes.get(i);
            if (!a.localName.equals(localName) || !a.nsUri.equals(nsUri)) continue;
            return a.value;
        }
        return null;
    }

    private void parseAttributes() {
        try {
            XMLStreamReader reader = this.readHeader();
            reader.nextTag();
            this.attributes = new FinalArrayList();
            boolean refParamAttrWritten = false;
            for (int i = 0; i < reader.getAttributeCount(); ++i) {
                String attrLocalName = reader.getAttributeLocalName(i);
                String namespaceURI = reader.getAttributeNamespace(i);
                String value = reader.getAttributeValue(i);
                if (namespaceURI.equals(AddressingVersion.W3C.nsUri) && attrLocalName.equals("IS_REFERENCE_PARAMETER")) {
                    refParamAttrWritten = true;
                }
                this.attributes.add((Object)new Attribute(namespaceURI, attrLocalName, value));
            }
            if (!refParamAttrWritten) {
                this.attributes.add((Object)new Attribute(AddressingVersion.W3C.nsUri, IS_REFERENCE_PARAMETER, TRUE_VALUE));
            }
        }
        catch (XMLStreamException e) {
            throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", (Throwable)e);
        }
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return new StreamReaderDelegate((XMLStreamReader)this.infoset.readAsXMLStreamReader()){
            int state;
            {
                this.state = 0;
            }

            @Override
            public int next() throws XMLStreamException {
                return this.check(super.next());
            }

            @Override
            public int nextTag() throws XMLStreamException {
                return this.check(super.nextTag());
            }

            private int check(int type) {
                switch (this.state) {
                    case 0: {
                        if (type != 1) break;
                        this.state = 1;
                        break;
                    }
                    case 1: {
                        this.state = 2;
                        break;
                    }
                }
                return type;
            }

            @Override
            public int getAttributeCount() {
                if (this.state == 1) {
                    return super.getAttributeCount() + 1;
                }
                return super.getAttributeCount();
            }

            @Override
            public String getAttributeLocalName(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER;
                }
                return super.getAttributeLocalName(index);
            }

            @Override
            public String getAttributeNamespace(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return AddressingVersion.W3C.nsUri;
                }
                return super.getAttributeNamespace(index);
            }

            @Override
            public String getAttributePrefix(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "wsa";
                }
                return super.getAttributePrefix(index);
            }

            @Override
            public String getAttributeType(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "CDATA";
                }
                return super.getAttributeType(index);
            }

            @Override
            public String getAttributeValue(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return OutboundReferenceParameterHeader.TRUE_VALUE;
                }
                return super.getAttributeValue(index);
            }

            @Override
            public QName getAttributeName(int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return new QName(AddressingVersion.W3C.nsUri, OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER, "wsa");
                }
                return super.getAttributeName(index);
            }

            @Override
            public String getAttributeValue(String namespaceUri, String localName) {
                if (this.state == 1 && localName.equals(OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER) && namespaceUri.equals(AddressingVersion.W3C.nsUri)) {
                    return OutboundReferenceParameterHeader.TRUE_VALUE;
                }
                return super.getAttributeValue(namespaceUri, localName);
            }
        };
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        this.infoset.writeToXMLStreamWriter((XMLStreamWriter)new XMLStreamWriterFilter(w){
            private boolean root;
            private boolean onRootEl;
            {
                this.root = true;
                this.onRootEl = true;
            }

            @Override
            public void writeStartElement(String localName) throws XMLStreamException {
                super.writeStartElement(localName);
                this.writeAddedAttribute();
            }

            private void writeAddedAttribute() throws XMLStreamException {
                if (!this.root) {
                    this.onRootEl = false;
                    return;
                }
                this.root = false;
                this.writeNamespace("wsa", AddressingVersion.W3C.nsUri);
                super.writeAttribute("wsa", AddressingVersion.W3C.nsUri, OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER, OutboundReferenceParameterHeader.TRUE_VALUE);
            }

            @Override
            public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
                super.writeStartElement(namespaceURI, localName);
                this.writeAddedAttribute();
            }

            @Override
            public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
                boolean prefixDeclared = this.isPrefixDeclared(prefix, namespaceURI);
                super.writeStartElement(prefix, localName, namespaceURI);
                if (!prefixDeclared && !prefix.equals("")) {
                    super.writeNamespace(prefix, namespaceURI);
                }
                this.writeAddedAttribute();
            }

            @Override
            public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
                if (!this.isPrefixDeclared(prefix, namespaceURI)) {
                    super.writeNamespace(prefix, namespaceURI);
                }
            }

            @Override
            public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
                if (this.onRootEl && namespaceURI.equals(AddressingVersion.W3C.nsUri) && localName.equals(OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER)) {
                    return;
                }
                this.writer.writeAttribute(prefix, namespaceURI, localName, value);
            }

            @Override
            public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
                this.writer.writeAttribute(namespaceURI, localName, value);
            }

            private boolean isPrefixDeclared(String prefix, String namespaceURI) {
                return namespaceURI.equals(this.getNamespaceContext().getNamespaceURI(prefix));
            }
        }, true);
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            Element node = (Element)this.infoset.writeTo((Node)header);
            node.setAttributeNS(AddressingVersion.W3C.nsUri, AddressingVersion.W3C.getPrefix() + ":" + IS_REFERENCE_PARAMETER, TRUE_VALUE);
        }
        catch (XMLStreamBufferException e) {
            throw new SOAPException((Throwable)e);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        class Filter
        extends XMLFilterImpl {
            private int depth = 0;

            Filter(ContentHandler ch) {
                this.setContentHandler(ch);
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if (this.depth++ == 0) {
                    super.startPrefixMapping("wsa", AddressingVersion.W3C.nsUri);
                    if (atts.getIndex(AddressingVersion.W3C.nsUri, OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER) == -1) {
                        AttributesImpl atts2 = new AttributesImpl(atts);
                        atts2.addAttribute(AddressingVersion.W3C.nsUri, OutboundReferenceParameterHeader.IS_REFERENCE_PARAMETER, "wsa:IsReferenceParameter", "CDATA", OutboundReferenceParameterHeader.TRUE_VALUE);
                        atts = atts2;
                    }
                }
                super.startElement(uri, localName, qName, atts);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
                if (--this.depth == 0) {
                    super.endPrefixMapping("wsa");
                }
            }
        }
        this.infoset.writeTo((ContentHandler)new Filter(contentHandler), errorHandler);
    }

    static final class Attribute {
        final String nsUri;
        final String localName;
        final String value;

        public Attribute(String nsUri, String localName, String value) {
            this.nsUri = Attribute.fixNull(nsUri);
            this.localName = localName;
            this.value = value;
        }

        private static String fixNull(String s) {
            return s == null ? "" : s;
        }
    }
}

