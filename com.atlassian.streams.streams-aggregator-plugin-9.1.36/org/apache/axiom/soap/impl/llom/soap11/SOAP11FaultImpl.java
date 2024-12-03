/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11BodyImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultRoleImpl;

public class SOAP11FaultImpl
extends SOAPFaultImpl {
    public SOAP11FaultImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP11FaultImpl(SOAPBody parent, Exception e, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, e, factory);
    }

    public SOAP11FaultImpl(SOAPBody parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    public SOAP11FaultImpl(SOAPBody parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory);
    }

    protected SOAPFaultDetail getNewSOAPFaultDetail(SOAPFault fault) throws SOAPProcessingException {
        return new SOAP11FaultDetailImpl(fault, (SOAPFactory)this.factory);
    }

    public void setCode(SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        if (!(soapFaultCode instanceof SOAP11FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultCodeImpl, got " + soapFaultCode.getClass());
        }
        super.setCode(soapFaultCode);
    }

    public void setReason(SOAPFaultReason reason) throws SOAPProcessingException {
        if (!(reason instanceof SOAP11FaultReasonImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultReasonImpl, got " + reason.getClass());
        }
        super.setReason(reason);
    }

    public void setNode(SOAPFaultNode node) throws SOAPProcessingException {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    public void setRole(SOAPFaultRole role) throws SOAPProcessingException {
        if (!(role instanceof SOAP11FaultRoleImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultRoleImpl, got " + role.getClass());
        }
        super.setRole(role);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11BodyImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11BodyImpl, got " + parent.getClass());
        }
    }

    public void setDetail(SOAPFaultDetail detail) throws SOAPProcessingException {
        if (!(detail instanceof SOAP11FaultDetailImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultDetailImpl, got " + detail.getClass());
        }
        super.setDetail(detail);
    }

    protected void serializeFaultNode(XMLStreamWriter writer) throws XMLStreamException {
    }

    public SOAPFaultCode getCode() {
        return (SOAPFaultCode)this.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_CODE);
    }

    public SOAPFaultReason getReason() {
        return (SOAPFaultReason)this.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_REASON);
    }

    public SOAPFaultNode getNode() {
        return null;
    }

    public SOAPFaultRole getRole() {
        return (SOAPFaultRole)this.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_ROLE);
    }

    public SOAPFaultDetail getDetail() {
        return (SOAPFaultDetail)this.getFirstChildWithName(SOAP11Constants.QNAME_FAULT_DETAIL);
    }
}

