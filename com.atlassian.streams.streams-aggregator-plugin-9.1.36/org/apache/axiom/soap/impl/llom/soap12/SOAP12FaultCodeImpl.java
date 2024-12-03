/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultValueImpl;

public class SOAP12FaultCodeImpl
extends SOAPFaultCodeImpl {
    public SOAP12FaultCodeImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultCodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    public SOAP12FaultCodeImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, true, factory);
    }

    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        if (!(subCode instanceof SOAP12FaultSubCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultSubCodeImpl, got " + subCode.getClass());
        }
        super.setSubCode(subCode);
    }

    public void setValue(SOAPFaultValue value) throws SOAPProcessingException {
        if (!(value instanceof SOAP12FaultValueImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultValueImpl, got " + value.getClass());
        }
        super.setValue(value);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl as parent, got " + parent.getClass());
        }
    }

    public QName getTextAsQName() {
        return this.getValueAsQName();
    }

    public SOAPFaultValue getValue() {
        return (SOAPFaultValue)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }

    public SOAPFaultSubCode getSubCode() {
        return (SOAPFaultSubCode)this.getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }

    public void setValue(QName value) {
        SOAPFaultValue valueElement = this.getValue();
        if (valueElement == null) {
            valueElement = ((SOAPFactory)this.getOMFactory()).createSOAPFaultValue(this);
        }
        valueElement.setText(value);
    }

    public QName getValueAsQName() {
        SOAPFaultValue value = this.getValue();
        return value == null ? null : value.getTextAsQName();
    }
}

