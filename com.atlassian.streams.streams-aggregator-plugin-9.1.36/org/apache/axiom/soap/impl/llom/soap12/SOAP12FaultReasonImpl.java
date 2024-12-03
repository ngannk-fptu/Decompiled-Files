/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultTextImpl;

public class SOAP12FaultReasonImpl
extends SOAPFaultReasonImpl {
    public SOAP12FaultReasonImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    public SOAP12FaultReasonImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultReasonImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super((OMElement)parent, true, factory);
    }

    public void addSOAPText(SOAPFaultText soapFaultText) throws SOAPProcessingException {
        if (!(soapFaultText instanceof SOAP12FaultTextImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultTextImpl, got " + soapFaultText.getClass());
        }
        this.addChild(soapFaultText);
    }

    public SOAPFaultText getFirstSOAPText() {
        return (SOAPFaultText)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_TEXT);
    }

    public String getText() {
        return this.getFirstSOAPText().getText();
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl, got " + parent.getClass());
        }
    }
}

