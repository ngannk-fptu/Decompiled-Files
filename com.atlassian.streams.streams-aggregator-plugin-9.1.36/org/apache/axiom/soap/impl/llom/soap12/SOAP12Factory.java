/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.axiom.soap.impl.llom.SOAPMessageImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12BodyImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultNodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultTextImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultValueImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12HeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12HeaderImpl;

public class SOAP12Factory
extends OMLinkedListImplFactory
implements SOAPFactoryEx {
    public SOAP12Factory(OMLinkedListMetaFactory metaFactory) {
        super(metaFactory);
    }

    public SOAP12Factory() {
    }

    public String getSoapVersionURI() {
        return "http://www.w3.org/2003/05/soap-envelope";
    }

    public SOAPVersion getSOAPVersion() {
        return SOAP12Version.getSingleton();
    }

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl("http://www.w3.org/2003/05/soap-envelope", "soapenv");
    }

    public SOAPEnvelope createSOAPEnvelope() {
        return new SOAPEnvelopeImpl(new OMNamespaceImpl("http://www.w3.org/2003/05/soap-envelope", "soapenv"), this);
    }

    public SOAPEnvelope createSOAPEnvelope(OMNamespace ns) {
        return new SOAPEnvelopeImpl(ns, this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP12HeaderImpl(envelope, (SOAPFactory)this);
    }

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException {
        return new SOAP12HeaderImpl(this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
        return new SOAP12HeaderImpl(envelope, builder, (SOAPFactory)this);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl(parent, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, SOAPHeader parent, OMXMLParserWrapper builder) throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl(parent, localName, null, builder, this, false);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
        return new SOAP12FaultImpl(parent, e, (SOAPFactory)this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException {
        return new SOAP12FaultImpl(parent, (SOAPFactory)this);
    }

    public SOAPFault createSOAPFault() throws SOAPProcessingException {
        return new SOAP12FaultImpl(this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP12BodyImpl(envelope, this);
    }

    public SOAPBody createSOAPBody() throws SOAPProcessingException {
        return new SOAP12BodyImpl(this);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
        return new SOAP12BodyImpl(envelope, builder, (SOAPFactory)this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException {
        return new SOAP12FaultCodeImpl(this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultValueImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(OMDataSource source) {
        return new SOAP12HeaderBlockImpl(this, source);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns) throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl(null, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, OMDataSource ds) throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl(localName, ns, this, ds);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultValueImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultSubCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultSubCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultReasonImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException {
        return new SOAP12FaultReasonImpl(this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultReasonImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException {
        return new SOAP12FaultTextImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException {
        return new SOAP12FaultTextImpl(this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultTextImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultNodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException {
        return new SOAP12FaultNodeImpl(this);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultNodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultRoleImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException {
        return new SOAP12FaultRoleImpl(this);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultRoleImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultDetailImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException {
        return new SOAP12FaultDetailImpl(this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP12FaultDetailImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException {
        OMNamespaceImpl ns = new OMNamespaceImpl("http://www.w3.org/2003/05/soap-envelope", "soapenv");
        SOAPEnvelopeImpl env = new SOAPEnvelopeImpl(ns, this);
        this.createSOAPHeader(env);
        this.createSOAPBody(env);
        return env;
    }

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope defaultEnvelope = this.getDefaultEnvelope();
        SOAPFault fault = this.createSOAPFault(defaultEnvelope.getBody());
        SOAPFaultCode faultCode = this.createSOAPFaultCode(fault);
        this.createSOAPFaultValue(faultCode);
        SOAPFaultReason reason = this.createSOAPFaultReason(fault);
        this.createSOAPFaultText(reason);
        this.createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }

    public SOAPMessage createSOAPMessage() {
        return new SOAPMessageImpl(this);
    }

    public SOAPMessage createSOAPMessage(OMXMLParserWrapper builder) {
        return new SOAPMessageImpl(builder, this);
    }

    public SOAPEnvelope createSOAPEnvelope(SOAPMessage message, OMXMLParserWrapper builder) {
        return new SOAPEnvelopeImpl(message, builder, (SOAPFactory)this);
    }
}

