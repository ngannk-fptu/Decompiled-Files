/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  org.jvnet.staxex.util.DOMStreamReader
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter$Breakpoint
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.LazyEnvelope;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.StaxBridge;
import com.sun.xml.messaging.saaj.soap.StaxLazySourceBridge;
import com.sun.xml.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.messaging.saaj.util.stax.LazyEnvelopeStaxReader;
import com.sun.xml.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jvnet.staxex.util.DOMStreamReader;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class EnvelopeImpl
extends ElementImpl
implements LazyEnvelope {
    protected HeaderImpl header;
    protected BodyImpl body;
    String omitXmlDecl = "yes";
    String charset = "utf-8";
    String xmlDecl = null;

    protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, Name name) {
        super(ownerDoc, name);
    }

    protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, QName name) {
        super(ownerDoc, name);
    }

    protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, NameImpl name, boolean createHeader, boolean createBody) throws SOAPException {
        this(ownerDoc, name);
        this.ensureNamespaceIsDeclared(this.getElementQName().getPrefix(), this.getElementQName().getNamespaceURI());
        if (createHeader) {
            this.addHeader();
        }
        if (createBody) {
            this.addBody();
        }
    }

    public EnvelopeImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract NameImpl getHeaderName(String var1);

    protected abstract NameImpl getBodyName(String var1);

    public SOAPHeader addHeader() throws SOAPException {
        return this.addHeader(null);
    }

    public SOAPHeader addHeader(String prefix) throws SOAPException {
        if (prefix == null || prefix.equals("")) {
            prefix = this.getPrefix();
        }
        NameImpl headerName = this.getHeaderName(prefix);
        NameImpl bodyName = this.getBodyName(prefix);
        HeaderImpl header = null;
        SOAPElement firstChild = (SOAPElement)this.getFirstChildElement();
        if (firstChild != null) {
            if (firstChild.getElementName().equals(headerName)) {
                log.severe("SAAJ0120.impl.header.already.exists");
                throw new SOAPExceptionImpl("Can't add a header when one is already present.");
            }
            if (!firstChild.getElementName().equals(bodyName)) {
                log.severe("SAAJ0121.impl.invalid.first.child.of.envelope");
                throw new SOAPExceptionImpl("First child of Envelope must be either a Header or Body");
            }
        }
        header = (HeaderImpl)this.createElement(headerName);
        this.insertBefore(header.getDomElement(), (Node)firstChild);
        header.ensureNamespaceIsDeclared(headerName.getPrefix(), headerName.getURI());
        return header;
    }

    protected void lookForHeader() throws SOAPException {
        HeaderImpl hdr2;
        NameImpl headerName = this.getHeaderName(null);
        this.header = hdr2 = (HeaderImpl)this.findChild(headerName);
    }

    public SOAPHeader getHeader() throws SOAPException {
        this.lookForHeader();
        return this.header;
    }

    protected void lookForBody() throws SOAPException {
        BodyImpl bodyChildElement;
        NameImpl bodyName = this.getBodyName(null);
        this.body = bodyChildElement = (BodyImpl)this.findChild(bodyName);
    }

    public SOAPBody addBody() throws SOAPException {
        return this.addBody(null);
    }

    public SOAPBody addBody(String prefix) throws SOAPException {
        this.lookForBody();
        if (prefix == null || prefix.equals("")) {
            prefix = this.getPrefix();
        }
        if (this.body != null) {
            log.severe("SAAJ0122.impl.body.already.exists");
            throw new SOAPExceptionImpl("Can't add a body when one is already present.");
        }
        NameImpl bodyName = this.getBodyName(prefix);
        this.body = (BodyImpl)this.createElement(bodyName);
        this.insertBefore(this.body.getDomElement(), null);
        this.body.ensureNamespaceIsDeclared(bodyName.getPrefix(), bodyName.getURI());
        return this.body;
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        if (this.getBodyName(null).equals(name)) {
            return this.addBody(name.getPrefix());
        }
        if (this.getHeaderName(null).equals(name)) {
            return this.addHeader(name.getPrefix());
        }
        return super.addElement(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        if (this.getBodyName(null).equals(NameImpl.convertToName(name))) {
            return this.addBody(name.getPrefix());
        }
        if (this.getHeaderName(null).equals(NameImpl.convertToName(name))) {
            return this.addHeader(name.getPrefix());
        }
        return super.addElement(name);
    }

    public SOAPBody getBody() throws SOAPException {
        this.lookForBody();
        return this.body;
    }

    @Override
    public Source getContent() {
        return new DOMSource(this.getOwnerDocument());
    }

    public Name createName(String localName, String prefix, String uri) throws SOAPException {
        if ("xmlns".equals(prefix)) {
            log.severe("SAAJ0123.impl.no.reserved.xmlns");
            throw new SOAPExceptionImpl("Cannot declare reserved xmlns prefix");
        }
        if (prefix == null && "xmlns".equals(localName)) {
            log.severe("SAAJ0124.impl.qualified.name.cannot.be.xmlns");
            throw new SOAPExceptionImpl("Qualified name cannot be xmlns");
        }
        return NameImpl.create(localName, prefix, uri);
    }

    public Name createName(String localName, String prefix) throws SOAPException {
        String namespace = this.getNamespaceURI(prefix);
        if (namespace == null) {
            log.log(Level.SEVERE, "SAAJ0126.impl.cannot.locate.ns", new String[]{prefix});
            throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
        }
        return NameImpl.create(localName, prefix, namespace);
    }

    public Name createName(String localName) throws SOAPException {
        return NameImpl.createFromUnqualifiedName(localName);
    }

    public void setOmitXmlDecl(String value) {
        this.omitXmlDecl = value;
    }

    public void setXmlDecl(String value) {
        this.xmlDecl = value;
    }

    public void setCharsetEncoding(String value) {
        this.charset = value;
    }

    @Override
    public void output(OutputStream out) throws IOException {
        try {
            Transformer transformer = EfficientStreamingTransformer.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("encoding", this.charset);
            if (this.omitXmlDecl.equals("no") && this.xmlDecl == null) {
                this.xmlDecl = "<?xml version=\"" + this.getOwnerDocument().getXmlVersion() + "\" encoding=\"" + this.charset + "\" ?>";
            }
            StreamResult result = new StreamResult(out);
            if (this.xmlDecl != null) {
                OutputStreamWriter writer = new OutputStreamWriter(out, this.charset);
                writer.write(this.xmlDecl);
                writer.flush();
                result = new StreamResult(writer);
            }
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "SAAJ0190.impl.set.xml.declaration", new String[]{this.omitXmlDecl});
                log.log(Level.FINE, "SAAJ0191.impl.set.encoding", new String[]{this.charset});
            }
            transformer.transform(this.getContent(), result);
        }
        catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void output(OutputStream out, boolean isFastInfoset) throws IOException {
        if (!isFastInfoset) {
            this.output(out);
        } else {
            try {
                Transformer transformer = EfficientStreamingTransformer.newTransformer();
                transformer.transform(this.getContent(), FastInfosetReflection.FastInfosetResult_new(out));
            }
            catch (Exception ex) {
                throw new IOException(ex.getMessage());
            }
        }
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
        log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }

    @Override
    public void setStaxBridge(StaxBridge bridge) throws SOAPException {
        ((BodyImpl)this.getBody()).setStaxBridge(bridge);
    }

    @Override
    public StaxBridge getStaxBridge() throws SOAPException {
        return ((BodyImpl)this.getBody()).getStaxBridge();
    }

    @Override
    public XMLStreamReader getPayloadReader() throws SOAPException {
        return ((BodyImpl)this.getBody()).getPayloadReader();
    }

    @Override
    public void writeTo(XMLStreamWriter writer) throws XMLStreamException, SOAPException {
        StaxBridge readBridge = this.getStaxBridge();
        if (readBridge != null && readBridge instanceof StaxLazySourceBridge) {
            final String soapEnvNS = this.getNamespaceURI();
            DOMStreamReader reader = new DOMStreamReader((Node)((Object)this));
            XMLStreamReaderToXMLStreamWriter writingBridge = new XMLStreamReaderToXMLStreamWriter();
            writingBridge.bridge(new XMLStreamReaderToXMLStreamWriter.Breakpoint((XMLStreamReader)reader, writer){

                public boolean proceedAfterStartElement() {
                    return !"Body".equals(this.reader.getLocalName()) || !soapEnvNS.equals(this.reader.getNamespaceURI());
                }
            });
            ((StaxLazySourceBridge)readBridge).writePayloadTo(writer);
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
        } else {
            LazyEnvelopeStaxReader lazyEnvReader = new LazyEnvelopeStaxReader(this);
            XMLStreamReaderToXMLStreamWriter writingBridge = new XMLStreamReaderToXMLStreamWriter();
            writingBridge.bridge((XMLStreamReader)((Object)lazyEnvReader), writer);
        }
        ((BodyImpl)this.getBody()).setPayloadStreamRead();
    }

    @Override
    public QName getPayloadQName() throws SOAPException {
        return ((BodyImpl)this.getBody()).getPayloadQName();
    }

    @Override
    public String getPayloadAttributeValue(String localName) throws SOAPException {
        return ((BodyImpl)this.getBody()).getPayloadAttributeValue(localName);
    }

    @Override
    public String getPayloadAttributeValue(QName qName) throws SOAPException {
        return ((BodyImpl)this.getBody()).getPayloadAttributeValue(qName);
    }

    @Override
    public boolean isLazy() {
        try {
            return ((BodyImpl)this.getBody()).isLazy();
        }
        catch (SOAPException e) {
            return false;
        }
    }
}

