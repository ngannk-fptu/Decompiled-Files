/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.soap.impl.llom;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.llom.OMNodeImpl;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.llom.SOAPBodyImpl;
import org.apache.axiom.soap.impl.llom.SOAPElement;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SOAPEnvelopeImpl
extends SOAPElement
implements SOAPEnvelope,
OMConstants {
    private static final Log log = LogFactory.getLog(SOAPEnvelopeImpl.class);

    public SOAPEnvelopeImpl(SOAPMessage message, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(message, "Envelope", builder, factory);
    }

    public SOAPEnvelopeImpl(OMNamespace ns, SOAPFactory factory) {
        super("Envelope", ns, factory);
    }

    public SOAPVersion getVersion() {
        return ((SOAPFactory)this.factory).getSOAPVersion();
    }

    public SOAPHeader getHeader() throws OMException {
        OMElement e = this.getFirstElement();
        if (e instanceof SOAPHeader) {
            return (SOAPHeader)e;
        }
        return null;
    }

    private void checkChild(OMNode child) {
        if (child instanceof OMElement && !(child instanceof SOAPHeader) && !(child instanceof SOAPBody)) {
            throw new SOAPProcessingException("SOAP Envelope can not have children other than SOAP Header and Body", "Sender");
        }
    }

    public void addChild(OMNode child, boolean fromBuilder) {
        if (this.getVersion() instanceof SOAP12Version) {
            this.checkChild(child);
        }
        if (child instanceof SOAPHeader) {
            if (this.state == 1) {
                SOAPBody body = this.getBody();
                if (body != null) {
                    body.insertSiblingBefore(child);
                    return;
                }
            } else {
                for (OMNode node = this.lastChild; node != null; node = node.getPreviousOMSibling()) {
                    if (!(node instanceof SOAPBody)) continue;
                    node.insertSiblingBefore(child);
                    return;
                }
            }
        }
        super.addChild(child, fromBuilder);
    }

    public SOAPBody getBody() throws OMException {
        OMElement element = this.getFirstElement();
        if (element != null) {
            OMNode node;
            if ("Body".equals(element.getLocalName())) {
                return (SOAPBody)element;
            }
            for (node = element.getNextOMSibling(); node != null && node.getType() != 1; node = node.getNextOMSibling()) {
            }
            if (node == null) {
                return null;
            }
            if ("Body".equals(((OMElement)node).getLocalName())) {
                return (SOAPBody)node;
            }
            throw new OMException("SOAPEnvelope must contain a body element which is either first or second child element of the SOAPEnvelope.");
        }
        return null;
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
    }

    public void internalSerialize(XMLStreamWriter writer2, boolean cache) throws XMLStreamException {
        SOAPBody body;
        SOAPHeader header;
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter)writer2;
        if (!writer.isIgnoreXMLDeclaration()) {
            String charSetEncoding = writer.getCharSetEncoding();
            String xmlVersion = writer.getXmlVersion();
            writer.getXmlStreamWriter().writeStartDocument(charSetEncoding == null ? "utf-8" : charSetEncoding, xmlVersion == null ? "1.0" : xmlVersion);
        }
        if (cache) {
            OMSerializerUtil.serializeStartpart(this, writer);
            header = this.getHeader();
            if (header != null && header.getFirstOMChild() != null) {
                ((SOAPHeaderImpl)header).internalSerialize(writer, true);
            }
            if ((body = this.getBody()) != null) {
                ((SOAPBodyImpl)body).internalSerialize(writer, true);
            }
            OMSerializerUtil.serializeEndpart(writer);
        } else {
            if (this.state == 1 || this.builder == null) {
                OMSerializerUtil.serializeStartpart(this, writer);
                header = this.getHeader();
                if (header != null && header.getFirstOMChild() != null) {
                    this.serializeInternally((OMNodeImpl)((Object)header), writer);
                }
                if ((body = this.getBody()) != null) {
                    this.serializeInternally((OMNodeImpl)((Object)body), writer);
                }
                OMSerializerUtil.serializeEndpart(writer);
            } else {
                OMSerializerUtil.serializeByPullStream(this, writer, cache);
            }
            if (this.builder != null && this.builder instanceof StAXBuilder) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("closing builder: " + this.builder));
                    }
                    StAXBuilder staxBuilder = (StAXBuilder)this.builder;
                    staxBuilder.close();
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error((Object)"Could not close builder or parser due to: ", (Throwable)e);
                    }
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)"Could not close builder or parser due to:");
                if (this.builder == null) {
                    log.debug((Object)"builder is null");
                }
                if (this.builder != null && !(this.builder instanceof StAXBuilder)) {
                    log.debug((Object)("builder is not instance of " + StAXBuilder.class.getName()));
                }
            }
        }
    }

    private void serializeInternally(OMNodeImpl child, MTOMXMLStreamWriter writer) throws XMLStreamException {
        if (!(child instanceof OMElement) || child.isComplete() || child.getBuilder() == null) {
            child.internalSerialize(writer, false);
        } else {
            OMElement element = (OMElement)((Object)child);
            element.getBuilder().setCache(false);
            OMSerializerUtil.serializeByPullStream(element, writer, false);
        }
        child.getNextOMSibling();
    }

    public boolean hasFault() {
        QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null && "Fault".equals(payloadQName.getLocalPart())) {
            String ns = payloadQName.getNamespaceURI();
            return "http://schemas.xmlsoap.org/soap/envelope/".equals(ns) || "http://www.w3.org/2003/05/soap-envelope".equals(ns);
        }
        SOAPBody body = this.getBody();
        return body == null ? false : body.hasFault();
    }

    public String getSOAPBodyFirstElementLocalName() {
        QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null) {
            return payloadQName.getLocalPart();
        }
        SOAPBody body = this.getBody();
        return body == null ? null : body.getFirstElementLocalName();
    }

    public OMNamespace getSOAPBodyFirstElementNS() {
        QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null) {
            return this.factory.createOMNamespace(payloadQName.getNamespaceURI(), payloadQName.getPrefix());
        }
        SOAPBody body = this.getBody();
        return body == null ? null : body.getFirstElementNS();
    }

    private QName getPayloadQName_Optimized() {
        OMXMLParserWrapper builder = this.getBuilder();
        if (builder instanceof StAXSOAPModelBuilder) {
            try {
                QName payloadQName = (QName)((StAXSOAPModelBuilder)builder).getReaderProperty("org.apache.axiom.SOAPBodyFirstChildElementQName");
                return payloadQName;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        SOAPEnvelope clone = ((SOAPFactory)this.factory).createSOAPEnvelope(this.getNamespace());
        if (targetParent != null) {
            targetParent.addChild(clone);
        }
        return clone;
    }
}

