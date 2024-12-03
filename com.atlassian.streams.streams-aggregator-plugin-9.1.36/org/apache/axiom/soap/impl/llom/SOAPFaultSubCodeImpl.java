/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultSubCodeImpl
extends SOAPElement
implements SOAPFaultSubCode {
    protected SOAPFaultValue value;
    protected SOAPFaultSubCode subCode;

    protected SOAPFaultSubCodeImpl(OMNamespace ns, SOAPFactory factory) {
        super("Subcode", ns, factory);
    }

    protected SOAPFaultSubCodeImpl(OMElement parent, String localName, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, localName, true, factory);
    }

    protected SOAPFaultSubCodeImpl(OMElement parent, String localName, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, localName, builder, factory);
    }

    public void setValue(SOAPFaultValue soapFaultSubCodeValue) throws SOAPProcessingException {
        ElementHelper.setNewElement(this, this.value, soapFaultSubCodeValue);
    }

    public SOAPFaultValue getValue() {
        if (this.value == null) {
            this.value = (SOAPFaultValue)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
        }
        return this.value;
    }

    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        ElementHelper.setNewElement(this, this.subCode, subCode);
    }

    public SOAPFaultSubCode getSubCode() {
        if (this.subCode == null) {
            this.subCode = (SOAPFaultSubCode)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
        }
        return this.subCode;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        if (targetParent instanceof SOAPFaultSubCode) {
            return ((SOAPFactory)this.factory).createSOAPFaultSubCode((SOAPFaultSubCode)targetParent);
        }
        return ((SOAPFactory)this.factory).createSOAPFaultSubCode((SOAPFaultCode)targetParent);
    }
}

