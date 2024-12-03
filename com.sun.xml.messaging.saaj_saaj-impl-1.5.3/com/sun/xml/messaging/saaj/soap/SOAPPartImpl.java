/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPPart
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.soap.AttachmentPartImpl;
import com.sun.xml.messaging.saaj.soap.Envelope;
import com.sun.xml.messaging.saaj.soap.MessageImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.SOAPIOException;
import com.sun.xml.messaging.saaj.soap.SOAPVersionMismatchException;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.messaging.saaj.util.MimeHeadersUtil;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import com.sun.xml.messaging.saaj.util.XMLDeclarationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public abstract class SOAPPartImpl
extends SOAPPart
implements SOAPDocument {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap", "com.sun.xml.messaging.saaj.soap.LocalStrings");
    protected MimeHeaders headers;
    protected Envelope envelope;
    protected Source source;
    protected SOAPDocumentImpl document = new SOAPDocumentImpl(this);
    private boolean sourceWasSet = false;
    protected boolean omitXmlDecl = true;
    protected String sourceCharsetEncoding = null;
    protected MessageImpl message;
    static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");

    protected SOAPPartImpl() {
        this(null);
    }

    protected SOAPPartImpl(MessageImpl message) {
        this.headers = new MimeHeaders();
        this.message = message;
        this.headers.setHeader("Content-Type", this.getContentType());
    }

    protected abstract String getContentType();

    protected abstract Envelope createEnvelopeFromSource() throws SOAPException;

    protected abstract Envelope createEmptyEnvelope(String var1) throws SOAPException;

    protected abstract SOAPPartImpl duplicateType();

    protected String getContentTypeString() {
        return this.getContentType();
    }

    public boolean isFastInfoset() {
        return this.message != null ? this.message.isFastInfoset() : false;
    }

    public SOAPEnvelope getEnvelope() throws SOAPException {
        if (this.sourceWasSet) {
            this.sourceWasSet = false;
        }
        this.lookForEnvelope();
        if (this.envelope != null) {
            if (this.source != null) {
                this.document.removeChild((Node)((Object)this.envelope));
                this.envelope = this.createEnvelopeFromSource();
            }
        } else if (this.source != null) {
            this.envelope = this.createEnvelopeFromSource();
        } else {
            this.envelope = this.createEmptyEnvelope(null);
            this.document.insertBefore(((EnvelopeImpl)this.envelope).getDomElement(), null);
        }
        return this.envelope;
    }

    protected void lookForEnvelope() throws SOAPException {
        Element envelopeChildElement = this.document.doGetDocumentElement();
        Node soapEnvelope = this.document.findIfPresent(envelopeChildElement);
        if (soapEnvelope == null || soapEnvelope instanceof Envelope) {
            this.envelope = (EnvelopeImpl)((Object)soapEnvelope);
        } else {
            if (this.document.find(envelopeChildElement) == null) {
                log.severe("SAAJ0512.soap.incorrect.factory.used");
                throw new SOAPExceptionImpl("Unable to create envelope: incorrect factory used during tree construction");
            }
            ElementImpl soapElement = (ElementImpl)this.document.find(envelopeChildElement);
            if (soapElement.getLocalName().equalsIgnoreCase("Envelope")) {
                String uri;
                String prefix = soapElement.getPrefix();
                String string = uri = prefix == null ? soapElement.getNamespaceURI() : soapElement.getNamespaceURI(prefix);
                if (!uri.equals("http://schemas.xmlsoap.org/soap/envelope/") && !uri.equals("http://www.w3.org/2003/05/soap-envelope")) {
                    log.severe("SAAJ0513.soap.unknown.ns");
                    throw new SOAPVersionMismatchException("Unable to create envelope from given source because the namespace was not recognized");
                }
            } else {
                log.severe("SAAJ0514.soap.root.elem.not.named.envelope");
                throw new SOAPExceptionImpl("Unable to create envelope from given source because the root element is not named \"Envelope\"");
            }
        }
    }

    public void removeAllMimeHeaders() {
        this.headers.removeAllHeaders();
    }

    public void removeMimeHeader(String header) {
        this.headers.removeHeader(header);
    }

    public String[] getMimeHeader(String name) {
        return this.headers.getHeader(name);
    }

    public void setMimeHeader(String name, String value) {
        this.headers.setHeader(name, value);
    }

    public void addMimeHeader(String name, String value) {
        this.headers.addHeader(name, value);
    }

    public Iterator<MimeHeader> getAllMimeHeaders() {
        return this.headers.getAllHeaders();
    }

    public Iterator<MimeHeader> getMatchingMimeHeaders(String[] names) {
        return this.headers.getMatchingHeaders(names);
    }

    public Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] names) {
        return this.headers.getNonMatchingHeaders(names);
    }

    public Source getContent() throws SOAPException {
        if (this.source != null) {
            InputStream bis = null;
            if (this.source instanceof JAXMStreamSource) {
                StreamSource streamSource = (StreamSource)this.source;
                bis = streamSource.getInputStream();
            } else if (FastInfosetReflection.isFastInfosetSource(this.source)) {
                SAXSource saxSource = (SAXSource)this.source;
                bis = saxSource.getInputSource().getByteStream();
            }
            if (bis != null) {
                try {
                    bis.reset();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            return this.source;
        }
        return ((Envelope)this.getEnvelope()).getContent();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void setContent(Source source) throws SOAPException {
        try {
            if (source instanceof StreamSource) {
                InputStream is = ((StreamSource)source).getInputStream();
                Reader rdr = ((StreamSource)source).getReader();
                if (is != null) {
                    this.source = new JAXMStreamSource(is);
                } else {
                    if (rdr == null) {
                        log.severe("SAAJ0544.soap.no.valid.reader.for.src");
                        throw new SOAPExceptionImpl("Source does not have a valid Reader or InputStream");
                    }
                    this.source = new JAXMStreamSource(rdr);
                }
            } else if (FastInfosetReflection.isFastInfosetSource(source)) {
                InputStream is = FastInfosetReflection.FastInfosetSource_getInputStream(source);
                if (!(is instanceof ByteInputStream)) {
                    try (ByteOutputStream bout = null;){
                        bout = new ByteOutputStream();
                        bout.write(is);
                        FastInfosetReflection.FastInfosetSource_setInputStream(source, bout.newInputStream());
                    }
                }
                this.source = source;
            } else {
                this.source = source;
            }
            this.sourceWasSet = true;
            return;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.severe("SAAJ0545.soap.cannot.set.src.for.part");
            throw new SOAPExceptionImpl("Error setting the source for SOAPPart: " + ex.getMessage());
        }
    }

    public InputStream getContentAsStream() throws IOException {
        if (this.source != null) {
            InputStream is = null;
            if (this.source instanceof StreamSource && !this.isFastInfoset()) {
                is = ((StreamSource)this.source).getInputStream();
            } else if (FastInfosetReflection.isFastInfosetSource(this.source) && this.isFastInfoset()) {
                try {
                    is = FastInfosetReflection.FastInfosetSource_getInputStream(this.source);
                }
                catch (Exception e) {
                    throw new IOException(e.toString());
                }
            }
            if (is != null) {
                if (lazyContentLength) {
                    return is;
                }
                if (!(is instanceof ByteInputStream)) {
                    log.severe("SAAJ0546.soap.stream.incorrect.type");
                    throw new IOException("Internal error: stream not of the right type");
                }
                return (ByteInputStream)is;
            }
        }
        ByteOutputStream b = new ByteOutputStream();
        Envelope env = null;
        try {
            env = (Envelope)this.getEnvelope();
            env.output(b, this.isFastInfoset());
        }
        catch (SOAPException soapException) {
            log.severe("SAAJ0547.soap.cannot.externalize");
            throw new SOAPIOException("SOAP exception while trying to externalize: ", soapException);
        }
        return b.newInputStream();
    }

    MimeBodyPart getMimePart() throws SOAPException {
        try {
            MimeBodyPart headerEnvelope = new MimeBodyPart();
            headerEnvelope.setDataHandler(this.getDataHandler());
            AttachmentPartImpl.copyMimeHeaders(this.headers, headerEnvelope);
            return headerEnvelope;
        }
        catch (SOAPException ex) {
            throw ex;
        }
        catch (Exception ex) {
            log.severe("SAAJ0548.soap.cannot.externalize.hdr");
            throw new SOAPExceptionImpl("Unable to externalize header", ex);
        }
    }

    MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    DataHandler getDataHandler() {
        DataSource ds = new DataSource(){

            public OutputStream getOutputStream() throws IOException {
                throw new IOException("Illegal Operation");
            }

            public String getContentType() {
                return SOAPPartImpl.this.getContentTypeString();
            }

            public String getName() {
                return SOAPPartImpl.this.getContentId();
            }

            public InputStream getInputStream() throws IOException {
                return SOAPPartImpl.this.getContentAsStream();
            }
        };
        return new DataHandler(ds);
    }

    @Override
    public SOAPDocumentImpl getDocument() {
        this.handleNewSource();
        return this.document;
    }

    @Override
    public SOAPPartImpl getSOAPPart() {
        return this;
    }

    public DocumentType getDoctype() {
        return this.document.getDoctype();
    }

    public DOMImplementation getImplementation() {
        return this.document.getImplementation();
    }

    public Element getDocumentElement() {
        try {
            this.getEnvelope();
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
        return this.document.getDocumentElement();
    }

    protected void doGetDocumentElement() {
        this.handleNewSource();
        try {
            this.lookForEnvelope();
        }
        catch (SOAPException sOAPException) {
            // empty catch block
        }
    }

    public Element createElement(String tagName) throws DOMException {
        return this.document.createElement(tagName);
    }

    public DocumentFragment createDocumentFragment() {
        return this.document.createDocumentFragment();
    }

    public Text createTextNode(String data) {
        return this.document.createTextNode(data);
    }

    public Comment createComment(String data) {
        return this.document.createComment(data);
    }

    public CDATASection createCDATASection(String data) throws DOMException {
        return this.document.createCDATASection(data);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return this.document.createProcessingInstruction(target, data);
    }

    public Attr createAttribute(String name) throws DOMException {
        return this.document.createAttribute(name);
    }

    public EntityReference createEntityReference(String name) throws DOMException {
        return this.document.createEntityReference(name);
    }

    public NodeList getElementsByTagName(String tagname) {
        this.handleNewSource();
        return this.document.getElementsByTagName(tagname);
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        this.handleNewSource();
        return this.document.importNode(importedNode, deep);
    }

    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createElementNS(namespaceURI, qualifiedName);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createAttributeNS(namespaceURI, qualifiedName);
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        this.handleNewSource();
        return this.document.getElementsByTagNameNS(namespaceURI, localName);
    }

    public Element getElementById(String elementId) {
        this.handleNewSource();
        return this.document.getElementById(elementId);
    }

    public Node appendChild(Node newChild) throws DOMException {
        this.handleNewSource();
        return this.document.appendChild(newChild);
    }

    public Node cloneNode(boolean deep) {
        this.handleNewSource();
        return this.doCloneNode();
    }

    protected SOAPPartImpl doCloneNode() {
        this.handleNewSource();
        SOAPPartImpl newSoapPart = this.duplicateType();
        newSoapPart.headers = MimeHeadersUtil.copy(this.headers);
        newSoapPart.source = this.source;
        newSoapPart.document = new SOAPDocumentImpl(newSoapPart, (Document)this.document.getDomDocument().cloneNode(true));
        return newSoapPart;
    }

    public NamedNodeMap getAttributes() {
        return this.document.getDomDocument().getAttributes();
    }

    public NodeList getChildNodes() {
        this.handleNewSource();
        return this.document.getChildNodes();
    }

    public Node getFirstChild() {
        this.handleNewSource();
        return this.document.getFirstChild();
    }

    public Node getLastChild() {
        this.handleNewSource();
        return this.document.getLastChild();
    }

    public String getLocalName() {
        return this.document.getDomDocument().getLocalName();
    }

    public String getNamespaceURI() {
        return this.document.getDomDocument().getNamespaceURI();
    }

    public Node getNextSibling() {
        this.handleNewSource();
        return this.document.getNextSibling();
    }

    public String getNodeName() {
        return this.document.getDomDocument().getNodeName();
    }

    public short getNodeType() {
        return this.document.getDomDocument().getNodeType();
    }

    public String getNodeValue() throws DOMException {
        return this.document.getNodeValue();
    }

    public Document getOwnerDocument() {
        return this.document;
    }

    public Node getParentNode() {
        return this.document.getDomDocument().getParentNode();
    }

    public String getPrefix() {
        return this.document.getDomDocument().getPrefix();
    }

    public Node getPreviousSibling() {
        return this.document.getDomDocument().getPreviousSibling();
    }

    public boolean hasAttributes() {
        return this.document.getDomDocument().hasAttributes();
    }

    public boolean hasChildNodes() {
        this.handleNewSource();
        return this.document.hasChildNodes();
    }

    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        this.handleNewSource();
        return this.document.insertBefore(arg0, arg1);
    }

    public boolean isSupported(String arg0, String arg1) {
        return this.document.getDomDocument().isSupported(arg0, arg1);
    }

    public void normalize() {
        this.handleNewSource();
        this.document.normalize();
    }

    public Node removeChild(Node arg0) throws DOMException {
        this.handleNewSource();
        return this.document.removeChild(arg0);
    }

    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        this.handleNewSource();
        return this.document.replaceChild(arg0, arg1);
    }

    public void setNodeValue(String arg0) throws DOMException {
        this.document.setNodeValue(arg0);
    }

    public void setPrefix(String arg0) throws DOMException {
        this.document.setPrefix(arg0);
    }

    private void handleNewSource() {
        if (this.sourceWasSet) {
            try {
                this.getEnvelope();
            }
            catch (SOAPException sOAPException) {
                // empty catch block
            }
        }
    }

    protected XMLDeclarationParser lookForXmlDecl() throws SOAPException {
        if (this.source != null && this.source instanceof StreamSource) {
            Reader reader = null;
            InputStream inputStream = ((StreamSource)this.source).getInputStream();
            if (inputStream != null) {
                if (this.getSourceCharsetEncoding() == null) {
                    reader = new InputStreamReader(inputStream);
                } else {
                    try {
                        reader = new InputStreamReader(inputStream, this.getSourceCharsetEncoding());
                    }
                    catch (UnsupportedEncodingException uee) {
                        log.log(Level.SEVERE, "SAAJ0551.soap.unsupported.encoding", new Object[]{this.getSourceCharsetEncoding()});
                        throw new SOAPExceptionImpl("Unsupported encoding " + this.getSourceCharsetEncoding(), uee);
                    }
                }
            } else {
                reader = ((StreamSource)this.source).getReader();
            }
            if (reader != null) {
                PushbackReader pushbackReader = new PushbackReader(reader, 4096);
                XMLDeclarationParser ev = new XMLDeclarationParser(pushbackReader);
                try {
                    ev.parse();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "SAAJ0552.soap.xml.decl.parsing.failed");
                    throw new SOAPExceptionImpl("XML declaration parsing failed", e);
                }
                String xmlDecl = ev.getXmlDeclaration();
                if (xmlDecl != null && xmlDecl.length() > 0) {
                    this.omitXmlDecl = false;
                }
                if (lazyContentLength) {
                    this.source = new StreamSource(pushbackReader);
                }
                return ev;
            }
        } else if (this.source == null || this.source instanceof DOMSource) {
            // empty if block
        }
        return null;
    }

    public void setSourceCharsetEncoding(String charset) {
        this.sourceCharsetEncoding = charset;
    }

    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        this.handleNewSource();
        return this.document.renameNode(n, namespaceURI, qualifiedName);
    }

    public void normalizeDocument() {
        this.document.normalizeDocument();
    }

    public DOMConfiguration getDomConfig() {
        return this.document.getDomDocument().getDomConfig();
    }

    public Node adoptNode(Node source) throws DOMException {
        this.handleNewSource();
        return this.document.adoptNode(source);
    }

    public void setDocumentURI(String documentURI) {
        this.document.setDocumentURI(documentURI);
    }

    public String getDocumentURI() {
        return this.document.getDomDocument().getDocumentURI();
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
        this.document.setStrictErrorChecking(strictErrorChecking);
    }

    public String getInputEncoding() {
        return this.document.getDomDocument().getInputEncoding();
    }

    public String getXmlEncoding() {
        return this.document.getDomDocument().getXmlEncoding();
    }

    public boolean getXmlStandalone() {
        return this.document.getDomDocument().getXmlStandalone();
    }

    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        this.document.setXmlStandalone(xmlStandalone);
    }

    public String getXmlVersion() {
        return this.document.getDomDocument().getXmlVersion();
    }

    public void setXmlVersion(String xmlVersion) throws DOMException {
        this.document.setXmlVersion(xmlVersion);
    }

    public boolean getStrictErrorChecking() {
        return this.document.getDomDocument().getStrictErrorChecking();
    }

    public String getBaseURI() {
        return this.document.getDomDocument().getBaseURI();
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        return this.document.compareDocumentPosition(other);
    }

    public String getTextContent() throws DOMException {
        return this.document.getTextContent();
    }

    public void setTextContent(String textContent) throws DOMException {
        this.document.setTextContent(textContent);
    }

    public boolean isSameNode(Node other) {
        return this.document.isSameNode(other);
    }

    public String lookupPrefix(String namespaceURI) {
        return this.document.getDomDocument().lookupPrefix(namespaceURI);
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return this.document.isDefaultNamespace(namespaceURI);
    }

    public String lookupNamespaceURI(String prefix) {
        return this.document.lookupNamespaceURI(prefix);
    }

    public boolean isEqualNode(Node arg) {
        return this.document.getDomDocument().isEqualNode(arg);
    }

    public Object getFeature(String feature, String version) {
        return this.document.getFeature(feature, version);
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.document.setUserData(key, data, handler);
    }

    public Object getUserData(String key) {
        return this.document.getDomDocument().getUserData(key);
    }

    public void recycleNode() {
    }

    public String getValue() {
        return null;
    }

    public void setValue(String value) {
        log.severe("SAAJ0571.soappart.setValue.not.defined");
        throw new IllegalStateException("Setting value of a soap part is not defined");
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        log.severe("SAAJ0570.soappart.parent.element.not.defined");
        throw new SOAPExceptionImpl("The parent element of a soap part is not defined");
    }

    public SOAPElement getParentElement() {
        return null;
    }

    public void detachNode() {
    }

    public String getSourceCharsetEncoding() {
        return this.sourceCharsetEncoding;
    }

    public abstract String getSOAPNamespace();
}

