/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
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

public interface SOAPFactory
extends OMFactory {
    public String getSoapVersionURI();

    public SOAPVersion getSOAPVersion();

    public SOAPMessage createSOAPMessage();

    public SOAPEnvelope createSOAPEnvelope() throws SOAPProcessingException;

    public SOAPEnvelope createSOAPEnvelope(OMNamespace var1);

    public SOAPHeader createSOAPHeader(SOAPEnvelope var1) throws SOAPProcessingException;

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException;

    public SOAPHeaderBlock createSOAPHeaderBlock(String var1, OMNamespace var2, SOAPHeader var3) throws SOAPProcessingException;

    public SOAPHeaderBlock createSOAPHeaderBlock(String var1, OMNamespace var2) throws SOAPProcessingException;

    public SOAPHeaderBlock createSOAPHeaderBlock(OMDataSource var1);

    public SOAPHeaderBlock createSOAPHeaderBlock(String var1, OMNamespace var2, OMDataSource var3) throws SOAPProcessingException;

    public SOAPFault createSOAPFault(SOAPBody var1, Exception var2) throws SOAPProcessingException;

    public SOAPFault createSOAPFault(SOAPBody var1) throws SOAPProcessingException;

    public SOAPFault createSOAPFault() throws SOAPProcessingException;

    public SOAPBody createSOAPBody(SOAPEnvelope var1) throws SOAPProcessingException;

    public SOAPBody createSOAPBody() throws SOAPProcessingException;

    public SOAPFaultCode createSOAPFaultCode(SOAPFault var1) throws SOAPProcessingException;

    public SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException;

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode var1) throws SOAPProcessingException;

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException;

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode var1) throws SOAPProcessingException;

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode var1) throws SOAPProcessingException;

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException;

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode var1) throws SOAPProcessingException;

    public SOAPFaultReason createSOAPFaultReason(SOAPFault var1) throws SOAPProcessingException;

    public SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException;

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason var1) throws SOAPProcessingException;

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException;

    public SOAPFaultNode createSOAPFaultNode(SOAPFault var1) throws SOAPProcessingException;

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException;

    public SOAPFaultRole createSOAPFaultRole(SOAPFault var1) throws SOAPProcessingException;

    public SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException;

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault var1) throws SOAPProcessingException;

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException;

    public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException;

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException;

    public OMNamespace getNamespace();
}

