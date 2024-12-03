/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.llom.SOAPElement;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;

public abstract class SOAPBodyImpl
extends SOAPElement
implements SOAPBody,
OMConstants {
    private boolean enableLookAhead = true;
    private boolean lookAheadAttempted = false;
    private boolean lookAheadSuccessful = false;
    private String lookAheadLocalName = null;
    private OMNamespace lookAheadNS = null;

    protected SOAPBodyImpl(String localName, OMNamespace ns, SOAPFactory factory) {
        super(localName, ns, factory);
    }

    public SOAPBodyImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, "Body", true, factory);
    }

    public SOAPBodyImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)envelope, "Body", builder, factory);
    }

    public abstract SOAPFault addFault(Exception var1) throws OMException;

    public boolean hasFault() {
        if (this.hasLookahead()) {
            return "Fault".equals(this.lookAheadLocalName) && this.lookAheadNS != null && ("http://schemas.xmlsoap.org/soap/envelope/".equals(this.lookAheadNS.getNamespaceURI()) || "http://www.w3.org/2003/05/soap-envelope".equals(this.lookAheadNS.getNamespaceURI()));
        }
        return this.getFirstElement() instanceof SOAPFault;
    }

    public SOAPFault getFault() {
        OMElement element = this.getFirstElement();
        return element instanceof SOAPFault ? (SOAPFault)element : null;
    }

    public void addFault(SOAPFault soapFault) throws OMException {
        if (this.hasFault()) {
            throw new OMException("SOAP Body already has a SOAP Fault and there can not be more than one SOAP fault");
        }
        this.addChild(soapFault);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException("Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }

    public OMNode detach() throws OMException {
        throw new SOAPProcessingException("Can not detach SOAP Body, SOAP Envelope must have a Body !!");
    }

    private boolean hasLookahead() {
        if (!this.enableLookAhead) {
            return false;
        }
        if (this.lookAheadAttempted) {
            return this.lookAheadSuccessful;
        }
        this.lookAheadAttempted = true;
        StAXSOAPModelBuilder soapBuilder = (StAXSOAPModelBuilder)this.builder;
        if (soapBuilder != null && soapBuilder.isCache() && !soapBuilder.isCompleted() && !soapBuilder.isClosed()) {
            this.lookAheadSuccessful = soapBuilder.lookahead();
            if (this.lookAheadSuccessful) {
                String prefix;
                this.lookAheadLocalName = soapBuilder.getName();
                String ns = soapBuilder.getNamespace();
                this.lookAheadNS = ns == null ? null : this.factory.createOMNamespace(ns, (prefix = soapBuilder.getPrefix()) == null ? "" : prefix);
            }
        }
        return this.lookAheadSuccessful;
    }

    public OMNamespace getFirstElementNS() {
        if (this.hasLookahead()) {
            return this.lookAheadNS;
        }
        OMElement element = this.getFirstElement();
        if (element == null) {
            return null;
        }
        return element.getNamespace();
    }

    public String getFirstElementLocalName() {
        if (this.hasLookahead()) {
            return this.lookAheadLocalName;
        }
        OMElement element = this.getFirstElement();
        if (element == null) {
            return null;
        }
        return element.getLocalName();
    }

    public void addChild(OMNode child, boolean fromBuilder) {
        this.enableLookAhead = false;
        super.addChild(child, fromBuilder);
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPBody((SOAPEnvelope)targetParent);
    }
}

