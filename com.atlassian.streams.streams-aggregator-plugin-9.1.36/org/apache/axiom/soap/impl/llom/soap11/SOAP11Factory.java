/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.soap.SOAP11Version;
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
import org.apache.axiom.soap.impl.llom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultRoleImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultTextImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultValueImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderBlockImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11HeaderImpl;

public class SOAP11Factory
extends OMLinkedListImplFactory
implements SOAPFactoryEx {
    public SOAP11Factory(OMLinkedListMetaFactory metaFactory) {
        super(metaFactory);
    }

    public SOAP11Factory() {
    }

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
    }

    public String getSoapVersionURI() {
        return "http://schemas.xmlsoap.org/soap/envelope/";
    }

    public SOAPVersion getSOAPVersion() {
        return SOAP11Version.getSingleton();
    }

    public SOAPEnvelope createSOAPEnvelope() {
        return new SOAPEnvelopeImpl(new OMNamespaceImpl("http://schemas.xmlsoap.org/soap/envelope/", "soapenv"), this);
    }

    public SOAPEnvelope createSOAPEnvelope(OMNamespace ns) {
        return new SOAPEnvelopeImpl(ns, this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP11HeaderImpl(envelope, (SOAPFactory)this);
    }

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException {
        return new SOAP11HeaderImpl(this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
        return new SOAP11HeaderImpl(envelope, builder, (SOAPFactory)this);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl(parent, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns) throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl(null, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(OMDataSource source) {
        return new SOAP11HeaderBlockImpl(this, source);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns, OMDataSource ds) throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl(localName, ns, this, ds);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, SOAPHeader parent, OMXMLParserWrapper builder) throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl(parent, localName, null, builder, this, false);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
        return new SOAP11FaultImpl(parent, e, (SOAPFactory)this);
    }

    public SOAPFault createSOAPFault() throws SOAPProcessingException {
        return new SOAP11FaultImpl(this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException {
        return new SOAP11FaultImpl(parent, (SOAPFactory)this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP11BodyImpl(envelope, this);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope, OMXMLParserWrapper builder) {
        return new SOAP11BodyImpl(envelope, builder, (SOAPFactory)this);
    }

    public SOAPBody createSOAPBody() throws SOAPProcessingException {
        return new SOAP11BodyImpl(this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP11FaultCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException {
        return new SOAP11FaultCodeImpl(this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException {
        return new SOAP11FaultValueImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException {
        return new SOAP11FaultValueImpl(this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultValueImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) throws SOAPProcessingException {
        return new SOAP11FaultValueImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultValueImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) throws SOAPProcessingException {
        return new SOAP11FaultSubCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException {
        return new SOAP11FaultSubCodeImpl(this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultSubCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) throws SOAPProcessingException {
        return new SOAP11FaultSubCodeImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultSubCodeImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP11FaultReasonImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException {
        return new SOAP11FaultReasonImpl(this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultReasonImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException {
        return new SOAP11FaultTextImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException {
        return new SOAP11FaultTextImpl(this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultTextImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent, OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP11FaultRoleImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException {
        return new SOAP11FaultRoleImpl(this);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultRoleImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP11FaultDetailImpl(parent, (SOAPFactory)this);
    }

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException {
        return new SOAP11FaultDetailImpl(this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent, OMXMLParserWrapper builder) {
        return new SOAP11FaultDetailImpl(parent, builder, (SOAPFactory)this);
    }

    public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException {
        OMNamespaceImpl ns = new OMNamespaceImpl("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        SOAPEnvelopeImpl env = new SOAPEnvelopeImpl(ns, this);
        this.createSOAPHeader(env);
        this.createSOAPBody(env);
        return env;
    }

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope defaultEnvelope = this.getDefaultEnvelope();
        SOAPFault fault = this.createSOAPFault(defaultEnvelope.getBody());
        SOAPFaultCode faultCode = this.createSOAPFaultCode(fault);
        SOAPFaultReason reason = this.createSOAPFaultReason(fault);
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

