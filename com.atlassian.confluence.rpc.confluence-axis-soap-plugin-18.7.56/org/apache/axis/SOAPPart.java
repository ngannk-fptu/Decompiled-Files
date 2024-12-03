/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.Part;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.InputStreamBody;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.MimeHeaders;
import org.apache.axis.message.SOAPDocumentImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.transport.http.SocketInputStream;
import org.apache.axis.utils.ByteArray;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPPart
extends javax.xml.soap.SOAPPart
implements Part {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$SOAPPart == null ? (class$org$apache$axis$SOAPPart = SOAPPart.class$("org.apache.axis.SOAPPart")) : class$org$apache$axis$SOAPPart).getName());
    public static final int FORM_STRING = 1;
    public static final int FORM_INPUTSTREAM = 2;
    public static final int FORM_SOAPENVELOPE = 3;
    public static final int FORM_BYTES = 4;
    public static final int FORM_BODYINSTREAM = 5;
    public static final int FORM_FAULT = 6;
    public static final int FORM_OPTIMIZED = 7;
    private int currentForm;
    public static final String ALLOW_FORM_OPTIMIZATION = "axis.form.optimization";
    private MimeHeaders mimeHeaders = new MimeHeaders();
    private static final String[] formNames = new String[]{"", "FORM_STRING", "FORM_INPUTSTREAM", "FORM_SOAPENVELOPE", "FORM_BYTES", "FORM_BODYINSTREAM", "FORM_FAULT", "FORM_OPTIMIZED"};
    private Object currentMessage;
    private String currentEncoding = "UTF-8";
    private String currentMessageAsString = null;
    private byte[] currentMessageAsBytes = null;
    private SOAPEnvelope currentMessageAsEnvelope = null;
    private Message msgObject;
    private Source contentSource = null;
    private Document document = new SOAPDocumentImpl(this);
    protected Document mDocument;
    static /* synthetic */ Class class$org$apache$axis$SOAPPart;

    public SOAPPart(Message parent, Object initialContents, boolean isBodyStream) {
        this.setMimeHeader("Content-Id", SessionUtils.generateSessionId());
        this.setMimeHeader("Content-Type", "text/xml");
        this.msgObject = parent;
        int form = 1;
        if (initialContents instanceof SOAPEnvelope) {
            form = 3;
            ((SOAPEnvelope)initialContents).setOwnerDocument(this);
        } else if (initialContents instanceof InputStream) {
            form = isBodyStream ? 5 : 2;
        } else if (initialContents instanceof byte[]) {
            form = 4;
        } else if (initialContents instanceof AxisFault) {
            form = 6;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Enter: SOAPPart ctor(" + formNames[form] + ")"));
        }
        this.setCurrentMessage(initialContents, form);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SOAPPart ctor()");
        }
    }

    public Message getMessage() {
        return this.msgObject;
    }

    public void setMessage(Message msg) {
        this.msgObject = msg;
    }

    public String getContentType() {
        return "text/xml";
    }

    public long getContentLength() throws AxisFault {
        this.saveChanges();
        if (this.currentForm == 7) {
            return ((ByteArray)this.currentMessage).size();
        }
        if (this.currentForm == 4) {
            return ((byte[])this.currentMessage).length;
        }
        byte[] bytes = this.getAsBytes();
        return bytes.length;
    }

    public void setSOAPEnvelope(SOAPEnvelope env) {
        this.setCurrentMessage(env, 3);
    }

    public void writeTo(OutputStream os) throws IOException {
        if (this.currentForm == 4) {
            os.write((byte[])this.currentMessage);
        } else if (this.currentForm == 7) {
            ((ByteArray)this.currentMessage).writeTo(os);
        } else {
            Writer writer = new OutputStreamWriter(os, this.currentEncoding);
            writer = new BufferedWriter(new PrintWriter(writer));
            this.writeTo(writer);
            writer.flush();
        }
    }

    public void writeTo(Writer writer) throws IOException {
        Serializable env;
        boolean inclXmlDecl = false;
        if (this.msgObject.getMessageContext() != null) {
            inclXmlDecl = true;
        } else {
            try {
                String xmlDecl = (String)this.msgObject.getProperty("javax.xml.soap.write-xml-declaration");
                if (xmlDecl != null && xmlDecl.equals("true")) {
                    inclXmlDecl = true;
                }
            }
            catch (SOAPException e) {
                throw new IOException(e.getMessage());
            }
        }
        if (this.currentForm == 6) {
            env = (AxisFault)this.currentMessage;
            try {
                SerializationContext serContext = new SerializationContext(writer, this.getMessage().getMessageContext());
                serContext.setSendDecl(inclXmlDecl);
                serContext.setEncoding(this.currentEncoding);
                ((AxisFault)env).output(serContext);
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                throw env;
            }
            return;
        }
        if (this.currentForm == 3) {
            env = (SOAPEnvelope)this.currentMessage;
            try {
                SerializationContext serContext = new SerializationContext(writer, this.getMessage().getMessageContext());
                serContext.setSendDecl(inclXmlDecl);
                serContext.setEncoding(this.currentEncoding);
                ((MessageElement)env).output(serContext);
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            return;
        }
        String xml = this.getAsString();
        if (inclXmlDecl && !xml.startsWith("<?xml")) {
            writer.write("<?xml version=\"1.0\" encoding=\"");
            writer.write(this.currentEncoding);
            writer.write("\"?>");
        }
        writer.write(xml);
    }

    public Object getCurrentMessage() {
        return this.currentMessage;
    }

    public void setCurrentMessage(Object currMsg, int form) {
        this.currentMessageAsString = null;
        this.currentMessageAsBytes = null;
        this.currentMessageAsEnvelope = null;
        this.setCurrentForm(currMsg, form);
    }

    private void setCurrentForm(Object currMsg, int form) {
        if (log.isDebugEnabled()) {
            String msgStr = currMsg instanceof String ? (String)currMsg : currMsg.getClass().getName();
            log.debug((Object)Messages.getMessage("setMsgForm", formNames[form], "" + msgStr));
        }
        if (this.isFormOptimizationAllowed()) {
            this.currentMessage = currMsg;
            this.currentForm = form;
            if (this.currentForm == 3) {
                this.currentMessageAsEnvelope = (SOAPEnvelope)currMsg;
            }
        }
    }

    private boolean isFormOptimizationAllowed() {
        Boolean propFormOptimization;
        MessageContext ctx;
        boolean allowFormOptimization = true;
        Message msg = this.getMessage();
        if (msg != null && (ctx = msg.getMessageContext()) != null && (propFormOptimization = (Boolean)ctx.getProperty(ALLOW_FORM_OPTIMIZATION)) != null) {
            allowFormOptimization = propFormOptimization;
        }
        return allowFormOptimization;
    }

    public int getCurrentForm() {
        return this.currentForm;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getAsBytes() throws AxisFault {
        block23: {
            log.debug((Object)"Enter: SOAPPart::getAsBytes");
            if (this.currentForm == 7) {
                log.debug((Object)"Exit: SOAPPart::getAsBytes");
                try {
                    return ((ByteArray)this.currentMessage).toByteArray();
                }
                catch (IOException e) {
                    throw AxisFault.makeFault(e);
                }
            }
            if (this.currentForm == 4) {
                log.debug((Object)"Exit: SOAPPart::getAsBytes");
                return (byte[])this.currentMessage;
            }
            if (this.currentForm == 5) {
                try {
                    this.getAsSOAPEnvelope();
                }
                catch (Exception e) {
                    log.fatal((Object)Messages.getMessage("makeEnvFail00"), (Throwable)e);
                    log.debug((Object)"Exit: SOAPPart::getAsBytes");
                    return null;
                }
            }
            if (this.currentForm != 2) break block23;
            try {
                InputStream inp = null;
                byte[] buf = null;
                try {
                    int len;
                    inp = (InputStream)this.currentMessage;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    buf = new byte[4096];
                    while ((len = inp.read(buf, 0, 4096)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    buf = baos.toByteArray();
                    Object var6_12 = null;
                }
                catch (Throwable throwable) {
                    Object var6_13 = null;
                    if (inp != null && this.currentMessage instanceof SocketInputStream) {
                        inp.close();
                    }
                    throw throwable;
                }
                if (inp != null && this.currentMessage instanceof SocketInputStream) {
                    inp.close();
                }
                this.setCurrentForm(buf, 4);
                log.debug((Object)"Exit: SOAPPart::getAsBytes");
                return (byte[])this.currentMessage;
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                log.debug((Object)"Exit: SOAPPart::getAsBytes");
                return null;
            }
        }
        if (this.currentForm == 3 || this.currentForm == 6) {
            this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream os = new BufferedOutputStream(baos);
            try {
                this.writeTo(os);
                os.flush();
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            this.setCurrentForm(baos.toByteArray(), 4);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsBytes(): " + this.currentMessage));
            }
            return (byte[])this.currentMessage;
        }
        if (this.currentForm == 1) {
            if (this.currentMessage == this.currentMessageAsString && this.currentMessageAsBytes != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Exit: SOAPPart::getAsBytes()");
                }
                return this.currentMessageAsBytes;
            }
            this.currentMessageAsString = (String)this.currentMessage;
            try {
                this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
                this.setCurrentForm(((String)this.currentMessage).getBytes(this.currentEncoding), 4);
            }
            catch (UnsupportedEncodingException ue) {
                this.setCurrentForm(((String)this.currentMessage).getBytes(), 4);
            }
            this.currentMessageAsBytes = (byte[])this.currentMessage;
            log.debug((Object)"Exit: SOAPPart::getAsBytes");
            return (byte[])this.currentMessage;
        }
        log.error((Object)Messages.getMessage("cantConvert00", "" + this.currentForm));
        log.debug((Object)"Exit: SOAPPart::getAsBytes");
        return null;
    }

    public void saveChanges() throws AxisFault {
        log.debug((Object)"Enter: SOAPPart::saveChanges");
        if (this.currentForm == 3 || this.currentForm == 6) {
            this.currentEncoding = XMLUtils.getEncoding(this.msgObject, null);
            ByteArray array = new ByteArray();
            try {
                this.writeTo(array);
                array.flush();
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            this.setCurrentForm(array, 7);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::saveChanges(): " + this.currentMessage));
            }
        }
    }

    public String getAsString() throws AxisFault {
        log.debug((Object)"Enter: SOAPPart::getAsString");
        if (this.currentForm == 1) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessage));
            }
            return (String)this.currentMessage;
        }
        if (this.currentForm == 2 || this.currentForm == 5) {
            this.getAsBytes();
        }
        if (this.currentForm == 7) {
            try {
                this.currentMessageAsBytes = ((ByteArray)this.currentMessage).toByteArray();
            }
            catch (IOException e) {
                throw AxisFault.makeFault(e);
            }
            try {
                this.setCurrentForm(new String(this.currentMessageAsBytes, this.currentEncoding), 1);
            }
            catch (UnsupportedEncodingException ue) {
                this.setCurrentForm(new String(this.currentMessageAsBytes), 1);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessage));
            }
            return (String)this.currentMessage;
        }
        if (this.currentForm == 4) {
            if (this.currentMessage == this.currentMessageAsBytes && this.currentMessageAsString != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessageAsString));
                }
                return this.currentMessageAsString;
            }
            this.currentMessageAsBytes = (byte[])this.currentMessage;
            try {
                this.setCurrentForm(new String((byte[])this.currentMessage, this.currentEncoding), 1);
            }
            catch (UnsupportedEncodingException ue) {
                this.setCurrentForm(new String((byte[])this.currentMessage), 1);
            }
            this.currentMessageAsString = (String)this.currentMessage;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessage));
            }
            return (String)this.currentMessage;
        }
        if (this.currentForm == 6) {
            StringWriter writer = new StringWriter();
            try {
                this.writeTo(writer);
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
                return null;
            }
            this.setCurrentForm(writer.getBuffer().toString(), 1);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessage));
            }
            return (String)this.currentMessage;
        }
        if (this.currentForm == 3) {
            StringWriter writer = new StringWriter();
            try {
                this.writeTo(writer);
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            this.setCurrentForm(writer.getBuffer().toString(), 1);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exit: SOAPPart::getAsString(): " + this.currentMessage));
            }
            return (String)this.currentMessage;
        }
        log.error((Object)Messages.getMessage("cantConvert01", "" + this.currentForm));
        log.debug((Object)"Exit: SOAPPart::getAsString()");
        return null;
    }

    public SOAPEnvelope getAsSOAPEnvelope() throws AxisFault {
        InputSource is;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SOAPPart::getAsSOAPEnvelope()");
            log.debug((Object)Messages.getMessage("currForm", formNames[this.currentForm]));
        }
        if (this.currentForm == 3) {
            return (SOAPEnvelope)this.currentMessage;
        }
        if (this.currentForm == 5) {
            InputStreamBody bodyEl = new InputStreamBody((InputStream)this.currentMessage);
            SOAPEnvelope env = new SOAPEnvelope();
            env.setOwnerDocument(this);
            env.addBodyElement(bodyEl);
            this.setCurrentForm(env, 3);
            return env;
        }
        if (this.currentForm == 2) {
            is = new InputSource((InputStream)this.currentMessage);
            String encoding = XMLUtils.getEncoding(this.msgObject, null, null);
            if (encoding != null) {
                this.currentEncoding = encoding;
                is.setEncoding(this.currentEncoding);
            }
        } else {
            is = new InputSource(new StringReader(this.getAsString()));
        }
        DeserializationContext dser = new DeserializationContext(is, this.getMessage().getMessageContext(), this.getMessage().getMessageType());
        dser.getEnvelope().setOwnerDocument(this);
        try {
            dser.parse();
        }
        catch (SAXException e) {
            Exception real = e.getException();
            if (real == null) {
                real = e;
            }
            throw AxisFault.makeFault(real);
        }
        SOAPEnvelope nse = dser.getEnvelope();
        if (this.currentMessageAsEnvelope != null) {
            Vector newHeaders = nse.getHeaders();
            Vector oldHeaders = this.currentMessageAsEnvelope.getHeaders();
            if (null != newHeaders && null != oldHeaders) {
                Iterator ohi = oldHeaders.iterator();
                Iterator nhi = newHeaders.iterator();
                while (ohi.hasNext() && nhi.hasNext()) {
                    SOAPHeaderElement nhe = (SOAPHeaderElement)nhi.next();
                    SOAPHeaderElement ohe = (SOAPHeaderElement)ohi.next();
                    if (!ohe.isProcessed()) continue;
                    nhe.setProcessed(true);
                }
            }
        }
        this.setCurrentForm(nse, 3);
        log.debug((Object)"Exit: SOAPPart::getAsSOAPEnvelope");
        SOAPEnvelope env = (SOAPEnvelope)this.currentMessage;
        env.setOwnerDocument(this);
        return env;
    }

    public void addMimeHeader(String header, String value) {
        this.mimeHeaders.addHeader(header, value);
    }

    private String getFirstMimeHeader(String header) {
        String[] values = this.mimeHeaders.getHeader(header);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public String getContentLocation() {
        return this.getFirstMimeHeader("Content-Location");
    }

    public void setContentLocation(String loc) {
        this.setMimeHeader("Content-Location", loc);
    }

    public void setContentId(String newCid) {
        this.setMimeHeader("Content-Id", newCid);
    }

    public String getContentId() {
        return this.getFirstMimeHeader("Content-Id");
    }

    public String getContentIdRef() {
        return "cid:" + this.getContentId();
    }

    public Iterator getMatchingMimeHeaders(String[] match) {
        return this.mimeHeaders.getMatchingHeaders(match);
    }

    public Iterator getNonMatchingMimeHeaders(String[] match) {
        return this.mimeHeaders.getNonMatchingHeaders(match);
    }

    public void setContent(Source source) throws SOAPException {
        if (source == null) {
            throw new SOAPException(Messages.getMessage("illegalArgumentException00"));
        }
        MessageContext ctx = this.getMessage().getMessageContext();
        if (ctx != null) {
            ctx.setProperty(ALLOW_FORM_OPTIMIZATION, Boolean.TRUE);
        }
        this.contentSource = source;
        InputSource in = XMLUtils.sourceToInputSource(this.contentSource);
        InputStream is = in.getByteStream();
        if (is != null) {
            this.setCurrentMessage(is, 2);
        } else {
            Reader r = in.getCharacterStream();
            if (r == null) {
                throw new SOAPException(Messages.getMessage("noCharacterOrByteStream"));
            }
            BufferedReader br = new BufferedReader(r);
            String line = null;
            StringBuffer sb = new StringBuffer();
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            catch (IOException e) {
                throw new SOAPException(Messages.getMessage("couldNotReadFromCharStream"), e);
            }
            this.setCurrentMessage(sb.toString(), 1);
        }
    }

    public Source getContent() throws SOAPException {
        if (this.contentSource == null) {
            switch (this.currentForm) {
                case 1: {
                    String s = (String)this.currentMessage;
                    this.contentSource = new StreamSource(new StringReader(s));
                    break;
                }
                case 2: {
                    this.contentSource = new StreamSource((InputStream)this.currentMessage);
                    break;
                }
                case 3: {
                    SOAPEnvelope se = (SOAPEnvelope)this.currentMessage;
                    try {
                        this.contentSource = new DOMSource(se.getAsDocument());
                        break;
                    }
                    catch (Exception e) {
                        throw new SOAPException(Messages.getMessage("errorGetDocFromSOAPEnvelope"), e);
                    }
                }
                case 7: {
                    try {
                        ByteArrayInputStream baos = new ByteArrayInputStream(((ByteArray)this.currentMessage).toByteArray());
                        this.contentSource = new StreamSource(baos);
                        break;
                    }
                    catch (IOException e) {
                        throw new SOAPException(Messages.getMessage("errorGetDocFromSOAPEnvelope"), e);
                    }
                }
                case 4: {
                    byte[] bytes = (byte[])this.currentMessage;
                    this.contentSource = new StreamSource(new ByteArrayInputStream(bytes));
                    break;
                }
                case 5: {
                    this.contentSource = new StreamSource((InputStream)this.currentMessage);
                }
            }
        }
        return this.contentSource;
    }

    public Iterator getAllMimeHeaders() {
        return this.mimeHeaders.getAllHeaders();
    }

    public void setMimeHeader(String name, String value) {
        this.mimeHeaders.setHeader(name, value);
    }

    public String[] getMimeHeader(String name) {
        return this.mimeHeaders.getHeader(name);
    }

    public void removeAllMimeHeaders() {
        this.mimeHeaders.removeAllHeaders();
    }

    public void removeMimeHeader(String header) {
        this.mimeHeaders.removeHeader(header);
    }

    public javax.xml.soap.SOAPEnvelope getEnvelope() throws SOAPException {
        try {
            return this.getAsSOAPEnvelope();
        }
        catch (AxisFault af) {
            throw new SOAPException(af);
        }
    }

    public Document getSOAPDocument() {
        if (this.document == null) {
            this.document = new SOAPDocumentImpl(this);
        }
        return this.document;
    }

    public DocumentType getDoctype() {
        return this.document.getDoctype();
    }

    public DOMImplementation getImplementation() {
        return this.document.getImplementation();
    }

    public Element getDocumentElement() {
        try {
            return this.getEnvelope();
        }
        catch (SOAPException se) {
            return null;
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
        return this.document.getElementsByTagName(tagname);
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        return this.document.importNode(importedNode, deep);
    }

    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createElementNS(namespaceURI, qualifiedName);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createAttributeNS(namespaceURI, qualifiedName);
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return this.document.getElementsByTagNameNS(namespaceURI, localName);
    }

    public Element getElementById(String elementId) {
        return this.document.getElementById(elementId);
    }

    public String getEncoding() {
        return this.currentEncoding;
    }

    public void setEncoding(String s) {
        this.currentEncoding = s;
    }

    public boolean getStandalone() {
        throw new UnsupportedOperationException("Not yet implemented.71");
    }

    public void setStandalone(boolean flag) {
        throw new UnsupportedOperationException("Not yet implemented.72");
    }

    public boolean getStrictErrorChecking() {
        throw new UnsupportedOperationException("Not yet implemented.73");
    }

    public void setStrictErrorChecking(boolean flag) {
        throw new UnsupportedOperationException("Not yet implemented. 74");
    }

    public String getVersion() {
        throw new UnsupportedOperationException("Not yet implemented. 75");
    }

    public void setVersion(String s) {
        throw new UnsupportedOperationException("Not yet implemented.76");
    }

    public Node adoptNode(Node node) throws DOMException {
        throw new UnsupportedOperationException("Not yet implemented.77");
    }

    public String getNodeName() {
        return this.document.getNodeName();
    }

    public String getNodeValue() throws DOMException {
        return this.document.getNodeValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.document.setNodeValue(nodeValue);
    }

    public short getNodeType() {
        return this.document.getNodeType();
    }

    public Node getParentNode() {
        return this.document.getParentNode();
    }

    public NodeList getChildNodes() {
        return this.document.getChildNodes();
    }

    public Node getFirstChild() {
        return this.document.getFirstChild();
    }

    public Node getLastChild() {
        return this.document.getLastChild();
    }

    public Node getPreviousSibling() {
        return this.document.getPreviousSibling();
    }

    public Node getNextSibling() {
        return this.document.getNextSibling();
    }

    public NamedNodeMap getAttributes() {
        return this.document.getAttributes();
    }

    public Document getOwnerDocument() {
        return this.document.getOwnerDocument();
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return this.document.insertBefore(newChild, refChild);
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return this.document.replaceChild(newChild, oldChild);
    }

    public Node removeChild(Node oldChild) throws DOMException {
        return this.document.removeChild(oldChild);
    }

    public Node appendChild(Node newChild) throws DOMException {
        return this.document.appendChild(newChild);
    }

    public boolean hasChildNodes() {
        return this.document.hasChildNodes();
    }

    public Node cloneNode(boolean deep) {
        return this.document.cloneNode(deep);
    }

    public void normalize() {
        this.document.normalize();
    }

    public boolean isSupported(String feature, String version) {
        return this.document.isSupported(feature, version);
    }

    public String getNamespaceURI() {
        return this.document.getNamespaceURI();
    }

    public String getPrefix() {
        return this.document.getPrefix();
    }

    public void setPrefix(String prefix) throws DOMException {
        this.document.setPrefix(prefix);
    }

    public String getLocalName() {
        return this.document.getLocalName();
    }

    public boolean hasAttributes() {
        return this.document.hasAttributes();
    }

    public boolean isBodyStream() {
        return this.currentForm == 2 || this.currentForm == 5;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

