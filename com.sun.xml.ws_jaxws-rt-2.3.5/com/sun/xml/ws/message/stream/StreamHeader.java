/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FinalArrayList
 *  com.sun.istack.NotNull
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferSource
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message.stream;

import com.sun.istack.FinalArrayList;
import com.sun.istack.NotNull;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.message.AbstractHeaderImpl;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.util.Set;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class StreamHeader
extends AbstractHeaderImpl {
    protected final XMLStreamBuffer _mark;
    protected boolean _isMustUnderstand;
    @NotNull
    protected String _role;
    protected boolean _isRelay;
    protected String _localName;
    protected String _namespaceURI;
    private final FinalArrayList<Attribute> attributes;

    protected StreamHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        assert (reader != null && mark != null);
        this._mark = mark;
        this._localName = reader.getLocalName();
        this._namespaceURI = reader.getNamespaceURI();
        this.attributes = this.processHeaderAttributes(reader);
    }

    protected StreamHeader(XMLStreamReader reader) throws XMLStreamException {
        this._localName = reader.getLocalName();
        this._namespaceURI = reader.getNamespaceURI();
        this.attributes = this.processHeaderAttributes(reader);
        this._mark = XMLStreamBuffer.createNewBufferFromXMLStreamReader((XMLStreamReader)reader);
    }

    @Override
    public final boolean isIgnorable(@NotNull SOAPVersion soapVersion, @NotNull Set<String> roles) {
        if (!this._isMustUnderstand) {
            return true;
        }
        if (roles == null) {
            return true;
        }
        return !roles.contains(this._role);
    }

    @Override
    @NotNull
    public String getRole(@NotNull SOAPVersion soapVersion) {
        assert (this._role != null);
        return this._role;
    }

    @Override
    public boolean isRelay() {
        return this._isRelay;
    }

    @Override
    @NotNull
    public String getNamespaceURI() {
        return this._namespaceURI;
    }

    @Override
    @NotNull
    public String getLocalPart() {
        return this._localName;
    }

    @Override
    public String getAttribute(String nsUri, String localName) {
        if (this.attributes != null) {
            for (int i = this.attributes.size() - 1; i >= 0; --i) {
                Attribute a = (Attribute)this.attributes.get(i);
                if (!a.localName.equals(localName) || !a.nsUri.equals(nsUri)) continue;
                return a.value;
            }
        }
        return null;
    }

    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return this._mark.readAsXMLStreamReader();
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        if (this._mark.getInscopeNamespaces().size() > 0) {
            this._mark.writeToXMLStreamWriter(w, true);
        } else {
            this._mark.writeToXMLStreamWriter(w);
        }
    }

    @Override
    public void writeTo(SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header;
            TransformerFactory tf = XmlUtil.newTransformerFactory(true);
            Transformer t = tf.newTransformer();
            XMLStreamBufferSource source = new XMLStreamBufferSource(this._mark);
            DOMResult result = new DOMResult();
            t.transform((Source)source, result);
            Node d = result.getNode();
            if (d.getNodeType() == 9) {
                d = d.getFirstChild();
            }
            if ((header = saaj.getSOAPHeader()) == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            Node node = header.getOwnerDocument().importNode(d, true);
            header.appendChild(node);
        }
        catch (Exception e) {
            throw new SOAPException((Throwable)e);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        this._mark.writeTo(contentHandler);
    }

    @Override
    @NotNull
    public WSEndpointReference readAsEPR(AddressingVersion expected) throws XMLStreamException {
        return new WSEndpointReference(this._mark, expected);
    }

    protected abstract FinalArrayList<Attribute> processHeaderAttributes(XMLStreamReader var1);

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    protected static final class Attribute {
        final String nsUri;
        final String localName;
        final String value;

        public Attribute(String nsUri, String localName, String value) {
            this.nsUri = StreamHeader.fixNull(nsUri);
            this.localName = localName;
            this.value = value;
        }
    }
}

