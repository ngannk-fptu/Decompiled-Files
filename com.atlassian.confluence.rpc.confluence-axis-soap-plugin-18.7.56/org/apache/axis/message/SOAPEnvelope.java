/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPEnvelope
extends MessageElement
implements javax.xml.soap.SOAPEnvelope {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$SOAPEnvelope == null ? (class$org$apache$axis$message$SOAPEnvelope = SOAPEnvelope.class$("org.apache.axis.message.SOAPEnvelope")) : class$org$apache$axis$message$SOAPEnvelope).getName());
    private SOAPHeader header;
    private SOAPBody body;
    public Vector trailers = new Vector();
    private SOAPConstants soapConstants;
    private SchemaVersion schemaVersion = SchemaVersion.SCHEMA_2001;
    public String messageType;
    static /* synthetic */ Class class$org$apache$axis$message$SOAPEnvelope;

    public SOAPEnvelope() {
        this(true, SOAPConstants.SOAP11_CONSTANTS);
    }

    public SOAPEnvelope(SOAPConstants soapConstants) {
        this(true, soapConstants);
    }

    public SOAPEnvelope(SOAPConstants soapConstants, SchemaVersion schemaVersion) {
        this(true, soapConstants, schemaVersion);
    }

    public SOAPEnvelope(boolean registerPrefixes, SOAPConstants soapConstants) {
        this(registerPrefixes, soapConstants, SchemaVersion.SCHEMA_2001);
    }

    public SOAPEnvelope(boolean registerPrefixes, SOAPConstants soapConstants, SchemaVersion schemaVersion) {
        super("Envelope", "soapenv", soapConstants != null ? soapConstants.getEnvelopeURI() : Constants.DEFAULT_SOAP_VERSION.getEnvelopeURI());
        if (soapConstants == null) {
            soapConstants = Constants.DEFAULT_SOAP_VERSION;
        }
        this.soapConstants = soapConstants;
        this.schemaVersion = schemaVersion;
        this.header = new SOAPHeader(this, soapConstants);
        this.body = new SOAPBody(this, soapConstants);
        if (registerPrefixes) {
            if (this.namespaces == null) {
                this.namespaces = new ArrayList();
            }
            this.namespaces.add(new Mapping(soapConstants.getEnvelopeURI(), "soapenv"));
            this.namespaces.add(new Mapping(schemaVersion.getXsdURI(), "xsd"));
            this.namespaces.add(new Mapping(schemaVersion.getXsiURI(), "xsi"));
        }
        this.setDirty(true);
    }

    public SOAPEnvelope(InputStream input) throws SAXException {
        InputSource is = new InputSource(input);
        this.header = new SOAPHeader(this, Constants.DEFAULT_SOAP_VERSION);
        DeserializationContext dser = null;
        AxisClient tmpEngine = new AxisClient(new NullProvider());
        MessageContext msgContext = new MessageContext(tmpEngine);
        dser = new DeserializationContext(is, msgContext, "request", this);
        dser.parse();
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Vector getBodyElements() throws AxisFault {
        if (this.body != null) {
            return this.body.getBodyElements();
        }
        return new Vector();
    }

    public Vector getTrailers() {
        return this.trailers;
    }

    public SOAPBodyElement getFirstBody() throws AxisFault {
        if (this.body == null) {
            return null;
        }
        return this.body.getFirstBody();
    }

    public Vector getHeaders() throws AxisFault {
        if (this.header != null) {
            return this.header.getHeaders();
        }
        return new Vector();
    }

    public Vector getHeadersByActor(ArrayList actors) {
        if (this.header != null) {
            return this.header.getHeadersByActor(actors);
        }
        return new Vector();
    }

    public void addHeader(SOAPHeaderElement hdr) {
        if (this.header == null) {
            this.header = new SOAPHeader(this, this.soapConstants);
        }
        hdr.setEnvelope(this);
        this.header.addHeader(hdr);
        this._isDirty = true;
    }

    public void addBodyElement(SOAPBodyElement element) {
        if (this.body == null) {
            this.body = new SOAPBody(this, this.soapConstants);
        }
        element.setEnvelope(this);
        this.body.addBodyElement(element);
        this._isDirty = true;
    }

    public void removeHeaders() {
        if (this.header != null) {
            this.removeChild(this.header);
        }
        this.header = null;
    }

    public void setHeader(SOAPHeader hdr) {
        if (this.header != null) {
            this.removeChild(this.header);
        }
        this.header = hdr;
        try {
            this.header.setParentElement(this);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    public void removeHeader(SOAPHeaderElement hdr) {
        if (this.header != null) {
            this.header.removeHeader(hdr);
            this._isDirty = true;
        }
    }

    public void removeBody() {
        if (this.body != null) {
            this.removeChild(this.body);
        }
        this.body = null;
    }

    public void setBody(SOAPBody body) {
        if (this.body != null) {
            this.removeChild(this.body);
        }
        this.body = body;
        try {
            body.setParentElement(this);
        }
        catch (SOAPException ex) {
            log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
        }
    }

    public void removeBodyElement(SOAPBodyElement element) {
        if (this.body != null) {
            this.body.removeBodyElement(element);
            this._isDirty = true;
        }
    }

    public void removeTrailer(MessageElement element) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("removeTrailer00"));
        }
        this.trailers.removeElement(element);
        this._isDirty = true;
    }

    public void clearBody() {
        if (this.body != null) {
            this.body.clearBody();
            this._isDirty = true;
        }
    }

    public void addTrailer(MessageElement element) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("removeTrailer00"));
        }
        element.setEnvelope(this);
        this.trailers.addElement(element);
        this._isDirty = true;
    }

    public SOAPHeaderElement getHeaderByName(String namespace, String localPart) throws AxisFault {
        return this.getHeaderByName(namespace, localPart, false);
    }

    public SOAPHeaderElement getHeaderByName(String namespace, String localPart, boolean accessAllHeaders) throws AxisFault {
        if (this.header != null) {
            return this.header.getHeaderByName(namespace, localPart, accessAllHeaders);
        }
        return null;
    }

    public SOAPBodyElement getBodyByName(String namespace, String localPart) throws AxisFault {
        if (this.body == null) {
            return null;
        }
        return this.body.getBodyByName(namespace, localPart);
    }

    public Enumeration getHeadersByName(String namespace, String localPart) throws AxisFault {
        return this.getHeadersByName(namespace, localPart, false);
    }

    public Enumeration getHeadersByName(String namespace, String localPart, boolean accessAllHeaders) throws AxisFault {
        if (this.header != null) {
            return this.header.getHeadersByName(namespace, localPart, accessAllHeaders);
        }
        return new Vector().elements();
    }

    public void outputImpl(SerializationContext context) throws Exception {
        boolean oldPretty = context.getPretty();
        context.setPretty(true);
        if (this.namespaces != null) {
            Iterator i = this.namespaces.iterator();
            while (i.hasNext()) {
                Mapping mapping = (Mapping)i.next();
                context.registerPrefixForURI(mapping.getPrefix(), mapping.getNamespaceURI());
            }
        }
        context.startElement(new QName(this.soapConstants.getEnvelopeURI(), "Envelope"), this.attributes);
        Iterator i = this.getChildElements();
        while (i.hasNext()) {
            NodeImpl node = (NodeImpl)i.next();
            if (node instanceof SOAPHeader) {
                this.header.outputImpl(context);
                continue;
            }
            if (node instanceof SOAPBody) {
                this.body.outputImpl(context);
                continue;
            }
            if (node instanceof MessageElement) {
                ((MessageElement)node).output(context);
                continue;
            }
            node.output(context);
        }
        Enumeration enumeration = this.trailers.elements();
        while (enumeration.hasMoreElements()) {
            MessageElement element = (MessageElement)enumeration.nextElement();
            element.output(context);
        }
        context.endElement();
        context.setPretty(oldPretty);
    }

    public SOAPConstants getSOAPConstants() {
        return this.soapConstants;
    }

    public void setSoapConstants(SOAPConstants soapConstants) {
        this.soapConstants = soapConstants;
    }

    public SchemaVersion getSchemaVersion() {
        return this.schemaVersion;
    }

    public void setSchemaVersion(SchemaVersion schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public javax.xml.soap.SOAPBody addBody() throws SOAPException {
        if (this.body == null) {
            this.body = new SOAPBody(this, this.soapConstants);
            this._isDirty = true;
            this.body.setOwnerDocument(this.getOwnerDocument());
            return this.body;
        }
        throw new SOAPException(Messages.getMessage("bodyPresent"));
    }

    public javax.xml.soap.SOAPHeader addHeader() throws SOAPException {
        if (this.header == null) {
            this.header = new SOAPHeader(this, this.soapConstants);
            this.header.setOwnerDocument(this.getOwnerDocument());
            return this.header;
        }
        throw new SOAPException(Messages.getMessage("headerPresent"));
    }

    public Name createName(String localName) throws SOAPException {
        return new PrefixedQName(null, localName, null);
    }

    public Name createName(String localName, String prefix, String uri) throws SOAPException {
        return new PrefixedQName(uri, localName, prefix);
    }

    public javax.xml.soap.SOAPBody getBody() throws SOAPException {
        return this.body;
    }

    public javax.xml.soap.SOAPHeader getHeader() throws SOAPException {
        return this.header;
    }

    public void setSAAJEncodingCompliance(boolean comply) {
        this.body.setSAAJEncodingCompliance(comply);
    }

    public Node removeChild(Node oldChild) throws DOMException {
        if (oldChild == this.header) {
            this.header = null;
        } else if (oldChild == this.body) {
            this.body = null;
        }
        return super.removeChild(oldChild);
    }

    public Node cloneNode(boolean deep) {
        SOAPEnvelope envelope = (SOAPEnvelope)super.cloneNode(deep);
        if (!deep) {
            envelope.body = null;
            envelope.header = null;
        }
        return envelope;
    }

    protected void childDeepCloned(NodeImpl oldNode, NodeImpl newNode) {
        if (oldNode == this.body) {
            this.body = (SOAPBody)newNode;
            try {
                this.body.setParentElement(this);
            }
            catch (SOAPException ex) {
                log.fatal((Object)Messages.getMessage("exception00"), (Throwable)ex);
            }
        } else if (oldNode == this.header) {
            this.header = (SOAPHeader)newNode;
        }
    }

    public void setOwnerDocument(SOAPPart sp) {
        super.setOwnerDocument(sp);
        if (this.body != null) {
            this.body.setOwnerDocument(sp);
            this.setOwnerDocumentForChildren(this.body.children, sp);
        }
        if (this.header != null) {
            this.header.setOwnerDocument(sp);
            this.setOwnerDocumentForChildren(this.body.children, sp);
        }
    }

    private void setOwnerDocumentForChildren(List children, SOAPPart sp) {
        if (children == null) {
            return;
        }
        int size = children.size();
        for (int i = 0; i < size; ++i) {
            NodeImpl node = (NodeImpl)children.get(i);
            node.setOwnerDocument(sp);
            this.setOwnerDocumentForChildren(node.children, sp);
        }
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

