/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPProcessingException;

public interface SOAPFault
extends OMElement {
    public void setCode(SOAPFaultCode var1) throws SOAPProcessingException;

    public SOAPFaultCode getCode();

    public void setReason(SOAPFaultReason var1) throws SOAPProcessingException;

    public SOAPFaultReason getReason();

    public void setNode(SOAPFaultNode var1) throws SOAPProcessingException;

    public SOAPFaultNode getNode();

    public void setRole(SOAPFaultRole var1) throws SOAPProcessingException;

    public SOAPFaultRole getRole();

    public void setDetail(SOAPFaultDetail var1) throws SOAPProcessingException;

    public SOAPFaultDetail getDetail();

    public Exception getException() throws OMException;

    public void setException(Exception var1) throws OMException;
}

