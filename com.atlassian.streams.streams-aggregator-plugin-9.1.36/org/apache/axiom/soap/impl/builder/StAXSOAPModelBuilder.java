/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.soap.impl.builder;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.OMMetaFactoryEx;
import org.apache.axiom.soap.impl.builder.SOAP11BuilderHelper;
import org.apache.axiom.soap.impl.builder.SOAP12BuilderHelper;
import org.apache.axiom.soap.impl.builder.SOAPBuilderHelper;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StAXSOAPModelBuilder
extends StAXOMBuilder
implements SOAPModelBuilder {
    private OMMetaFactory metaFactory;
    private SOAPFactoryEx soapFactory;
    private boolean headerPresent = false;
    private boolean bodyPresent = false;
    private static final Log log = LogFactory.getLog(StAXSOAPModelBuilder.class);
    private boolean processingFault = false;
    private SOAPBuilderHelper builderHelper;

    public StAXSOAPModelBuilder(XMLStreamReader parser, String soapVersion) {
        this(OMAbstractFactory.getMetaFactory(), parser, soapVersion);
    }

    public StAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser, String soapVersion) {
        super(metaFactory.getOMFactory(), parser);
        this.metaFactory = metaFactory;
        this.identifySOAPVersion(soapVersion);
    }

    public StAXSOAPModelBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getMetaFactory(), parser);
    }

    public StAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser) {
        super(metaFactory.getOMFactory(), parser);
        this.metaFactory = metaFactory;
    }

    public StAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory, String soapVersion) {
        super(factory, parser);
        this.soapFactory = (SOAPFactoryEx)factory;
        this.identifySOAPVersion(soapVersion);
    }

    protected void identifySOAPVersion(String soapVersionURIFromTransport) {
        String namespaceName;
        SOAPEnvelope soapEnvelope = this.getSOAPEnvelope();
        if (soapEnvelope == null) {
            throw new SOAPProcessingException("SOAP Message does not contain an Envelope", "VersionMismatch");
        }
        OMNamespace envelopeNamespace = soapEnvelope.getNamespace();
        if (soapVersionURIFromTransport != null && !soapVersionURIFromTransport.equals(namespaceName = envelopeNamespace.getNamespaceURI())) {
            throw new SOAPProcessingException("Transport level information does not match with SOAP Message namespace URI", envelopeNamespace.getPrefix() + ":" + "VersionMismatch");
        }
    }

    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)this.getDocumentElement();
    }

    protected OMNode createNextOMElement() {
        String localPart;
        String namespace;
        CustomBuilder customBuilder;
        OMNode newElement = null;
        if (this.elementLevel == 3 && this.customBuilderForPayload != null && this.target instanceof SOAPBody) {
            newElement = this.createWithCustomBuilder(this.customBuilderForPayload, this.soapFactory);
        }
        if (newElement == null && this.customBuilders != null && this.elementLevel <= this.maxDepthForCustomBuilders && (customBuilder = this.getCustomBuilder(namespace = this.parser.getNamespaceURI(), localPart = this.parser.getLocalName())) != null) {
            newElement = this.createWithCustomBuilder(customBuilder, this.soapFactory);
        }
        if (newElement == null) {
            newElement = this.createOMElement();
        } else {
            --this.elementLevel;
        }
        return newElement;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected OMElement constructNode(OMContainer parent, String elementName) {
        void var3_15;
        if (this.elementLevel == 1) {
            if (!elementName.equals("Envelope")) {
                throw new SOAPProcessingException("First Element must contain the local name, Envelope , but found " + elementName, "");
            }
            String namespaceURI = this.parser.getNamespaceURI();
            if (this.soapFactory == null) {
                if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
                    this.soapFactory = (SOAPFactoryEx)this.metaFactory.getSOAP12Factory();
                    log.debug((Object)"Starting to process SOAP 1.2 message");
                } else {
                    if (!"http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI)) throw new SOAPProcessingException("Only SOAP 1.1 or SOAP 1.2 messages are supported in the system", "VersionMismatch");
                    this.soapFactory = (SOAPFactoryEx)this.metaFactory.getSOAP11Factory();
                    log.debug((Object)"Starting to process SOAP 1.1 message");
                }
            } else if (!this.soapFactory.getSoapVersionURI().equals(namespaceURI)) {
                throw new SOAPProcessingException("Invalid SOAP namespace URI. Expected " + this.soapFactory.getSoapVersionURI(), "Sender");
            }
            SOAPEnvelope sOAPEnvelope = this.soapFactory.createSOAPEnvelope((SOAPMessage)parent, this);
            return var3_15;
        } else if (this.elementLevel == 2) {
            String elementNS = this.parser.getNamespaceURI();
            if (this.soapFactory.getSoapVersionURI().equals(elementNS)) {
                if (elementName.equals("Header")) {
                    if (this.headerPresent) {
                        throw new SOAPProcessingException("Multiple headers encountered!", this.getSenderFaultCode());
                    }
                    if (this.bodyPresent) {
                        throw new SOAPProcessingException("Header Body wrong order!", this.getSenderFaultCode());
                    }
                    this.headerPresent = true;
                    SOAPHeader sOAPHeader = this.soapFactory.createSOAPHeader((SOAPEnvelope)parent, this);
                    return var3_15;
                } else {
                    if (!elementName.equals("Body")) throw new SOAPProcessingException(elementName + " is not supported here.", this.getSenderFaultCode());
                    if (this.bodyPresent) {
                        throw new SOAPProcessingException("Multiple body elements encountered", this.getSenderFaultCode());
                    }
                    this.bodyPresent = true;
                    SOAPBody sOAPBody = this.soapFactory.createSOAPBody((SOAPEnvelope)parent, this);
                }
                return var3_15;
            } else {
                if (this.soapFactory.getSOAPVersion() != SOAP11Version.getSingleton() || !this.bodyPresent) throw new SOAPProcessingException("Disallowed element found inside Envelope : {" + elementNS + "}" + elementName);
                OMElement oMElement = this.omfactory.createOMElement(this.parser.getLocalName(), parent, this);
            }
            return var3_15;
        } else if (this.elementLevel == 3 && ((OMElement)parent).getLocalName().equals("Header")) {
            try {
                SOAPHeaderBlock sOAPHeaderBlock = this.soapFactory.createSOAPHeaderBlock(elementName, (SOAPHeader)parent, this);
                return var3_15;
            }
            catch (SOAPProcessingException e) {
                throw new SOAPProcessingException("Can not create SOAPHeader block", this.getReceiverFaultCode(), e);
            }
        } else if (this.elementLevel == 3 && ((OMElement)parent).getLocalName().equals("Body") && elementName.equals("Fault") && this.soapFactory.getSoapVersionURI().equals(this.parser.getNamespaceURI())) {
            SOAPFault sOAPFault = this.soapFactory.createSOAPFault((SOAPBody)parent, this);
            this.processingFault = true;
            if (this.soapFactory.getSOAPVersion() == SOAP12Version.getSingleton()) {
                this.builderHelper = new SOAP12BuilderHelper(this, this.soapFactory);
                return var3_15;
            } else {
                if (this.soapFactory.getSOAPVersion() != SOAP11Version.getSingleton()) return var3_15;
                this.builderHelper = new SOAP11BuilderHelper(this, this.soapFactory);
            }
            return var3_15;
        } else if (this.elementLevel > 3 && this.processingFault) {
            OMElement oMElement = this.builderHelper.handleEvent(this.parser, (OMElement)parent, this.elementLevel);
            return var3_15;
        } else {
            OMElement oMElement = this.soapFactory.createOMElement(elementName, parent, this);
        }
        return var3_15;
    }

    private String getSenderFaultCode() {
        return this.getSOAPEnvelope().getVersion().getSenderFaultCode().getLocalPart();
    }

    private String getReceiverFaultCode() {
        return this.getSOAPEnvelope().getVersion().getReceiverFaultCode().getLocalPart();
    }

    protected OMDocument createDocument() {
        if (this.soapFactory != null) {
            return this.soapFactory.createSOAPMessage(this);
        }
        return ((OMMetaFactoryEx)this.metaFactory).createSOAPMessage(this);
    }

    protected OMNode createDTD() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain a Document Type Declaration(DTD)");
    }

    protected OMNode createPI() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain Processing Instructions(PI)");
    }

    protected OMNode createEntityReference() {
        throw new SOAPProcessingException("A SOAP message cannot contain entity references because it must not have a DTD");
    }

    public OMNamespace getEnvelopeNamespace() {
        return this.getSOAPEnvelope().getNamespace();
    }

    public SOAPMessage getSoapMessage() {
        return (SOAPMessage)this.getDocument();
    }

    public SOAPFactory getSOAPFactory() {
        if (this.soapFactory == null) {
            this.getSOAPEnvelope();
        }
        return this.soapFactory;
    }
}

