/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
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
import org.apache.axiom.soap.impl.llom.soap12.SOAP12BodyImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultNodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultRoleImpl;

public class SOAP12FaultImpl
extends SOAPFaultImpl {
    public SOAP12FaultImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultImpl(SOAPBody parent, Exception e, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, e, factory);
    }

    public SOAP12FaultImpl(SOAPBody parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    public SOAP12FaultImpl(SOAPBody parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory);
    }

    protected SOAPFaultDetail getNewSOAPFaultDetail(SOAPFault fault) {
        return new SOAP12FaultDetailImpl(fault, (SOAPFactory)this.factory);
    }

    public void setCode(SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        if (!(soapFaultCode instanceof SOAP12FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultCodeImpl, got " + soapFaultCode.getClass());
        }
        super.setCode(soapFaultCode);
    }

    public void setReason(SOAPFaultReason reason) throws SOAPProcessingException {
        if (!(reason instanceof SOAP12FaultReasonImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultReasonImpl, got " + reason.getClass());
        }
        super.setReason(reason);
    }

    public void setNode(SOAPFaultNode node) throws SOAPProcessingException {
        if (!(node instanceof SOAP12FaultNodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultNodeImpl, got " + node.getClass());
        }
        super.setNode(node);
    }

    public void setRole(SOAPFaultRole role) throws SOAPProcessingException {
        if (!(role instanceof SOAP12FaultRoleImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultRoleImpl, got " + role.getClass());
        }
        super.setRole(role);
    }

    public void setDetail(SOAPFaultDetail detail) throws SOAPProcessingException {
        if (!(detail instanceof SOAP12FaultDetailImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultDetailImpl, got " + detail.getClass());
        }
        super.setDetail(detail);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12BodyImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12BodyImpl, got " + parent.getClass());
        }
    }

    protected void serializeFaultNode(XMLStreamWriter writer) throws XMLStreamException {
        SOAPFaultNode faultNode = this.getNode();
        if (faultNode != null && faultNode.getText() != null && !"".equals(faultNode.getText())) {
            ((SOAP12FaultNodeImpl)faultNode).internalSerialize(writer, true);
        }
    }

    public SOAPFaultNode getNode() {
        return (SOAPFaultNode)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_NODE);
    }

    public SOAPFaultCode getCode() {
        return (SOAPFaultCode)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_CODE);
    }

    public SOAPFaultReason getReason() {
        return (SOAPFaultReason)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_REASON);
    }

    public SOAPFaultRole getRole() {
        return (SOAPFaultRole)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_ROLE);
    }

    public SOAPFaultDetail getDetail() {
        return (SOAPFaultDetail)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_DETAIL);
    }
}

